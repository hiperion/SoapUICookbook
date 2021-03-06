/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.jaxrs.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

public class HttpHeadersImpl implements HttpHeaders {

    private static final String HEADER_SPLIT_PROPERTY =
        "org.apache.cxf.http.header.split";
    private static final String COOKIE_SEPARATOR_PROPERTY =
        "org.apache.cxf.http.cookie.separator";
    private static final String COOKIE_SEPARATOR_CRLF = "crlf";
    private static final String DEFAULT_SEPARATOR = ",";
    private static final String DEFAULT_COOKIE_SEPARATOR = ";";
    
    private static final String COMPLEX_HEADER_EXPRESSION = 
        "(([\\w]+=\"[^\"]*\")|([\\w]+=[\\w]+)|([\\w]+))(;(([\\w]+=\"[^\"]*\")|([\\w]+=[\\w]+)|([\\w]+)))?";
    private static final Pattern COMPLEX_HEADER_PATTERN =
        Pattern.compile(COMPLEX_HEADER_EXPRESSION);
    private static final String QUOTE = "\"";
    private static final Set<String> HEADERS_WITH_POSSIBLE_QUOTES;
    static {
        HEADERS_WITH_POSSIBLE_QUOTES = new HashSet<String>();
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.CONTENT_TYPE);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.CACHE_CONTROL);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.ETAG);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.IF_MATCH);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.IF_NONE_MATCH);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.COOKIE);
        HEADERS_WITH_POSSIBLE_QUOTES.add(HttpHeaders.SET_COOKIE);
    }
    
    
    private Message message;
    private Map<String, List<String>> headers;
    public HttpHeadersImpl(Message message) {
        this.message = message;
        this.headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
        if (headers == null) {
            headers = Collections.emptyMap();
        }
    }
    
    public List<MediaType> getAcceptableMediaTypes() {
        List<String> lValues = headers.get(HttpHeaders.ACCEPT);
        if (lValues == null || lValues.isEmpty() || lValues.get(0) == null) {
            return Collections.singletonList(MediaType.WILDCARD_TYPE);
        }
        List<MediaType> mediaTypes = new LinkedList<MediaType>();
        for (String value : lValues) {
            mediaTypes.addAll(JAXRSUtils.parseMediaTypes(value));
        }
        sortMediaTypesUsingQualityFactor(mediaTypes); 
        return mediaTypes;
    }

    public Map<String, Cookie> getCookies() {
        List<String> values = headers.get(HttpHeaders.COOKIE);
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, Cookie> cl = new HashMap<String, Cookie>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            List<String> cs = value.contains("$")
                ? Collections.singletonList(value)
                : getHeaderValues(HttpHeaders.COOKIE, value, getCookieSeparator());
            for (String c : cs) {
                Cookie cookie = Cookie.valueOf(c);
                cl.put(cookie.getName(), cookie);
            }
        }
        return cl;
    }

    private String getCookieSeparator() {
        Object cookiePropValue = message.getContextualProperty(COOKIE_SEPARATOR_PROPERTY);
        if (cookiePropValue != null) {
            return COOKIE_SEPARATOR_CRLF.equals(cookiePropValue.toString()) 
                ? "\r\n" : cookiePropValue.toString();
        } else {
            return DEFAULT_COOKIE_SEPARATOR;
        }
    }
    
    public Locale getLanguage() {
        List<String> values = getListValues(HttpHeaders.CONTENT_LANGUAGE);
        return values.size() == 0 ? null : HttpUtils.getLocale(values.get(0).trim());
    }

    public MediaType getMediaType() {
        List<String> values = getListValues(HttpHeaders.CONTENT_TYPE);
        return values.size() == 0 ? null : JAXRSUtils.toMediaType(values.get(0));
    }

    public MultivaluedMap<String, String> getRequestHeaders() {
        boolean splitIndividualValue 
            = MessageUtils.isTrue(message.getContextualProperty(HEADER_SPLIT_PROPERTY));
        if (splitIndividualValue) {
            Map<String, List<String>> newHeaders = 
                new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                newHeaders.put(entry.getKey(), getRequestHeader(entry.getKey()));
            }
            return new MetadataMap<String, String>(Collections.unmodifiableMap(newHeaders), false);
        } else {
            return new MetadataMap<String, String>(Collections.unmodifiableMap(headers), false);
        }
    }

    public List<Locale> getAcceptableLanguages() {
        List<String> ls = getListValues(HttpHeaders.ACCEPT_LANGUAGE);
        if (ls.isEmpty()) {
            return Collections.singletonList(new Locale("*"));
        }
        
        List<Locale> newLs = new ArrayList<Locale>(); 
        Map<Locale, Float> prefs = new HashMap<Locale, Float>();
        for (String l : ls) {
            String[] pair = StringUtils.split(l, ";");
            
            Locale locale = HttpUtils.getLocale(pair[0].trim());
            
            newLs.add(locale);
            if (pair.length > 1) {
                String[] pair2 = StringUtils.split(pair[1], "=");
                if (pair2.length > 1) {
                    prefs.put(locale, JAXRSUtils.getMediaTypeQualityFactor(pair2[1].trim()));
                } else {
                    prefs.put(locale, 1F);
                }
            } else {
                prefs.put(locale, 1F);
            }
        }
        if (newLs.size() <= 1) {
            return newLs;
        }
        
        Collections.sort(newLs, new AcceptLanguageComparator(prefs));
        return newLs;
        
    }

    public List<String> getRequestHeader(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return Collections.emptyList();
        }
        boolean splitIndividualValue 
            = MessageUtils.isTrue(message.getContextualProperty(HEADER_SPLIT_PROPERTY));
        if (!splitIndividualValue
            || HttpUtils.isDateRelatedHeader(name)) {
            return values;
        }
        List<String> ls = new LinkedList<String>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String sep = HttpHeaders.COOKIE.equalsIgnoreCase(name) ? getCookieSeparator() : DEFAULT_SEPARATOR;
            ls.addAll(getHeaderValues(name, value, sep));
        }
        return ls;
    }

    private List<String> getListValues(String headerName) {
        List<String> values = headers.get(headerName);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return Collections.emptyList();
        }
        if (HttpUtils.isDateRelatedHeader(headerName)) {
            return values;
        }
        List<String> actualValues = new LinkedList<String>();
        for (String v : values) {
            actualValues.addAll(getHeaderValues(headerName, v));
        }
        return actualValues;
    }
    
    private List<String> getHeaderValues(String headerName, String originalValue) {
        return getHeaderValues(headerName, originalValue, DEFAULT_SEPARATOR);
    }
    
    private List<String> getHeaderValues(String headerName, String originalValue, String sep) {
        if (!originalValue.contains(QUOTE)
            || HEADERS_WITH_POSSIBLE_QUOTES.contains(headerName)) {
            String[] ls = StringUtils.split(originalValue, sep);
            if (ls.length == 1) {
                return Collections.singletonList(ls[0].trim());
            } else {
                List<String> newValues = new ArrayList<String>();
                for (String v : ls) {
                    newValues.add(v.trim());
                }
                return newValues;
            }
        }
        if (originalValue.startsWith("\"") && originalValue.endsWith("\"")) {
            String actualValue = originalValue.length() == 2 ? "" 
                : originalValue.substring(1, originalValue.length() - 1);
            return Collections.singletonList(actualValue);
        }
        List<String> values = new ArrayList<String>(4);
        Matcher m = COMPLEX_HEADER_PATTERN.matcher(originalValue);
        while (m.find()) {
            String val = m.group().trim();
            if (val.length() > 0) {
                values.add(val);
            }
        }
        return values;
    }
    
    private static class AcceptLanguageComparator implements Comparator<Locale> {
        private Map<Locale, Float> prefs;
        
        public AcceptLanguageComparator(Map<Locale, Float> prefs) {
            this.prefs = prefs;
        }

        public int compare(Locale lang1, Locale lang2) {
            float p1 = prefs.get(lang1);
            float p2 = prefs.get(lang2);
            return Float.compare(p1, p2) * -1;
        }
    }
    
    private void sortMediaTypesUsingQualityFactor(List<MediaType> types) {
        if (types.size() > 1) {
            Collections.sort(types, new Comparator<MediaType>() {

                public int compare(MediaType mt1, MediaType mt2) {
                    return JAXRSUtils.compareMediaTypesQualityFactors(mt1, mt2);
                }
                
            });
        }
    }

    public Date getDate() {
        List<String> values = headers.get(HttpHeaders.DATE);
        if (values == null || StringUtils.isEmpty(values.get(0))) {
            return null;
        }
        return HttpUtils.getHttpDate(values.get(0));
    }

    public String getHeaderString(String headerName) {
        return HttpUtils.getHeaderString(headers.get(headerName));
    }

    public int getLength() {
        List<String> values = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (values == null || values.size() != 1) {
            return -1;
        }
        return HttpUtils.getContentLength(values.get(0));
    }
    
}
