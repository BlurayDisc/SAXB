package com.run.saxb.transducer;

import com.run.saxb.transducer.accessor.BooleanAccessor;
import com.run.saxb.transducer.accessor.DoubleAccessor;
import com.run.saxb.transducer.accessor.IntegerAccessor;
import com.run.saxb.transducer.accessor.ListAccessor;
import com.run.saxb.transducer.accessor.StringAccessor;

public interface DataTypeVisitor {
	
    void visit(BooleanAccessor accessor);
    
    void visit(DoubleAccessor accessor);
    
    void visit(IntegerAccessor accessor);
    
    void visit(StringAccessor accessor);
    
    void visit(ListAccessor accessor);
}