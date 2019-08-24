package com.test.aggregate;

import kotlin.Metadata;
import net.ntworld.foundation.Aggregate;

@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\b\u0010\u0003\u001a\u00020\u0004H&¨\u0006\u0005"},
        d2 = {"Lcom/test/aggregate/TodoOne;", "Lnet/ntworld/foundation/Aggregate;", "Lcom/test/aggregate/TodoState;", "doSomething", "", "foundation-processor"}
)
public interface TodoOne extends Aggregate<TodoState> {
    void doSomething();
}
