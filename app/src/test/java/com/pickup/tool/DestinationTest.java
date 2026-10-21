package com.pickup.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public final class DestinationTest {
    @Test
    public void fromKeyIgnoresCase() {
        assertEquals(Destination.CAINIAO, Destination.fromKey("CAINIAO"));
        assertEquals(Destination.TAOBAO_PENDING, Destination.fromKey("taobao_pending"));
    }

    @Test
    public void fromKeyReturnsNullForInvalidInput() {
        assertNull(Destination.fromKey(null));
        assertNull(Destination.fromKey(""));
        assertNull(Destination.fromKey("unknown"));
    }

    @Test
    public void everyDestinationHasLinkData() {
        for (Destination destination : Destination.values()) {
            assertEquals(0 < destination.appUris.length, true);
            assertEquals(destination.webUri.startsWith("https://"), true);
        }
    }
}
