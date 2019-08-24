package com.test.aggregate;

import kotlin.Metadata;
import net.ntworld.foundation.Implementation;
import net.ntworld.foundation.State;
import org.jetbrains.annotations.NotNull;

@Implementation
@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\r\u001a\u00020\u000eH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u000f"},
        d2 = {"Lcom/test/aggregate/TodoOneImpl;", "Lcom/test/aggregate/TodoOne;", "id", "", "isGenerated", "", "(Ljava/lang/String;Z)V", "getId", "()Ljava/lang/String;", "state", "Lcom/test/aggregate/TodoState;", "getState", "()Lcom/test/aggregate/TodoState;", "doSomething", "", "foundation-processor"}
)
public final class TodoOneImpl implements TodoOne {
    @NotNull
    private final TodoState state;
    @NotNull
    private final String id;

    @NotNull
    public TodoState getState() {
        return this.state;
    }

    public void doSomething() {
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public TodoOneImpl(@NotNull String id, boolean isGenerated) {
        super();
        this.id = id;
        this.state = new TodoState(this.getId(), isGenerated);
    }
}
