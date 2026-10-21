package com.pickup.tool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class DatesTest {
    @Test
    public void ordinalSuffixFollowsEnglishRules() {
        assertEquals("st", Dates.ordinalSuffix(1));
        assertEquals("nd", Dates.ordinalSuffix(2));
        assertEquals("rd", Dates.ordinalSuffix(3));
        assertEquals("th", Dates.ordinalSuffix(4));
        assertEquals("st", Dates.ordinalSuffix(21));
        assertEquals("nd", Dates.ordinalSuffix(22));
        assertEquals("rd", Dates.ordinalSuffix(23));
        assertEquals("st", Dates.ordinalSuffix(31));
    }

    @Test
    public void teensAlwaysUseTh() {
        assertEquals("th", Dates.ordinalSuffix(11));
        assertEquals("th", Dates.ordinalSuffix(12));
        assertEquals("th", Dates.ordinalSuffix(13));
        assertEquals("th", Dates.ordinalSuffix(111));
    }
}
