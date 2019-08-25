package com.test.event;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.ntworld.foundation.Event;
import net.ntworld.foundation.eventSourcing.EventSourcing;
import net.ntworld.foundation.eventSourcing.EventSourcing.Encrypted;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventSourcing(
        type = "created"
)
@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0003HÖ\u0001R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0019"},
        d2 = {"Lcom/test/event/kotlin/CreatedEvent;", "Lnet/ntworld/foundation/eventSourcing/Event;", "id", "", "companyId", "email", "time", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCompanyId", "()Ljava/lang/String;", "getEmail", "getId", "getTime", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "hashCode", "", "toString", "foundation-processor"}
)
public final class CreatedEvent implements Event {
    @NotNull
    private final String id;
    @net.ntworld.foundation.eventSourcing.EventSourcing.Metadata
    @NotNull
    private final String companyId;
    @Encrypted
    @NotNull
    private final String email;
    @net.ntworld.foundation.eventSourcing.EventSourcing.Metadata
    @NotNull
    private final String time;

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final String getCompanyId() {
        return this.companyId;
    }

    @NotNull
    public final String getEmail() {
        return this.email;
    }

    @NotNull
    public final String getTime() {
        return this.time;
    }

    public CreatedEvent(@NotNull String id, @NotNull String companyId, @NotNull String email, @NotNull String time) {
        super();
        this.id = id;
        this.companyId = companyId;
        this.email = email;
        this.time = time;
    }

    @NotNull
    public final String component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.companyId;
    }

    @NotNull
    public final String component3() {
        return this.email;
    }

    @NotNull
    public final String component4() {
        return this.time;
    }

    @NotNull
    public final CreatedEvent copy(@NotNull String id, @NotNull String companyId, @NotNull String email, @NotNull String time) {
        Intrinsics.checkParameterIsNotNull(id, "id");
        Intrinsics.checkParameterIsNotNull(companyId, "companyId");
        Intrinsics.checkParameterIsNotNull(email, "email");
        Intrinsics.checkParameterIsNotNull(time, "time");
        return new CreatedEvent(id, companyId, email, time);
    }

    // $FF: synthetic method
    @NotNull
    public static CreatedEvent copy$default(CreatedEvent var0, String var1, String var2, String var3, String var4, int var5, Object var6) {
        if ((var5 & 1) != 0) {
            var1 = var0.id;
        }

        if ((var5 & 2) != 0) {
            var2 = var0.companyId;
        }

        if ((var5 & 4) != 0) {
            var3 = var0.email;
        }

        if ((var5 & 8) != 0) {
            var4 = var0.time;
        }

        return var0.copy(var1, var2, var3, var4);
    }

    @NotNull
    public String toString() {
        return "CreatedEvent(id=" + this.id + ", companyId=" + this.companyId + ", email=" + this.email + ", time=" + this.time + ")";
    }

    public int hashCode() {
        String var10000 = this.id;
        int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
        String var10001 = this.companyId;
        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
        var10001 = this.email;
        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
        var10001 = this.time;
        return var1 + (var10001 != null ? var10001.hashCode() : 0);
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof CreatedEvent) {
                CreatedEvent var2 = (CreatedEvent) var1;
                if (Intrinsics.areEqual(this.id, var2.id) && Intrinsics.areEqual(this.companyId, var2.companyId) && Intrinsics.areEqual(this.email, var2.email) && Intrinsics.areEqual(this.time, var2.time)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
