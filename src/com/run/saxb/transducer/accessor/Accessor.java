package com.run.saxb.transducer.accessor;

import com.run.saxb.transducer.DataTypeVisitor;

/**
 * Field Accessor. Implemented with the visitor pattern.
 * Accesses a particular property of a bean. 
 * Accessor can be used as a receiver. Upon receiving an object it sets that to the field.
 * @author RuN
 *
 */
public interface Accessor {
	
	void accept(DataTypeVisitor visitor);
}
