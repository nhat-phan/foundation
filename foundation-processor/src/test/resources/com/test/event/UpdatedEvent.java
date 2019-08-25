package com.test.event;

import kotlin.Metadata;
import net.ntworld.foundation.Event;
import net.ntworld.foundation.eventSourcing.EventSourcing;
import org.jetbrains.annotations.NotNull;

@EventSourcing(
        type = "created"
)
@Metadata(
        mv = {1, 1, 15},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\bg\u0018\u00002\u00020\u0001R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\u0005R\u0012\u0010\b\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0005R\u0012\u0010\n\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\u0005R\u0012\u0010\f\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u0005¨\u0006\u000e"},
        d2 = {"Lcom/test/event/kotlin/UpdatedEvent;", "Lnet/ntworld/foundation/eventSourcing/Event;", "companyId", "", "getCompanyId", "()Ljava/lang/String;", "email", "getEmail", "id", "getId", "name", "getName", "time", "getTime", "foundation-processor"}
)
public interface UpdatedEvent extends Event {
    @NotNull
    String getId();

    @NotNull
    String getCompanyId();

    @NotNull
    String getEmail();

    @NotNull
    String getName();

    @NotNull
    String getTime();
}
