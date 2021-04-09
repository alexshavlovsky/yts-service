package com.ctzn.youtubescraper.core.parser;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.ctzn.youtubescraper.core.parser.ParserUtil.parsePublishedTimeText;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserUtilTest {

    private static void assertDatesEquals(Date expected, Date actual) {
        assertNotEquals(null, actual);
        assertTrue(Math.abs(actual.getTime() - expected.getTime()) < 1000);
    }

    @Test
    void testParsePublishedTimeText() {
        assertDatesEquals(Date.from(Instant.now().minus(Duration.ofSeconds(2))), parsePublishedTimeText("2 seconds ago"));
        assertDatesEquals(Date.from(Instant.now().minus(Duration.ofMinutes(4))), parsePublishedTimeText("4 minutes ago"));
        assertDatesEquals(Date.from(Instant.now().minus(Duration.ofHours(10))), parsePublishedTimeText("10 hours ago"));
        assertDatesEquals(Date.from(Instant.now().minus(Duration.ofDays(5))), parsePublishedTimeText("5 days ago"));
        assertDatesEquals(Date.from(Instant.now().minus(Duration.ofDays(3 * 7))), parsePublishedTimeText("3 weeks ago"));
        assertDatesEquals(Date.from(ZonedDateTime.now().minusMonths(2).toInstant()), parsePublishedTimeText("2 months ago"));
        assertDatesEquals(Date.from(ZonedDateTime.now().minusYears(3).toInstant()), parsePublishedTimeText("3 years ago"));
        assertDatesEquals(Date.from(ZonedDateTime.now().minusYears(1).toInstant()), parsePublishedTimeText("Streamed 1 year ago"));
    }

}
