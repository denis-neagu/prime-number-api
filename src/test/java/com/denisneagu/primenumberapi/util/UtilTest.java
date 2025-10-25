package com.denisneagu.primenumberapi.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilTest {

    @Test
    void givenValidInput_whenFormatSizeInMbAndMib_ThenReturnExpectedString() {
        long sizeInBytes = 5_000_000L;
        String result = Util.formatSizeInMbAndMiB(sizeInBytes);
        Assertions.assertEquals("5.0000 MB and 4.7684 MiB (true value)", result);
    }
}
