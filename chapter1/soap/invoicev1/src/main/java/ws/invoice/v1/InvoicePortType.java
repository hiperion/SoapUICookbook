package ws.invoice.v1;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Get An Invoice Using It's Invoice Number
 * 
 *
 * This class was generated by Apache CXF 3.0.1 2015-01-09T17:15:30.732Z
 * Generated source version: 3.0.1
 * 
 */
@WebService(targetNamespace = "http://soapui.cookbook.samples/contract/invoice", name = "InvoicePortType")
@XmlSeeAlso({ ObjectFactory.class })
public interface InvoicePortType {

	@RequestWrapper(localName = "getInvoice", targetNamespace = "http://soapui.cookbook.samples/schema/invoice", className = "ws.invoice.v1.InvoiceRefType")
	@WebMethod(action = "http://soapui.cookbook.samples/getInvoiceRequest/request")
	@ResponseWrapper(localName = "InvoiceDocument", targetNamespace = "http://soapui.cookbook.samples/schema/invoice", className = "ws.invoice.v1.InvoiceDocumentType")
	public void getInvoice(
			@WebParam(mode = WebParam.Mode.INOUT, name = "invoiceNo", targetNamespace = "http://soapui.cookbook.samples/schema/invoice") javax.xml.ws.Holder<java.lang.String> invoiceNo,
			@WebParam(mode = WebParam.Mode.OUT, name = "company", targetNamespace = "http://soapui.cookbook.samples/schema/invoice") javax.xml.ws.Holder<java.lang.String> company,
			@WebParam(mode = WebParam.Mode.OUT, name = "amount", targetNamespace = "http://soapui.cookbook.samples/schema/invoice") javax.xml.ws.Holder<java.lang.Double> amount);
}
