package com.run.saxb.transducer.accessor;

import com.run.saxb.transducer.DataTypeVisitor;

public class IntegerAccessor implements Accessor {

	@Override
	public void accept(DataTypeVisitor visitor) {
		visitor.visit(this);
	}
}