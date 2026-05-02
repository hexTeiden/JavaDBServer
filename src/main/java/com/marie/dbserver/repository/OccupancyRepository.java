package com.marie.dbserver.repository;

import com.marie.dbserver.model.Occupancy;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OccupancyRepository {
    private final JdbcClient jdbc;

    public OccupancyRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Occupancy> getAllOccupancies() {
        return jdbc.sql("SELECT hotel_id AS hid, rooms, usedRooms, usedBeds, year, month FROM occupancies")
                .query(Occupancy.class)
                .list();
    }

    public List<Occupancy> findByHotelAndPeriod(int hotelId, Integer yearFrom, Integer yearTo, Integer monthFrom, Integer monthTo) {
        return jdbc.sql("""
                SELECT hotel_id AS hid, rooms, usedRooms, usedBeds, year, month FROM occupancies
                WHERE hotel_id = :hotelId
                  AND (:yearFrom  IS NULL OR year  >= :yearFrom)
                  AND (:yearTo    IS NULL OR year  <= :yearTo)
                  AND (:monthFrom IS NULL OR month >= :monthFrom)
                  AND (:monthTo   IS NULL OR month <= :monthTo)
                ORDER BY year, month
                """)
                .param("hotelId", hotelId)
                .param("yearFrom", yearFrom)
                .param("yearTo", yearTo)
                .param("monthFrom", monthFrom)
                .param("monthTo", monthTo)
                .query(Occupancy.class)
                .list();
    }

    public Occupancy addNewOccupancy(Occupancy occupancy) {
        jdbc.sql("""
                INSERT INTO occupancies (hotel_id, rooms, usedRooms, usedBeds, year, month)
                VALUES (:hotel_id, :rooms, :usedRooms, :usedBeds, :year, :month)
                """)
                .param("hotel_id", occupancy.hid())
                .param("rooms", occupancy.rooms())
                .param("usedRooms", occupancy.usedRooms())
                .param("usedBeds", occupancy.usedBeds())
                .param("year", occupancy.year())
                .param("month", occupancy.month())
                .update();
        return occupancy;
    }
}
