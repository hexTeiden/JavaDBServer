package com.marie.dbserver.controller;

import com.marie.dbserver.model.Hotel;
import com.marie.dbserver.model.HotelOccupancyView;
import com.marie.dbserver.model.Occupancy;
import com.marie.dbserver.repository.HotelRepository;
import com.marie.dbserver.repository.OccupancyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/occupancies")
public class OccupancyController {

    private final OccupancyRepository occupancyRepo;
    private final HotelRepository hotelRepo;

    public OccupancyController(OccupancyRepository repo, HotelRepository hotelRepo) {
        this.occupancyRepo = repo;
        this.hotelRepo = hotelRepo;
    }

    @GetMapping
    public ResponseEntity<List<Occupancy>> getAll(){
        return ResponseEntity.ok(occupancyRepo.getAllOccupancies());
    }

    // GET /api/occupancies/{hotelId}?yearFrom=2024&yearTo=2025&monthFrom=1&monthTo=6
    // Returns hotel id + name and occupancy records, optionally filtered by year/month range.
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelOccupancyView> getForHotel(
            @PathVariable int hotelId,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer monthFrom,
            @RequestParam(required = false) Integer monthTo
    ) {
        List<Hotel> hotels = hotelRepo.findById(hotelId);
        if (hotels.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Hotel hotel = hotels.getFirst();
        List<Occupancy> occupancies = occupancyRepo.findByHotelAndPeriod(hotelId, yearFrom, yearTo, monthFrom, monthTo);
        return ResponseEntity.ok(new HotelOccupancyView(hotel.id(), hotel.name(), occupancies));
    }

    // POST /api/occupancies/{hotelId}
    // Adds new transactional data (rooms, usedRooms, usedBeds, year, month) for a hotel.
    // hotelId comes from the path — the body must not override it.
    @PostMapping("/{hotelId}")
    public ResponseEntity<Occupancy> create(@PathVariable int hotelId, @RequestBody Occupancy body) {
        List<Hotel> hotels = hotelRepo.findById(hotelId);
        if (hotels.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Occupancy toSave = new Occupancy(hotelId, body.rooms(), body.usedRooms(), body.usedBeds(), body.year(), body.month());
        Occupancy saved = occupancyRepo.save(toSave);
        return ResponseEntity.status(201).body(saved);
    }
}
