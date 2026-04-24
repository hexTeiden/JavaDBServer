package com.marie.dbserver.repository;

import com.marie.dbserver.model.Hotel;
import com.marie.dbserver.model.HotelSizeStats;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * Average Hotel size bekommen
     */

    public List<HotelSizeStats> getHotelSizeAvg() {
        return jdbc.sql("""
        select name, category as rating, avg(noRooms + noBeds) as average_size
        from hotels
        group bycategory
        order by category desc;
        """)
                .query(HotelSizeStats.class)
                .list();
    }

    /**
     * Hotels nach Kategorie (Anzahl an Sternen) filtern
     */
    public List<Hotel> findbyCategory(String category) {
        return jdbc.sql("select * from hotels where category = :category")
                .param("category", category)
                .query(Hotel.class)
                .list();
    }

    /**
     * Neues Hotel einfügen
     * "OUTPUT" sorgt dafür, dass das eingefügte Hotel angezeigt wird in der Response
     */
    public Hotel save(Hotel hotel) {
        Integer newId = jdbc.sql("""
                INSERT INTO hotel (noRooms, noBeds, category, name, owner, contact, address, city, cityCode, phone)
                OUTPUT INSERTED.id
                VALUES (:category, :name, :owner, :contact, :address, :city, :cityCode, :phone, :noRooms, :noBeds)
                """)
                .param("noRooms", hotel.noRooms())
                .param("noBeds", hotel.noBeds())
                .param("category", hotel.category())
                .param("name", hotel.name())
                .param("owner", hotel.owner())
                .param("contact", hotel.contact())
                .param("city", hotel.city())
                .param("cityCode", hotel.cityCode())
                .param("phone", hotel.phone())

                .query(Integer.class)
                .single();

        return new Hotel(newId, hotel.noRooms(), hotel.noBeds(), hotel.category(), hotel.name(), hotel.owner(), hotel.contact(), hotel.address(), hotel.city(), hotel.cityCode(), hotel.phone());
    }

    /**
     * Operator updaten.
     */
    public int updateHotelRating(int id, Hotel hotel) {
        return jdbc.sql("""
                UPDATE hotel
                SET category = :category,
                WHERE id = :id
                """)
                .param("id", id)
                .param("category", hotel.category())
                .update();
    }

    /**
     * Operator löschen.
     */
    public int deleteById(int id) {
        return jdbc.sql("DELETE FROM hotels WHERE id = :id")
                .param("id", id)
                .update();
    }

    /**
     * BONUS: Beispiel für ein T-SQL-spezifisches Feature (Window Function).
     * Zeigt, wie viele Operator pro Seite es gibt, mit Ranking.
     */
    public List<Hotel> findTopBySpeed() {
        return jdbc.sql("""
                SELECT TOP 5 id, category, name, owner, contact
                FROM hotels
                ORDER BY category DESC 
                """)
                .query(Hotel.class)
                .list();
    }
}
