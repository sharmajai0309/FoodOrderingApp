package com.deliveryService.service.entity.Enums;

public enum AssignmentStatus {
    PENDING,        // Order created, waiting for driver to accept
    ASSIGNED,       // (Deprecated or specific use) Order assigned, awaiting driver
    ACCEPTED,       // Driver accepted the order
    PICKED_UP,      // Driver picked up from restaurant
    DELIVERED,      // Successfully delivered to customer
    CANCELLED       // Cancelled (driver unavailable / order cancelled)
}
