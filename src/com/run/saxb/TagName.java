/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.run.saxb;

import org.w3c.dom.Element;

/**
 * Represents an XML tag name (and attributes for start tags.)
 *
 * <p>
 * This object is used so reduce the number of method call parameters
 * among unmarshallers.
 *
 * An instance of this is expected to be reused by the caller of
 * {@link XmlVisitor}.
 *
 * <p>
 * The 'qname' parameter, which holds the qualified name of the tag
 * (such as 'foo:bar' or 'zot'), is not used in the typical unmarshalling
 * route and it's also expensive to compute for some input.
 * Thus this parameter is computed lazily.
 *
 * @author Kohsuke Kawaguchi
 */
public class TagName {
    /**
     * URI of the attribute/element name.
     *
     * Can be empty, but never null. Interned.
     */
    public String uri;
    /**
     * Local part of the attribute/element name.
     *
     * Never be null. Interned.
     */
    public String local;
    
    /**
     * The qualified name of the tag.
     */
    public String qname;
    
    public TagName() {
    	
    }

    public TagName(Element element) { 
        this.uri = element.getNamespaceURI();
        this.local = element.getLocalName();
        this.qname = element.getTagName();
    }

    /**
     * Checks if the given name pair matches this name.
     */
    public final boolean matches(String nsUri, String local) {
        return this.uri == nsUri && this.local == local;
    }

    /**
     * Gets the prefix. This is slow.
     *
     * @return can be "" but never null.
     */
    public final String getPrefix() {
        int idx = qname.indexOf(':');
        if (idx < 0) return "";
        else         return qname.substring(0, idx);
    }

    @Override
    public String toString() {
        return '{'+uri+'}'+local;
    }
}