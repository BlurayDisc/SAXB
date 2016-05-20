## Introduction

SAXB is a client and server side library that implements the XML to Java Objects mapping.

The Simple Architecture for XML Binding (SAXB) is a much lighter and easy to use version of the JAXB. It's fast and has been optimized exactly like the JAXB, it promises with the same thread safetly, cache handling and low memory usage.

## Quick Start Guide

SAXB has the same API as the JAXB for it's marshaling and unmarshaling prcoess. It allows you to define SAXBContext by using class names, classes or packagenames.
SAXB does not require a jaxb.index file but instead it's been implemented to be able to reflect all the classes under a specific, user-defined package.

**Unmarshalling**

The Unmarshaller class provides the client application the ability to convert XML data into a tree of Java content objects.
    
    SAXBContext sc = SAXBContext.newInstance( "com.foo:com.bar" ); // scanning from 2 packages.
    Unmarshaller u = sc.createUnmarshaller();
    FooObject fooObj = (FooObject) u.unmarshal( new File( "foo.xml" ) ); // ok
    BarObject barObj = (BarObject) u.unmarshal( new File( "bar.xml" ) ); // ok
        
**Marshalling**

The Marshaller class provides the client application the ability to convert a Java content tree back into XML data.

    SAXBContext sc = SAXBContext.newInstance( "com.foo" );

    // unmarshal from foo.xml
    Unmarshaller u = sc.createUnmarshaller();
    FooObject fooObj = (FooObject) u.unmarshal( new File( "foo.xml" ) );

    // marshal to System.out
    Marshaller m = sc.createMarshaller();
    m.marshal( fooObj, System.out );
