package com.run.saxb;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Creates new instances of classes.
 *
 * <p>
 * This code handles the case where the class is not public or the constructor is
 * not public.
 *
 * @since 2.0
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClassFactory {
	
    private static final Class[] emptyClass = new Class[0];
    private static final Object[] emptyObject = new Object[0];

    /**
     * Cache from a class to its default constructor.
     *
     * To avoid synchronization among threads, we use {@link ThreadLocal}.
     */
	private static final ThreadLocal<Map<Class, WeakReference<Constructor>>> tls = 
			new ThreadLocal<Map<Class, WeakReference<Constructor>>>() {
        @Override
        public Map<Class,WeakReference<Constructor>> initialValue() {
            return new WeakHashMap<Class,WeakReference<Constructor>>();
        }
    };

    public static void cleanCache() {
        if (tls != null) {
            try {
                tls.remove();
            } catch (Exception e) {
                System.out.println("Unable to clean Thread Local cache of classes used in Unmarshaller: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * The same as {@link #create0} but with an error handling to make
     * the instantiation error fatal.
     */
    public static <T> T create(Class<T> clazz) {
        try {
            return create0(clazz);
        } catch (InstantiationException e) {
        	System.out.println("failed to create a new instance of " + clazz + e);
            throw new InstantiationError(e.toString());
        } catch (IllegalAccessException e) {
        	System.out.println("failed to create a new instance of "+ clazz + e);
            throw new IllegalAccessError(e.toString());
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();

            // most likely an error on the user's code.
            // just let it through for the ease of debugging
            if(target instanceof RuntimeException)
                throw (RuntimeException)target;

            // error. just forward it for the ease of debugging
            if(target instanceof Error)
                throw (Error)target;

            // a checked exception.
            // not sure how we should report this error,
            // but for now we just forward it by wrapping it into a runtime exception
            throw new IllegalStateException(target);
        }
    }

    /**
     * Creates a new instance of the class but throw exceptions without catching it.
     */
	private static <T> T create0(final Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<Class,WeakReference<Constructor>> m = tls.get();
        Constructor<T> cons = null;
        WeakReference<Constructor> consRef = m.get(clazz);
        if (consRef != null)
            cons = consRef.get();
        if (cons == null) {
            try {
                cons = clazz.getDeclaredConstructor(emptyClass);
            } catch (NoSuchMethodException e) {
            	System.out.println("No default constructor found on "+ clazz + e);
                NoSuchMethodError exp;
                if(clazz.getDeclaringClass()!=null && !Modifier.isStatic(clazz.getModifiers())) {
                    exp = new NoSuchMethodError("NO DEFAULT CONSTRUCTOR IN INNER CLASS " + clazz.getName());
                } else {
                    exp = new NoSuchMethodError(e.getMessage());
                }
                exp.initCause(e);
                throw exp;
            }

            int classMod = clazz.getModifiers();

            if (!Modifier.isPublic(classMod) || !Modifier.isPublic(cons.getModifiers())) {
                // attempt to make it work even if the constructor is not accessible
                try {
                    cons.setAccessible(true);
                } catch(SecurityException e) {
                    // but if we don't have a permission to do so, work gracefully.
                	System.out.println("Unable to make the constructor of " + clazz + " accessible " + e);
                    throw e;
                }
            }

            m.put(clazz,new WeakReference<Constructor>(cons));
        }

        return cons.newInstance(emptyObject);
    }

    /**
     *  Call a method in the factory class to get the object.
     */
    public static Object create(Method method) {
        Throwable errorMsg;
        try {
            return method.invoke(null, emptyObject);
        } catch (InvocationTargetException ive) {
            Throwable target = ive.getTargetException();

            if(target instanceof RuntimeException)
                throw (RuntimeException)target;

            if(target instanceof Error)
                throw (Error)target;

            throw new IllegalStateException(target);
        } catch (IllegalAccessException e) {
        	System.out.println("failed to create a new instance of "+method.getReturnType().getName() + e);
            throw new IllegalAccessError(e.toString());
        } catch (IllegalArgumentException iae){
        	System.out.println("failed to create a new instance of "+method.getReturnType().getName() + iae);
            errorMsg = iae;
        } catch (NullPointerException npe){
        	System.out.println("failed to create a new instance of "+method.getReturnType().getName() + npe);
            errorMsg = npe;
        } catch (ExceptionInInitializerError eie){
            System.out.println("failed to create a new instance of "+method.getReturnType().getName() + eie);
            errorMsg = eie;
        }

        NoSuchMethodError exp;
        exp = new NoSuchMethodError(errorMsg.getMessage());
        exp.initCause(errorMsg);
        throw exp;
    }
}