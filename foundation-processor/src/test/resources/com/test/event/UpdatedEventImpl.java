package com.test.event;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.ntworld.foundation.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Implementation
@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001J\t\u0010\u001b\u001a\u00020\u0003HÖ\u0001R\u0014\u0010\u0004\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0014\u0010\u0006\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0014\u0010\u0007\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n¨\u0006\u001c"},
        d2 = {"Lcom/test/event/kotlin/UpdatedEventImpl;", "Lcom/test/event/kotlin/UpdatedEvent;", "id", "", "companyId", "email", "name", "time", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCompanyId", "()Ljava/lang/String;", "getEmail", "getId", "getName", "getTime", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "", "toString", "foundation-processor"}
)
public final class UpdatedEventImpl implements UpdatedEvent {
    @NotNull
    private final String id;
    @NotNull
    private final String companyId;
    @NotNull
    private final String email;
    @NotNull
    private final String name;
    @NotNull
    private final String time;

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getCompanyId() {
        return this.companyId;
    }

    @NotNull
    public String getEmail() {
        return this.email;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getTime() {
        return this.time;
    }

    public UpdatedEventImpl(@NotNull String id, @NotNull String companyId, @NotNull String email, @NotNull String name, @NotNull String time) {
        super();
        Intrinsics.checkParameterIsNotNull(id, "id");
        Intrinsics.checkParameterIsNotNull(companyId, "companyId");
        Intrinsics.checkParameterIsNotNull(email, "email");
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(time, "time");
        this.id = id;
        this.companyId = companyId;
        this.email = email;
        this.name = name;
        this.time = time;
    }

    @NotNull
    public final String component1() {
        return this.getId();
    }

    @NotNull
    public final String component2() {
        return this.getCompanyId();
    }

    @NotNull
    public final String component3() {
        return this.getEmail();
    }

    @NotNull
    public final String component4() {
        return this.getName();
    }

    @NotNull
    public final String component5() {
        return this.getTime();
    }

    @NotNull
    public final UpdatedEventImpl copy(@NotNull String id, @NotNull String companyId, @NotNull String email, @NotNull String name, @NotNull String time) {
        Intrinsics.checkParameterIsNotNull(id, "id");
        Intrinsics.checkParameterIsNotNull(companyId, "companyId");
        Intrinsics.checkParameterIsNotNull(email, "email");
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(time, "time");
        return new UpdatedEventImpl(id, companyId, email, name, time);
    }

    // $FF: synthetic method
    @NotNull
    public static UpdatedEventImpl copy$default(UpdatedEventImpl var0, String var1, String var2, String var3, String var4, String var5, int var6, Object var7) {
        if ((var6 & 1) != 0) {
            var1 = var0.getId();
        }

        if ((var6 & 2) != 0) {
            var2 = var0.getCompanyId();
        }

        if ((var6 & 4) != 0) {
            var3 = var0.getEmail();
        }

        if ((var6 & 8) != 0) {
            var4 = var0.getName();
        }

        if ((var6 & 16) != 0) {
            var5 = var0.getTime();
        }

        return var0.copy(var1, var2, var3, var4, var5);
    }

    @NotNull
    public String toString() {
        return "UpdatedEventImpl(id=" + this.getId() + ", companyId=" + this.getCompanyId() + ", email=" + this.getEmail() + ", name=" + this.getName() + ", time=" + this.getTime() + ")";
    }

    public int hashCode() {
        String var10000 = this.getId();
        int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
        String var10001 = this.getCompanyId();
        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
        var10001 = this.getEmail();
        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
        var10001 = this.getName();
        var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
        var10001 = this.getTime();
        return var1 + (var10001 != null ? var10001.hashCode() : 0);
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof UpdatedEventImpl) {
                UpdatedEventImpl var2 = (UpdatedEventImpl)var1;
                if (Intrinsics.areEqual(this.getId(), var2.getId()) && Intrinsics.areEqual(this.getCompanyId(), var2.getCompanyId()) && Intrinsics.areEqual(this.getEmail(), var2.getEmail()) && Intrinsics.areEqual(this.getName(), var2.getName()) && Intrinsics.areEqual(this.getTime(), var2.getTime())) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
