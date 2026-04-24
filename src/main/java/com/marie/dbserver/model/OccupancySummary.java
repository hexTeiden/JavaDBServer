package com.marie.dbserver.model;

public record OccupancySummary(
        String name,
        String category,
        int year,
        int month,
        int totalRoomsOccupied,
        int totalBedsOccupied
) {}
