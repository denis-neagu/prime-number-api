package com.denisneagu.primenumberapi.util;

import com.denisneagu.primenumberapi.exception.MemoryConstraintException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class UtilTest {

    @Test
    void givenValidInput_whenFormatSizeInMbAndMib_ThenReturnExpectedString() {
        long sizeInBytes = 5_000_000L;
        String result = Util.formatSizeInMbAndMiB(sizeInBytes);
        Assertions.assertEquals("5.0000 MB and 4.7684 MiB (true value)", result);
    }

    @Test
    void givenSafeMemoryRequest_whenCheckMemorySafety_thenDoesNotThrow() {
        try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(Util::getMaxMemory).thenReturn(1_000_000_000L);
            Assertions.assertDoesNotThrow(() -> Util.checkMemorySafety(100_000_000L));
        }
    }

    @Test
    void givenUnsafeMemoryRequest_whenCheckMemorySafety_thenThrowMemoryConstraintException() {
        try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(Util::getMaxMemory).thenReturn(100_000_000L);
            Assertions.assertThrows(MemoryConstraintException.class, () ->
                    Util.checkMemorySafety(50_000_000L)
            );
        }
    }
}
