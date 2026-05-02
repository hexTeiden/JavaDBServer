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

    // GET api/occupancy
    @GetMapping
    public ResponseEntity<List<Occupancy>> getAll(){
        return ResponseEntity.ok(occupancyRepo.getAllOccupancies());
    }

    //US 10

    // GET /api/occupancies/{hotelId}?yearFrom=2024&yearTo=2025&monthFrom=1&monthTo=6
    // Returned hotelid, name und occupancy Einträge, optional ist filtern nach Jahr/Monats Intervall (z.B. 2024-2025, Mai-August, März 2023-Juli 2023)
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

    // War nicht gefragt, aber nicht schlecht dabei zu haben

    // POST /api/occupancies/{hotelId}
    // Fügt neue transactional data (Rooms, usedRooms, usedBeds, year, month) zu nem Hotel hinzu
    // hotelId kommt aus dem Path --> auf keinen Fall überschreiben
    @PostMapping("/{hotelId}")
    public ResponseEntity<Occupancy> create(@PathVariable int hotelId, @RequestBody Occupancy body) {
        List<Hotel> hotels = hotelRepo.findById(hotelId);
        if (hotels.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Occupancy toSave = new Occupancy(hotelId, body.rooms(), body.usedRooms(), body.usedBeds(), body.year(), body.month());
        Occupancy saved = occupancyRepo.addNewOccupancy(toSave);
        return ResponseEntity.status(201).body(saved);
    }
}
