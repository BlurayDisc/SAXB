package com.run.saxb;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Element;

public class SAXBContextImpl extends SAXBContext {
	
	protected final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	protected final TransformerFactory tff = TransformerFactory.newInstance();
	
	final Map<QName, BeanInfo<?>> rootMap = new LinkedHashMap<>();
	
    /**
     * Map from JAXB-bound {@link Class} to its {@link BeanInfo}.
     */
    final Map<Class<?>, BeanInfo<?>> beanInfoMap = new LinkedHashMap<>();
    
    private final Class<?>[] classes;
    
    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * This is a convenience method to invoke the
     * {@link #newInstance(String,ClassLoader)} method with
     * the context class loader of the current thread.
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>JAXBContext</tt> such as
     * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     * </ol>
     */
    public SAXBContextImpl(List<?> classList) throws JAXBException {
    	
    	Object[] src = classList.toArray();
    	this.classes = new Class[src.length];
        System.arraycopy(src, 0, classes, 0, src.length);
        
        for (Class<?> c: classes) {
        	BeanInfo<?> bean = new BeanInfo<>(this, c);
        	beanInfoMap.put(bean.jaxbType, bean);
        	rootMap.put(bean.qname, bean);
        }
    }
    
    @Override
    public Marshaller createMarshaller() {
        return new Marshaller(this);
    }

    @Override
    public Unmarshaller createUnmarshaller() {
        return new Unmarshaller(this);
    }
    
    /**
     * Gets the {@link BeanInfo} object that can handle
     * the given JAXB-bound object.
     *
     * <p>
     * This method traverses the base classes of the given object.
     *
     * @return null
     *      if <tt>c</tt> isn't a JAXB-bound class and <tt>fatal==false</tt>.
     */
    public final BeanInfo<?> getBeanInfo(Object o) {
        // don't allow xs:anyType beanInfo to handle all the unbound objects
        for (Class<?> c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
            BeanInfo<?> bi = beanInfoMap.get(c);
            if (bi != null)    return bi;
        }
        if (o instanceof Element)
            return beanInfoMap.get(Object.class);   // return the BeanInfo for xs:anyType
        for (Class<?> c: o.getClass().getInterfaces()) {
            BeanInfo<?> bi = beanInfoMap.get(c);
            if (bi != null)    return bi;
        }
        return null;
    }
    
    /**
     * Based on the tag name, determine what object to unmarshal,
     * and then set a new object and its loader to the current unmarshaller state.
     *
     * @return
     *      null if the given name pair is not recognized.
     */
    public final BeanInfo<?> selectRoot(QName qname) {
    	BeanInfo<?> bi = rootMap.get(qname);
        return bi;
    }
    
    protected BeanInfo<?> getOrCreate(Class<?> clazz) {
    	BeanInfo<?> bi = beanInfoMap.get(clazz);
        if (bi != null)    return bi;
        bi = new BeanInfo<>(this, clazz);
        beanInfoMap.put(bi.jaxbType, bi);
        return bi;
    }
    
    public QName createQName(BeanInfo<?> bean) {
		return new QName(bean.beanName);
    }
    
    public QName createQName(Element e) {
    	return new QName(e.getNamespaceURI(), e.getLocalName(), getPrefix(e));
    }
    
    /**
     * Gets the prefix. This is slow.
     *
     * @return can be "" but never null.
     */
    private static final String getPrefix(Element e) {
    	String qname = e.getTagName();
        int idx = qname.indexOf(':');
        if (idx < 0) return "";
        else         return qname.substring(0, idx);
    }
}