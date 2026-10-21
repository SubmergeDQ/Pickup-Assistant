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
    public void fromActionResolvesStaticShortcutKeys() {
        String prefix = "com.pickup.tool.OPEN_";
        assertEquals(Destination.CAINIAO, Destination.fromAction(prefix + "CAINIAO", prefix));
        assertEquals(
                Destination.TAOBAO_PENDING,
                Destination.fromAction(prefix + "TAOBAO_PENDING", prefix)
        );
        assertNull(Destination.fromAction("com.pickup.tool.OPEN", prefix));
        assertNull(Destination.fromAction(prefix + "UNKNOWN", prefix));
        assertNull(Destination.fromAction(null, prefix));
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
