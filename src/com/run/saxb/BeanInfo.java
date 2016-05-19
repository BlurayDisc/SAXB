package com.run.saxb;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Encapsulates various JAXB operations on objects bound by JAXB.
 * Immutable and thread-safe.
 */
public class BeanInfo<BeanT> {
	
	public final String beanName;
	
    /**
     * Gets the JAXB bound class type that this {@link BeanInfo}
     * handles.
     *
     * <p>
     * IOW, when a bean info object is requested for T,
     * sometimes the bean info for one of its base classes might be
     * returned.
     */
    public final Class<BeanT> jaxbType;
    
    public final QName qname;
    
    public final Map<String, String> attributeProperties = new LinkedHashMap<>();
    
    public final Map<String, BeanInfo<?>> properties = new LinkedHashMap<>();
    
    public BeanInfo(SAXBContextImpl grammar, Class<BeanT> jaxbType) {
    	this.beanName = jaxbType.getSimpleName();
    	this.jaxbType = jaxbType;
    	this.qname = grammar.createQName(this);
        grammar.beanInfoMap.put(jaxbType, this);
	}
    
    /**
     * Creates a new instance of the bean.
     *
     * <p>
     * This operation is only supported when {@link #isImmutable} is false.
     */
    public BeanT createInstance() {
        BeanT bean = ClassFactory.create(jaxbType);
        return bean;
    }
    
    /**
     * Serializes the bean as the root element.
     *
     * <p>
     * In the java-to-schema binding, an object might marshal in two different
     * ways depending on whether it is used as the root of the graph or not.
     * In the former case, an object could marshal as an element, whereas
     * in the latter case, it marshals as a type.
     *
     * <p>
     * This method is used to marshal the root of the object graph to allow
     * this semantics to be implemented.
     *
     * <p>
     * It is doubtful to me if it's a good idea for an object to marshal
     * in two ways depending on the context.
     *
     * <p>
     * For schema-to-java, this is equivalent to {@link #serializeBody(Object, XMLSerializer)}.
     */
    public void serializeRoot(Node target) {
    	serializeBody(target);
    }

    /**
     * Serializes child elements and texts into the specified target.
     */
    public void serializeBody(Node target) {
    	Document document = target.getOwnerDocument();
    	for (BeanInfo<?> property: properties.values()) {
    		Node node = target.appendChild(
    					document.createElement(property.beanName));
    		serializeAttributes(node);
    		serializeBody(node);
    	}
    }

    /**
     * Serializes attributes into the specified target.
     */
    public void serializeAttributes(Node target) {
        for (Entry<String, String> entry: attributeProperties.entrySet()) {
        	Element element = (Element) target;
        	element.setAttribute(entry.getKey(), entry.getValue());
    	}
    }
}