//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.22 at 09:56:33 p. m. CEST 
//


package org.xmlsoap.schemas.wsdl.http;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.xmlsoap.schemas.wsdl.http package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Binding_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/http/", "binding");
    private final static QName _Operation_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/http/", "operation");
    private final static QName _Address_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/http/", "address");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.xmlsoap.schemas.wsdl.http
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BindingType }
     * 
     */
    public BindingType createBindingType() {
        return new BindingType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link UrlEncoded }
     * 
     */
    public UrlEncoded createUrlEncoded() {
        return new UrlEncoded();
    }

    /**
     * Create an instance of {@link OperationType }
     * 
     */
    public OperationType createOperationType() {
        return new OperationType();
    }

    /**
     * Create an instance of {@link UrlReplacement }
     * 
     */
    public UrlReplacement createUrlReplacement() {
        return new UrlReplacement();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BindingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/wsdl/http/", name = "binding")
    public JAXBElement<BindingType> createBinding(BindingType value) {
        return new JAXBElement<BindingType>(_Binding_QNAME, BindingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/wsdl/http/", name = "operation")
    public JAXBElement<OperationType> createOperation(OperationType value) {
        return new JAXBElement<OperationType>(_Operation_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/wsdl/http/", name = "address")
    public JAXBElement<AddressType> createAddress(AddressType value) {
        return new JAXBElement<AddressType>(_Address_QNAME, AddressType.class, null, value);
    }

}