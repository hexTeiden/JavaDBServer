package com.marie.dbserver.controller;

import com.marie.dbserver.model.Hotel;
import com.marie.dbserver.model.HotelSizeStats;
import com.marie.dbserver.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    //GET /api/hotels/size
    @GetMapping("/size")
    public List<HotelSizeStats> getHotelSizeAvg() {
        return repo.getHotelSizeAvg();
    }

    //GET /api/hotels/{id}
    @GetMapping("/{id:\\\\d+}")
    public List<Hotel> getHotelByID (@PathVariable int id) {
        return repo.findById(id);
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
