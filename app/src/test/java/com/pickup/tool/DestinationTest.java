package com.pickup.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    public void everyDestinationHasDisplayAndLinkData() {
        for (Destination destination : Destination.values()) {
            assertFalse(destination.label.isEmpty());
            assertFalse(destination.subtitle.isEmpty());
            assertTrue(destination.appUris.length > 0);
            assertTrue(destination.webUri.startsWith("https://"));
        }
    }
}
