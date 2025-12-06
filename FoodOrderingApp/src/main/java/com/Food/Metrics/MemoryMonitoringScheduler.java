package com.Food.Metrics;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MemoryMonitoringScheduler {

    private final MemoryMonitor memoryMonitor;

    public MemoryMonitoringScheduler(MemoryMonitor memoryMonitor) {
        this.memoryMonitor = memoryMonitor;
    }

    // Monitor every 5 minutes
    @Scheduled(fixedRate = 300000)  // 300,000 ms = 5 minutes
    public void monitorMemory() {
        memoryMonitor.logMemoryUsage("Scheduled Check");
        memoryMonitor.recordMemoryMetrics();
    }

    // Quick check every minute
    @Scheduled(fixedRate = 60000)  // 60,000 ms = 1 minute
    public void quickMemoryCheck() {
        if (memoryMonitor.isMemoryCritical()) {
            memoryMonitor.logMemoryUsage("CRITICAL - Quick Check");
        }
    }
}
