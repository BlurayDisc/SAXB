package com.run.saxb;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

public class ContextFinder {

	static SAXBContext find(String contextPath) throws JAXBException {
		
		final List<Class<?>> classList = new ArrayList<>();
		
        StringTokenizer packages = new StringTokenizer(contextPath, ":");

        if(!packages.hasMoreTokens())
            throw new JAXBException("ContextFinder.NoPackageInContextPath");
        
        ContextFinder.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();

        while(packages.hasMoreTokens()) {
        	
            String packageName = packages.nextToken(":").replace('.','/');
            
            List<Class<?>> classes = scan(packageName);
            classList.addAll(classes);
            //throw new JAXBException("ContextFinder.MissingProperty");
        }
        
        return new SAXBContextImpl(classList);
	}
	
	/**
	 * Compute the absolute file path to the jar file.
	 * The framework is based on http://stackoverflow.com/a/12733172/1614775
	 * But that gets it right for only one of the four cases.
	 * 
	 * @param aclass A class residing in the required jar.
	 * 
	 * @return A File object for the directory in which the jar file resides.
	 * During testing with NetBeans, the result is ./build/classes/,
	 * which is the directory containing what will be in the jar.
	 */
	public static File getJarDir(Class<?> aclass) {
	    URL url;
	    String extURL;      //  url.toExternalForm();

	    // get an url
	    try {
	        url = aclass.getProtectionDomain().getCodeSource().getLocation();
	          // url is in one of two forms
	          //        ./build/classes/   NetBeans test
	          //        jardir/JarName.jar  froma jar
	    } catch (SecurityException ex) {
	        url = aclass.getResource(aclass.getSimpleName() + ".class");
	        // url is in one of two forms, both ending "/com/physpics/tools/ui/PropNode.class"
	        //          file:/U:/Fred/java/Tools/UI/build/classes
	        //          jar:file:/U:/Fred/java/Tools/UI/dist/UI.jar!
	    }

	    // convert to external form
	    extURL = url.toExternalForm();

	    // prune for various cases
	    if (extURL.endsWith(".jar"))   // from getCodeSource
	        extURL = extURL.substring(0, extURL.lastIndexOf("/"));
	    else {  // from getResource
	        String suffix = "/"+(aclass.getName()).replace(".", "/")+".class";
	        extURL = extURL.replace(suffix, "");
	        if (extURL.startsWith("jar:") && extURL.endsWith(".jar!"))
	            extURL = extURL.substring(4, extURL.lastIndexOf("/"));
	    }

	    // convert back to url
	    try {
	        url = new URL(extURL);
	    } catch (MalformedURLException mux) {
	        // leave url unchanged; probably does not happen
	    }

	    // convert url to File
	    try {
	        return new File(url.toURI());
	    } catch(URISyntaxException ex) {
	        return new File(url.getPath());
	    }
	}
	
    private static final String PKG_SEPARATOR = ".";

    private static final String CLASS_FILE_SUFFIX = ".class";

    public static List<Class<?>> scan(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, File.separator);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(
            		String.format("Unable to get resources from path '%s'. Are you sure the package '%s' exists?", 
            				scannedPath, 
            				scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(scan(file, scannedPackage));
        }
        return classes;
    }

    private static List<Class<?>> scan(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(scan(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {}
        }
        return classes;
    }
}