package com.Food.Controller;

import com.Food.Metrics.MemoryMonitor;
import com.Food.Response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/memory")
public class MemoryController {

    private final MemoryMonitor memoryMonitor;

    public MemoryController(MemoryMonitor memoryMonitor) {
        this.memoryMonitor = memoryMonitor;
    }

    @GetMapping("/status")
    public ApiResponse getMemoryStatus() {
        MemoryMonitor.MemoryStats stats = memoryMonitor.getMemoryStats();

        String health;
        if (stats.getUsedPercentage() > 90) {
            health = "CRITICAL";
        } else if (stats.getUsedPercentage() > 70) {
            health = "WARNING";
        } else {
            health = "HEALTHY";
        }

        return ApiResponse.success(stats,
                String.format("Memory Status: %s (%.1f%% used)",
                        health, stats.getUsedPercentage()));
    }

    @GetMapping("/suggest-gc")
    public ApiResponse suggestGarbageCollection() {
        long before = Runtime.getRuntime().freeMemory();
        System.gc();
        long after = Runtime.getRuntime().freeMemory();

        long freedMB = (after - before) / (1024 * 1024);

        return ApiResponse.success(
                Map.of(
                        "freedMemoryMB", freedMB,
                        "beforeFreeMB", before / (1024 * 1024),
                        "afterFreeMB", after / (1024 * 1024)
                ),
                String.format("GC suggested. Freed approximately %d MB", freedMB)
        );
    }
}
