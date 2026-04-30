package com.marie.dbserver.model;

public record Occupancy(
        int hid,
        int rooms,
        int usedRooms,
        int usedBeds,
        int year,
        int month
) {
}
