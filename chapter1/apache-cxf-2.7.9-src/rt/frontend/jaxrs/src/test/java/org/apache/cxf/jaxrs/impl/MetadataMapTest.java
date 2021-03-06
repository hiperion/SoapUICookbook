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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Assert;
import org.junit.Test;



public class MetadataMapTest extends Assert {
    
    @Test
    public void testPutSingle() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        List<Object> value1 = new ArrayList<Object>();
        value1.add("bar");
        value1.add("foo");
        m.put("baz", value1);
        
        m.putSingle("baz", "clazz");
        List<Object> value2 = m.get("baz");
        assertEquals("Only a single value should be in the list", 1, value2.size());
        assertEquals("Value is wrong", "clazz", value2.get(0));
        assertNull(m.get("baZ"));
    }
    
    @Test
    public void testAddFirst() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.addFirst("baz", "foo");
        List<Object> values = m.get("baz");
        assertEquals(1, values.size());
        assertEquals("foo", values.get(0));
        
        m.addFirst("baz", "clazz");
        values = m.get("baz");
        assertEquals(2, values.size());
        assertEquals("clazz", values.get(0));
        assertEquals("foo", values.get(1));
    }
    
    @Test
    public void testAddFirstUnmodifiableListFirst() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.put("baz", Arrays.<Object>asList("foo"));
        List<Object> values = m.get("baz");
        assertEquals(1, values.size());
        assertEquals("foo", values.get(0));
        
        m.addFirst("baz", "clazz");
        values = m.get("baz");
        assertEquals(2, values.size());
        assertEquals("clazz", values.get(0));
        assertEquals("foo", values.get(1));
    }
    
    @Test
    public void testAddAll() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        List<Object> values = new ArrayList<Object>();
        values.add("foo");
        m.addAll("baz", values);
        values = m.get("baz");
        assertEquals(1, values.size());
        assertEquals("foo", values.get(0));
        
        m.addAll("baz", Collections.<Object>singletonList("foo2"));
        values = m.get("baz");
        assertEquals(2, values.size());
        assertEquals("foo", values.get(0));
        assertEquals("foo2", values.get(1));
    }
    
    @Test
    public void testPutSingleCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>(false, true);
        List<Object> value1 = new ArrayList<Object>();
        value1.add("bar");
        value1.add("foo");
        m.put("baz", value1);
        
        m.putSingle("baz", "clazz");
        assertEquals(1, m.size());
        
        List<Object> value2 = m.get("baz");
        assertEquals("Only a single value should be in the list", 1, value2.size());
        assertEquals("Value is wrong", "clazz", value2.get(0));
        
        m.putSingle("Baz", "clazz2");
        assertEquals(1, m.size());
        value2 = m.get("baz");
        assertEquals("Only a single value should be in the list", 1, value2.size());
        assertEquals("Value is wrong", "clazz2", value2.get(0));
   
        assertTrue(m.containsKey("Baz"));
        assertTrue(m.containsKey("baz"));
    }
    
    @Test
    public void testContainsKeyCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>(false, true);
        m.putSingle("a", "b");
        assertTrue(m.containsKey("a"));
        assertTrue(m.containsKey("A"));
    }
    
    @Test
    public void testContainsKeyCaseSensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.putSingle("a", "b");
        assertTrue(m.containsKey("a"));
        assertFalse(m.containsKey("A"));
    }
    
    
    @Test
    public void testKeySetCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>(false, true);
        m.putSingle("a", "b");
        assertTrue(m.keySet().contains("a"));
        assertTrue(m.keySet().contains("A"));
    }
    
    @Test
    public void testKeySetCaseSensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.putSingle("a", "b");
        assertTrue(m.keySet().contains("a"));
        assertFalse(m.keySet().contains("A"));
    }
    
    @Test
    public void testPutAllCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>(false, true);
        List<Object> value1 = new ArrayList<Object>();
        value1.add("bar");
        value1.add("foo");
        m.put("baz", value1);
        assertEquals(1, m.size());
        List<Object> values = m.get("baz");
        assertEquals(2, values.size());
        assertEquals("bar", values.get(0));
        assertEquals("foo", values.get(1));
        
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(false, true);
        List<Object> value2 = new ArrayList<Object>();
        value2.add("bar2");
        value2.add("foo2");
        m2.put("BaZ", value2);
        
        m.putAll(m2);
        
        assertEquals(1, m.size());
        values = m.get("Baz");
        assertEquals(2, values.size());
        assertEquals("bar2", values.get(0));
        assertEquals("foo2", values.get(1));
    }
    
    @Test
    public void testRemoveCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>(false, true);
        List<Object> value1 = new ArrayList<Object>();
        value1.add("bar");
        value1.add("foo");
        m.put("baz", value1);
        
        m.putSingle("baz", "clazz");
        assertEquals(1, m.size());
        
        m.remove("Baz");
        assertEquals(0, m.size());
    }
    
    @Test
    public void testAddAndGetFirst() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("baz", "bar");
        
        List<Object> value = m.get("baz");
        assertEquals("Only a single value should be in the list", 1, value.size());
        assertEquals("Value is wrong", "bar", value.get(0));
        
        m.add("baz", "foo");
        
        value = m.get("baz");
        assertEquals("Two values should be in the list", 2, value.size());
        assertEquals("Value1 is wrong", "bar", value.get(0));
        assertEquals("Value2 is wrong", "foo", value.get(1));
        
        assertEquals("GetFirst value is wrong", "bar", m.getFirst("baz"));
    }
    
    @Test
    public void testCopyAndUpdate() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("baz", "bar");
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(m);
        m.remove("baz");
        m.add("baz", "foo");
        assertEquals("bar", m2.getFirst("baz"));
        assertEquals("foo", m.getFirst("baz"));
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyRemove() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("baz", "bar");
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(m, true, false);
        m2.remove("baz");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyAdd() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("baz", "bar");
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(m, true, false);
        m2.add("bar", "foo");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyAddFirst() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("baz", "bar");
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(m, true, false);
        m2.addFirst("baz", "bar2");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyAdd2() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();
        List<String> list = new LinkedList<String>();
        list.add("bar");
        values.put("baz", list);
        MultivaluedMap<String, String> map = 
            new MetadataMap<String, String>(values, false, true, true);
        map.add("baz", "baz");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyAddFirst2() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();
        List<String> list = new LinkedList<String>();
        list.add("bar");
        values.put("baz", list);
        MultivaluedMap<String, String> map = 
            new MetadataMap<String, String>(values, false, true, true);
        map.addFirst("baz", "bar2");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testReadOnlyPutSingle() {
        Map<String, List<String>> values = new HashMap<String, List<String>>();
        MultivaluedMap<String, String> map = 
            new MetadataMap<String, String>(values, false, true, true);
        map.putSingle("baz", "baz");
    }
    
    @Test
    public void testGetCaseInsensitive() {
        MetadataMap<String, Object> m = new MetadataMap<String, Object>();
        m.add("Baz", "bar");
        MetadataMap<String, Object> m2 = new MetadataMap<String, Object>(m, true, true);
        assertEquals("bar", m2.getFirst("baZ"));
        assertEquals("bar", m2.getFirst("Baz"));
        assertTrue(m2.containsKey("BaZ"));
        assertTrue(m2.containsKey("Baz"));
        List<Object> values = m2.get("baz");
        assertEquals(1, values.size());
        assertEquals("bar", values.get(0).toString());
    }
    
    @Test
    public void testCompareIgnoreValueOrder() {
        MetadataMap<String, String> m = new MetadataMap<String, String>();
        m.add("baz", "bar1");
        m.add("baz", "bar2");
        List<String> values = m.get("baz");
        assertEquals("bar1", values.get(0));
        assertEquals("bar2", values.get(1));
        
        MetadataMap<String, String> m2 = new MetadataMap<String, String>();
        m2.add("baz", "bar2");
        m2.add("baz", "bar1");
        values = m2.get("baz");
        assertEquals("bar2", values.get(0));
        assertEquals("bar1", values.get(1));
        
        assertTrue(m.equalsIgnoreValueOrder(m2));
        assertTrue(m.equalsIgnoreValueOrder(m));
        assertTrue(m2.equalsIgnoreValueOrder(m));
        
        MetadataMap<String, String> m3 = new MetadataMap<String, String>();
        m3.add("baz", "bar1");
        assertFalse(m.equalsIgnoreValueOrder(m3));
        assertFalse(m2.equalsIgnoreValueOrder(m3));
    }
    
}
