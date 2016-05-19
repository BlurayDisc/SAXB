package com.run.saxb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <tt>Unmarshaller</tt> class governs the process of deserializing XML
 * data into newly created Java content trees, optionally validating the XML
 * data as it is unmarshalled.  It provides an overloading of unmarshal methods
 * for many different input kinds.
 *
 * <p>
 * Unmarshalling from a File:
 * <blockquote>
 *    <pre>
 *       JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *       Unmarshaller u = jc.createUnmarshaller();
 *       Object o = u.unmarshal( new File( "nosferatu.xml" ) );
 *    </pre>
 * </blockquote>
 *
 *
 * <p>
 * Unmarshalling from an InputStream:
 * <blockquote>
 *    <pre>
 *       InputStream is = new FileInputStream( "nosferatu.xml" );
 *       JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *       Unmarshaller u = jc.createUnmarshaller();
 *       Object o = u.unmarshal( is );
 *    </pre>
 * </blockquote>
 * 
 * <p>
 * Unmarshalling from a StringBuffer using a
 * <tt>javax.xml.transform.stream.StreamSource</tt>:
 * <blockquote>
 *    <pre>
 *       JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *       Unmarshaller u = jc.createUnmarshaller();
 *       StringBuffer xmlStr = new StringBuffer( "&lt;?xml version=&quot;1.0&quot;?&gt;..." );
 *       Object o = u.unmarshal( new StreamSource( new StringReader( xmlStr.toString() ) ) );
 *    </pre>
 * </blockquote>
 *
 * <p>
 * Unmarshalling from a <tt>org.w3c.dom.Node</tt>:
 * <blockquote>
 *    <pre>
 *       JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *       Unmarshaller u = jc.createUnmarshaller();
 *
 *       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 *       dbf.setNamespaceAware(true);
 *       DocumentBuilder db = dbf.newDocumentBuilder();
 *       Document doc = db.parse(new File( "nosferatu.xml"));

 *       Object o = u.unmarshal( doc );
 *    </pre>
 * </blockquote>
 * 
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see JAXBContext
 * @see Marshaller
 * @since JAXB1.0
 */
public class Unmarshaller {

	private SAXBContextImpl grammar;
	private DocumentBuilder parser;
	
	public Unmarshaller(SAXBContextImpl context) {
		this.grammar = context;
		try {
			this.parser = grammar.dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) { }
	}
	
    /**
     * Unmarshal XML data from the specified file and return the resulting
     * content tree.
     *
     * <p>
     * Implements <a href="#unmarshalGlobal">Unmarshal Global Root Element</a>.
     *
     * @param f the file to unmarshal XML data from
     * @return the newly created root object of the java content tree
     *
     * @throws JAXBException
     *     If any unexpected errors occur while unmarshalling
     */
    public Object unmarshal(File f) throws JAXBException {
    	URL url;
        try {
            // copied from JAXP
            String path = f.getAbsolutePath();
            if (File.separatorChar != '/')
                path = path.replace(File.separatorChar, '/');
            if (!path.startsWith("/"))
                path = "/" + path;
            if (!path.endsWith("/") && f.isDirectory())
                path = path + "/";
            url = new URL("file", "", path);
        } catch( MalformedURLException e ) {
            throw new IllegalArgumentException(e.getMessage());
        }
    	
        return unmarshal0(new InputSource(url.toExternalForm()));
    }

    /**
     * Unmarshal XML data from the specified InputStream and return the
     * resulting content tree.  Validation event location information may
     * be incomplete when using this form of the unmarshal API.
     *
     * <p>
     * Implements <a href="#unmarshalGlobal">Unmarshal Global Root Element</a>.
     *
     * @param is the InputStream to unmarshal XML data from
     * @return the newly created root object of the java content tree
     *
     * @throws JAXBException
     *     If any unexpected errors occur while unmarshalling
     */
    public Object unmarshal(InputStream is) throws JAXBException {
    	return unmarshal0(new InputSource(is));
    }
    
    /**
     * Unmarshal XML data from the specified Reader and return the
     * resulting content tree.  Validation event location information may
     * be incomplete when using this form of the unmarshal API,
     * because a Reader does not provide the system ID.
     *
     * <p>
     * Implements <a href="#unmarshalGlobal">Unmarshal Global Root Element</a>.
     *
     * @param reader the Reader to unmarshal XML data from
     * @return the newly created root object of the java content tree
     *
     * @throws JAXBException
     *     If any unexpected errors occur while unmarshalling
     * @since JAXB2.0
     */
    public Object unmarshal(Reader reader) throws JAXBException {
    	return unmarshal0(new InputSource(reader));
    }

    /**
     * Unmarshal global XML data from the specified DOM tree and return the resulting
     * content tree.
     *
     * <p>
     * Implements <a href="#unmarshalGlobal">Unmarshal Global Root Element</a>.
     *
     * @param node
     *      the document/element to unmarshal XML data from.
     *      The caller must support at least Document and Element.
     * @return the newly created root object of the java content tree
     *
     * @throws JAXBException
     *     If any unexpected errors occur while unmarshalling
     * @see #unmarshal(org.w3c.dom.Node, Class)
     */
    public Object unmarshal(Node node) throws JAXBException {
    	return unmarshalRoot(node);
    }
    
    private Object unmarshal0(InputSource is) throws JAXBException  {
    	try {
    		Document doc =  parser.parse(is);
    		return unmarshalRoot(doc);
    	} catch (IOException | SAXException e) {
    		throw new JAXBException(e);
    	}
    }
    
    private Object unmarshalRoot(Node node) {
    	Element e;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
        	e = (Element) node;
        } else if(node.getNodeType() == Node.DOCUMENT_NODE) {
        	e = ((Document) node).getDocumentElement();
        } else {
            throw new IllegalArgumentException("Unexpected node type: " + node);
        }
        
        QName qname = grammar.createQName(e);
        BeanInfo<?> bean = grammar.selectRoot(qname);
        Object saxbElement = bean.createInstance();
        
    	System.out.println(saxbElement);
    	
    	NodeList children = e.getChildNodes();
    	for (int i = 0; i < children.getLength(); i++) {
    		Node child = children.item(i);
    		if (child.getNodeType() == Node.ELEMENT_NODE) {
    			
    		}
    	}
		return saxbElement;
    }
    
    static final QName createQName(Element e) {
    	return new QName(e.getNamespaceURI(), e.getLocalName(), getPrefix(e));
    }
    
    /**
     * Gets the prefix. This is slow.
     *
     * @return can be "" but never null.
     */
    public static final String getPrefix(Element e) {
    	String qname = e.getTagName();
        int idx = qname.indexOf(':');
        if (idx < 0) return "";
        else         return qname.substring(0, idx);
    }
}