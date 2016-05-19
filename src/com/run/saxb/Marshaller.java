package com.run.saxb;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

/**
 * <p>
 * The <tt>Marshaller</tt> class is responsible for governing the process
 * of serializing Java content trees back into XML data.  It provides the basic
 * marshalling methods:
 *
 * <p>
 * <i>Assume the following setup code for all following code fragments:</i>
 * <blockquote>
 *    <pre>
 *       JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *       Unmarshaller u = jc.createUnmarshaller();
 *       Object element = u.unmarshal( new File( "foo.xml" ) );
 *       Marshaller m = jc.createMarshaller();
 *    </pre>
 * </blockquote>
 *
 * <p>
 * Marshalling to a File:
 * <blockquote>
 *    <pre>
 *       OutputStream os = new FileOutputStream( "nosferatu.xml" );
 *       m.marshal( element, os );
 *    </pre>
 * </blockquote>
 *
 * <p>
 * Marshalling to a DOM Node:
 * <blockquote>
 *    <pre>
 *       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 *       dbf.setNamespaceAware(true);
 *       DocumentBuilder db = dbf.newDocumentBuilder();
 *       Document doc = db.newDocument();
 *
 *       m.marshal( element, doc );
 *    </pre>
 * </blockquote>
 *
 * <p>
 * Marshalling to a java.io.OutputStream:
 * <blockquote>
 *    <pre>
 *       m.marshal( element, System.out );
 *    </pre>
 * </blockquote>
 *
 * <p>
 * Marshalling to a java.io.Writer:
 * <blockquote>
 *    <pre>
 *       m.marshal( element, new PrintWriter( System.out ) );
 *    </pre>
 * </blockquote>
 * 
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Ryan Shoemaker, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see JAXBContext
 * @see Unmarshaller
 * @since JAXB1.0
 */
public class Marshaller {
	
	private SAXBContextImpl grammar;
	private Transformer serializer;
	
	public Marshaller(SAXBContextImpl context) {
		this.grammar = context;
		try {
			this.serializer = grammar.tff.newTransformer();
		} catch (TransformerConfigurationException e) { }
	}
	
    /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into an output stream.
     *
     * @param jaxbElement
     *      The root of content tree to be marshalled.
     * @param os
     *      XML will be added to this stream.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     *      <p>If the <tt>Marshaller</tt> is unable to marshal <tt>obj</tt> 
     *      (or any object reachable from <tt>obj</tt>).  
     *      See <a href="#elementMarshalling">Marshalling a JAXB element</a>.
     * @throws IllegalArgumentException
     *      If any of the method parameters are null
     */
    public void marshal(Object jaxbElement, OutputStream os)
        throws JAXBException {
    	marhsal0(jaxbElement, new StreamResult(os));
    }

    /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into a file.
     *
     * @param jaxbElement
     *      The root of content tree to be marshalled.
     * @param output
     *      File to be written. If this file already exists, it will be overwritten.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     *      
     * @since JAXB2.1
     */
    public void marshal(Object jaxbElement, File output)
        throws JAXBException {
    	marhsal0(jaxbElement, new StreamResult(output));
    }

    /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into a Writer.
     *
     * @param jaxbElement
     *      The root of content tree to be marshalled.
     * @param writer
     *      XML will be sent to this writer.
     *
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     */
    public void marshal(Object jaxbElement, Writer writer)
        throws JAXBException {
    	marhsal0(jaxbElement, new StreamResult(writer));
    }
    
    /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into a DOM tree.
     *
     * @param jaxbElement
     *      The content tree to be marshalled.
     * @param node
     *      DOM nodes will be added as children of this node.
     *      This parameter must be a Node that accepts children
     *      ({@link org.w3c.dom.Document},
     *      {@link  org.w3c.dom.DocumentFragment}, or
     *      {@link  org.w3c.dom.Element})
     *
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
 	 *		<p> If the <tt>Marshaller</tt> is unable to marshal <tt>jaxbElement</tt> 
 	 *		(or any object reachable from <tt>jaxbElement</tt>).  
 	 *		See <a href="#elementMarshalling">Marshalling a JAXB element</a>.
     * @throws IllegalArgumentException
     *      If any of the method parameters are null
     */
    public void marshal(Object jaxbElement, Node node)
        throws JAXBException {
    	marhsal0(jaxbElement, new DOMResult(node));
    }
    
    private void marhsal0(Object object, Result outputTarget) throws JAXBException {
    	
    	Document document = new DocumentImpl();
    	
    	BeanInfo<?> beanInfo = grammar.getBeanInfo(object);
    	beanInfo.serializeRoot(document);

    	try {
    		serializer.transform(new DOMSource(document), outputTarget);
    	} catch (TransformerException e) {
    		throw new JAXBException(e);
    	}
    }
}