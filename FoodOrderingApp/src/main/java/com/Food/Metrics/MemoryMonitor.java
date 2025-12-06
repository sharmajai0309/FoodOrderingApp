package com.Food.Metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class MemoryMonitor {

    private static final Logger log = LoggerFactory.getLogger(MemoryMonitor.class);
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final long MB = 1024 * 1024;

    /**
     * Log current memory usage
     */
    public void logMemoryUsage(String operation) {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        double usedPercentage = (double) usedMemory / maxMemory * 100;
        double freePercentage = (double) freeMemory / totalMemory * 100;

        String message = String.format(
                "[Memory] %s | Used: %sMB (%s%%) | Free: %sMB (%s%%) | Total: %sMB | Max: %sMB",
                operation,
                df.format(usedMemory / (double) MB),
                df.format(usedPercentage),
                df.format(freeMemory / (double) MB),
                df.format(freePercentage),
                df.format(totalMemory / (double) MB),
                df.format(maxMemory / (double) MB)
        );

        if (usedPercentage > 80) {
            log.warn("⚠️  HIGH MEMORY USAGE - " + message);
        } else if (usedPercentage > 60) {
            log.info("ℹ️  " + message);
        } else {
            log.debug(message);
        }
    }

    /**
     * Check if memory is critically low
     */
    public boolean isMemoryCritical() {
        Runtime runtime = Runtime.getRuntime();
        double freePercentage = (double) runtime.freeMemory() / runtime.totalMemory() * 100;
        return freePercentage < 5.0;  // Less than 5% free memory
    }

    /**
     * Check if memory usage is high
     */
    public boolean isMemoryHigh() {
        Runtime runtime = Runtime.getRuntime();
        double usedPercentage = (double) (runtime.totalMemory() - runtime.freeMemory())
                / runtime.maxMemory() * 100;
        return usedPercentage > 80.0;  // More than 80% used
    }

    /**
     * Get memory statistics as a Map
     */
    public MemoryStats getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();

        return MemoryStats.builder()
                .usedMB((runtime.totalMemory() - runtime.freeMemory()) / MB)
                .freeMB(runtime.freeMemory() / MB)
                .totalMB(runtime.totalMemory() / MB)
                .maxMB(runtime.maxMemory() / MB)
                .usedPercentage((double) (runtime.totalMemory() - runtime.freeMemory())
                        / runtime.maxMemory() * 100)
                .build();
    }

    /**
     * Suggest GC if memory is critical
     */
    public void suggestGCIfNeeded(String operation) {
        if (isMemoryCritical()) {
            log.warn("🔄 Memory critical! Suggesting GC before: {}", operation);

            long before = Runtime.getRuntime().freeMemory();
            System.gc();  // Just a suggestion
            long after = Runtime.getRuntime().freeMemory();

            log.info("GC suggested. Memory freed: {} MB",
                    (after - before) / MB);
        }
    }

    /**
     * Record memory usage for metrics
     */
    public void recordMemoryMetrics() {
        MemoryStats stats = getMemoryStats();

        // You can push to monitoring system here
        log.debug("Memory Metrics - Used: {}%, Free: {}MB",
                df.format(stats.getUsedPercentage()),
                stats.getFreeMB());
    }

    /**
     * DTO for memory statistics
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemoryStats {
        private long usedMB;
        private long freeMB;
        private long totalMB;
        private long maxMB;
        private double usedPercentage;
    }
}
