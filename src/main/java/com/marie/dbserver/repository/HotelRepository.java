package com.marie.dbserver.repository;

import com.marie.dbserver.model.Hotel;
import com.marie.dbserver.model.HotelSizeStats;
import com.marie.dbserver.model.OccupancySummary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HotelRepository {

    private final JdbcClient jdbc;

    // Constructor Injection - Spring gibt dir den JdbcClient automatisch
    public HotelRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Alle Hotels holen.
     * Äquivalent zu: SELECT * FROM hotels
     */
    public List<Hotel> findAllHotels() {
        return jdbc.sql("select * from hotels")
                .query(Hotel.class)
                .list();
    }

    public List<Hotel> findByIds(List<Integer> ids) {
        return jdbc.sql("SELECT * FROM hotels WHERE id IN (:ids)")
                .param("ids", ids)
                .query(Hotel.class)
                .list();
    }

    /**
     * Ein Hotel per ID holen
     */
    public List<Hotel> findById(int id) {
        return jdbc.sql("select * from hotels where id = :id")
                .param("id", id)
                .query(Hotel.class)
                .list();
    }

    /**
     * Average Hotel size bekommen (optional gefiltert nach hotel, year, category >=, month)
     */
    public List<HotelSizeStats> getHotelSizeAvg(Integer hotelId, Integer year, Integer category, Integer month) {
        // checkt ob ein Join required ist in der SQL Query
        boolean needsBookingJoin = (year != null || month != null);

        // StringBuilder wird aufgerufen um eine neue SQL Query zu erstellen, die im Nachhinein noch verändert werden kann (Text adden)
        StringBuilder sql = new StringBuilder("""
                SELECT h.name, h.category AS rating, AVG(h.noRooms + h.noBeds) AS average_size
                FROM hotels h
                """);

        // Falls Join required ist
        if (needsBookingJoin) {
            sql.append(" JOIN bookings b ON b.hotel_id = h.id\n");
        }

        // where - clause
        sql.append(" WHERE 1=1\n");

        // Hashmap erstellen für die Parameter
        // Hashmap besteht aus einem Key/Value Pair --> einzigartiger Key deutet auf Value, welche durch den Key aufgerufen werden kann
        Map<String, Object> params = new HashMap<>();

        if (hotelId != null) {
            sql.append(" AND h.id = :hotelId\n");
            params.put("hotelId", hotelId);
        }
        if (category != null) {
            sql.append(" AND LEN(h.category) >= :category\n");
            params.put("category", category);
        }
        if (year != null) {
            sql.append(" AND YEAR(b.checkin_date) = :year\n");
            params.put("year", year);
        }
        if (month != null) {
            sql.append(" AND MONTH(b.checkin_date) = :month\n");
            params.put("month", month);
        }

        //Group by und Order By in die oben erstellte SQL Query einfügen
        sql.append(" GROUP BY h.category, h.name\n");
        sql.append(" ORDER BY h.category DESC\n");

        // jdbc sagen das die Variable "spec" im Aufruf für den Server die SQL query die erstellt wurde für die Datenbank gedacht ist
        JdbcClient.StatementSpec spec = jdbc.sql(sql.toString());

        //Durch die Hashmap loopen, alle Keys und Values auslesen und anschließend als Parameter in die SQL Request einfügen
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.param(entry.getKey(), entry.getValue());
        }

        // query ausführen
        return spec.query(HotelSizeStats.class).list();
    }

    /**
     * Hotels nach Kategorie (Anzahl an Sternen) filtern
     */
    public List<Hotel> findbyCategory(String category) {
        StringBuilder sqlQuery = new StringBuilder("""
                select * from hotels
                """);

        if (category != null) {
            sqlQuery.append(" where category = :category\n");
        }


        JdbcClient.StatementSpec spec = jdbc.sql(sqlQuery.toString());
        spec.param("category", category);

        return spec.query(Hotel.class).list();
    }

    /**
     * Neues Hotel einfügen
     * "OUTPUT" sorgt dafür, dass das eingefügte Hotel angezeigt wird in der Response
     */
    public Hotel save(Hotel hotel) {
        Integer newId = jdbc.sql("""
                INSERT INTO hotels (id, noRooms, noBeds, category, name, owner, contact, address, city, cityCode, phone, tags)
                OUTPUT INSERTED.id
                VALUES (:id, :noRooms, :noBeds, :category, :name, :owner, :contact, :address, :city, :cityCode, :phone, :tags)
                """)
                .param("id", hotel.id())
                .param("noRooms", hotel.noRooms())
                .param("noBeds", hotel.noBeds())
                .param("category", hotel.category())
                .param("name", hotel.name())
                .param("address", hotel.address())
                .param("owner", hotel.owner())
                .param("contact", hotel.contact())
                .param("city", hotel.city())
                .param("cityCode", hotel.cityCode())
                .param("phone", hotel.phone())
                .param("tags", hotel.tags())

                .query(Integer.class)
                .single();


        return new Hotel(
                newId, hotel.noRooms(), hotel.noBeds(), hotel.category(), hotel.name(), hotel.owner(), hotel.contact(), hotel.address(), hotel.city(), hotel.cityCode(), hotel.phone(), hotel.tags()
        );
    }

    /**
     * Hotel updaten.
     */
    public int updateHotelRating(int id, Hotel hotel) {
        return jdbc.sql("""
                UPDATE hotels
                SET category = :category
                WHERE id = :id
                """)
                .param("id", id)
                .param("category", hotel.category())
                .update();
    }

    public int updateHotelTags(int id, Hotel hotel){
        return jdbc.sql("""
                UPDATE hotels
                SET tags = :tags
                WHERE id = :id
                """)
                .param("id", id)
                .param("tags", hotel.tags())
                .update();
    }

    /**
     * Hotel löschen.
     */
    public int deleteById(int id) {
        return jdbc.sql("DELETE FROM hotels WHERE id = :id")
                .param("id", id)
                .update();
    }

    public List<OccupancySummary> getOccupancySummary(Integer hotelId, Integer year, Integer category, Integer month) {
        //sql query über nen StringBuilder machen um später noch mehr Details ins Statement einbauen zu können
        StringBuilder sql = new StringBuilder("""
                SELECT h.name, h.category,
                       YEAR(b.checkin_date)  AS year,
                       MONTH(b.checkin_date) AS month,
                       SUM(b.rooms_occupied) AS total_rooms_occupied,
                       SUM(b.beds_occupied)  AS total_beds_occupied
                FROM bookings b
                JOIN hotels h ON b.hotel_id = h.id
                WHERE 1=1
                """);

        //Hashmap erstellen um den Parametern eine eindeutige Value und einen eindeutigen Key zu geben
        Map<String, Object> params = new HashMap<>();

        //if checks um zu schauen, welche Parameter verwendet werden
        if (hotelId != null) {
            sql.append(" AND h.id = :hotelId");
            params.put("hotelId", hotelId);
        }
        if (year != null) {
            sql.append(" AND YEAR(b.checkin_date) = :year");
            params.put("year", year);
        }
        if (category != null) {
            sql.append(" AND LEN(h.category) >= :category");
            params.put("category", category);
        }
        if (month != null) {
            sql.append(" AND MONTH(b.checkin_date) = :month");
            params.put("month", month);
        }

        sql.append("""

                GROUP BY h.name, h.category, YEAR(b.checkin_date), MONTH(b.checkin_date)
                ORDER BY YEAR(b.checkin_date), MONTH(b.checkin_date)
                """);

        //die SQL Request readable machen für den Server
        JdbcClient.StatementSpec spec = jdbc.sql(sql.toString());

        //Durch die Hashmap loopen, alle Keys und Values auslesen und anschließend als Parameter in die SQL Request einfügen
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.param(entry.getKey(), entry.getValue());
        }
        return spec.query(OccupancySummary.class).list();
    }
}
