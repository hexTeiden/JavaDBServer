package com.marie.dbserver.model;

import java.util.List;

public record HotelOccupancyView(
        int hotelId,
        String hotelName,
        List<Occupancy> occupancies
) {
}
