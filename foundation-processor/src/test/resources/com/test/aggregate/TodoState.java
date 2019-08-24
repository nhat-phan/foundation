package com.test.aggregate;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.ntworld.foundation.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\r\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fHÖ\u0003J\t\u0010\u0010\u001a\u00020\u0011HÖ\u0001J\t\u0010\u0012\u001a\u00020\u0003HÖ\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\t¨\u0006\u0013"},
        d2 = {"Lcom/test/aggregate/TodoState;", "Lnet/ntworld/foundation/State;", "id", "", "isGenerated", "", "(Ljava/lang/String;Z)V", "getId", "()Ljava/lang/String;", "()Z", "component1", "component2", "copy", "equals", "other", "", "hashCode", "", "toString", "foundation-processor"}
)
public final class TodoState implements State {
    @NotNull
    private final String id;
    private final boolean isGenerated;

    @NotNull
    public String getId() {
        return this.id;
    }

    public boolean isGenerated() {
        return this.isGenerated;
    }

    public TodoState(@NotNull String id, boolean isGenerated) {
        super();
        this.id = id;
        this.isGenerated = isGenerated;
    }

    @NotNull
    public final String component1() {
        return this.getId();
    }

    public final boolean component2() {
        return this.isGenerated();
    }

    @NotNull
    public final TodoState copy(@NotNull String id, boolean isGenerated) {
        Intrinsics.checkParameterIsNotNull(id, "id");
        return new TodoState(id, isGenerated);
    }

    // $FF: synthetic method
    @NotNull
    public static TodoState copy$default(TodoState var0, String var1, boolean var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.getId();
        }

        if ((var3 & 2) != 0) {
            var2 = var0.isGenerated();
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "TodoState(id=" + this.getId() + ", isGenerated=" + this.isGenerated() + ")";
    }

    public int hashCode() {
        String var10000 = this.getId();
        int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
        byte var10001 = this.isGenerated() ? (byte)1 : (byte)0;
        if (var10001 != 0) {
            var10001 = 1;
        }

        return var1 + var10001;
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof TodoState) {
                TodoState var2 = (TodoState)var1;
                if (Intrinsics.areEqual(this.getId(), var2.getId()) && this.isGenerated() == var2.isGenerated()) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
