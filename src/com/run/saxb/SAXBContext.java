package com.run.saxb;

import javax.xml.bind.JAXBException;

/**
 * EJAXB Stands for "The Simple Architecture for XML Binding".
 * @author RuN
 *
 */
public abstract class SAXBContext {
	
    /**
     * <p>
     * Obtain a new instance of a <tt>SAXBContext</tt> class.
     *
     * <p>
     * This is a convenience method to invoke the
     * {@link #newInstance(String,ClassLoader)} method with
     * the context class loader of the current thread.
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>SAXBContext</tt> such as
     * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     * </ol>
     */
    public static SAXBContext newInstance(String contextPath)
        throws JAXBException {
        return ContextFinder.find(contextPath);
    }
    
    /**
     * Create an <tt>Unmarshaller</tt> object that can be used to convert XML
     * data into a java content tree.
     *
     * @return an <tt>Unmarshaller</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Unmarshaller</tt> object
     */
    public abstract Unmarshaller createUnmarshaller() throws JAXBException;


    /**
     * Create a <tt>Marshaller</tt> object that can be used to convert a
     * java content tree into XML data.
     *
     * @return a <tt>Marshaller</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Marshaller</tt> object
     */
    public abstract Marshaller createMarshaller() throws JAXBException;
}