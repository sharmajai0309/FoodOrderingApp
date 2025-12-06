package com.Food.ServiceTesting;


import com.Food.Metrics.MemoryMonitor;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
public class MemoryMonitorTest {
    @Autowired
    private MemoryMonitor memoryMonitor;

    @Test
    void testMemoryMonitoring() {
        System.out.println("=== Testing Memory Monitor ===");

        // 1. Log current memory
        memoryMonitor.logMemoryUsage("Test Start");

        // 2. Get memory stats
        MemoryMonitor.MemoryStats stats = memoryMonitor.getMemoryStats();
        System.out.println("Used: " + stats.getUsedMB() + " MB");
        System.out.println("Free: " + stats.getFreeMB() + " MB");
        System.out.println("Used %: " + stats.getUsedPercentage() + "%");

        // 3. Check memory status
        System.out.println("Is memory critical? " + memoryMonitor.isMemoryCritical());
        System.out.println("Is memory high? " + memoryMonitor.isMemoryHigh());

        // 4. Create some objects to see memory change
        List<String> testList = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            testList.add("TestString" + i);
        }

        memoryMonitor.logMemoryUsage("After creating list");

        // 5. Clear list and suggest GC
        testList.clear();
        testList = null;

        memoryMonitor.suggestGCIfNeeded("Test cleanup");
        memoryMonitor.logMemoryUsage("Test End");
    }
}
