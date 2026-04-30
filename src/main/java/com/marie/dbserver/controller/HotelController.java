package com.marie.dbserver.controller;

import com.marie.dbserver.model.Hotel;
import com.marie.dbserver.model.HotelSizeStats;
import com.marie.dbserver.model.OccupancySummary;
import com.marie.dbserver.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelRepository repo;

    public HotelController(HotelRepository repo) {
        this.repo = repo;
    }

    // GET /api/hotels
    @GetMapping
    public List<Hotel> getAll(@RequestParam(required = false) String category) {
        if (category != null) {
            return repo.findbyCategory(category);
        }
        return repo.findAllHotels();
    }


    // GET /api/hotels/size?hotelId=1&year=2024&category=3&month=6
    @GetMapping("/size")
    public List<HotelSizeStats> getHotelSizeAvg(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer month
    ) {
        return repo.getHotelSizeAvg(hotelId, year, category, month);
    }

    // GET /api/hotels/occupancy?hotelId=1&year=2024&category=3&month=6
    @GetMapping("/occupancy")
    public List<OccupancySummary> getOccupancy(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer month
    ) {
        return repo.getOccupancySummary(hotelId, year, category, month);
    }

    // GET /api/hotels/batch?ids=1,2,3
    @GetMapping("/batch")
    public ResponseEntity<List<Hotel>> getByIds(@RequestParam List<Integer> ids) {
        if (ids.size() < 2) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findByIds(ids));
    }

    // POST /api/hotels
    @PostMapping
    public ResponseEntity<Hotel> create(@RequestBody Hotel hotel) {
        Hotel saved = repo.save(hotel);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/hotels/5
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Hotel hotel) {
        int rowsChanged = repo.updateHotelRating(id, hotel);
        if (rowsChanged == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/hotel/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        int rowsChanged = repo.deleteById(id);
        if (rowsChanged == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
