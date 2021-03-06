package com.run.saxb.transducer.accessor;

import com.run.saxb.transducer.DataTypeVisitor;

public class BooleanAccessor implements Accessor {

	@Override
	public void accept(DataTypeVisitor visitor) {
		visitor.visit(this);
	}
}