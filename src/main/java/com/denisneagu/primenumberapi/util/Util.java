package com.denisneagu.primenumberapi.util;

import com.denisneagu.primenumberapi.exception.MemoryConstraintException;

public class Util {
    private static final int MAX_SAFE_MEMORY_PERCENTAGE = 40;

    public static String formatSizeInMbAndMiB(long sizeInBytes) {
        // mebibyte
        double sizeInMiB = sizeInBytes / (1024.0 * 1024.0);
        double sizeInMB = sizeInBytes / 1_000_000.0;
        return String.format("%.4f MB and %.4f MiB (true value)", sizeInMB, sizeInMiB);
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getMaxSafeMemory() {
        return (getMaxMemory() * MAX_SAFE_MEMORY_PERCENTAGE) / 100;
    }

    public static void checkMemorySafety(long bytesNeeded) {
        long maxSafeMemory = getMaxSafeMemory();
        if (bytesNeeded > maxSafeMemory) {
            throw new MemoryConstraintException(
                    String.format(
                            "Requested memory for %s exceeds safe limit of %s",
                            formatSizeInMbAndMiB(bytesNeeded),
                            formatSizeInMbAndMiB(maxSafeMemory)));
        }
    }
}