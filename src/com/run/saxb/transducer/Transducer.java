package com.run.saxb.transducer;

import com.run.saxb.transducer.accessor.Accessor;
import com.run.saxb.transducer.accessor.BooleanAccessor;
import com.run.saxb.transducer.accessor.DoubleAccessor;
import com.run.saxb.transducer.accessor.IntegerAccessor;
import com.run.saxb.transducer.accessor.ListAccessor;
import com.run.saxb.transducer.accessor.StringAccessor;

/**
 * Responsible for converting a Java object to a lexical representation
 * and vice versa.
 *
 * <p>
 * An implementation of this interface hides how this conversion happens.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class Transducer {

	// com.sun.xml.internal.bind.v2.runtime.Transducer
    Accessor[] xers  = new Accessor[] {
    	new BooleanAccessor(), 
    	new IntegerAccessor(),
    	new DoubleAccessor(),
    	new StringAccessor(),
    	new ListAccessor()
    };
    		
    public Transducer() {
    	
    }

    public void accept(DataTypeVisitor visitor) {
        for(Accessor x : xers) {
            x.accept(visitor);
        }
    }
}