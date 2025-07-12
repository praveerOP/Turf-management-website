package com.turfmanagement.controller;

import com.turfmanagement.model.Booking;
import com.turfmanagement.model.Turf;
import com.turfmanagement.service.TurfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turfs")
@CrossOrigin(origins = "*")
public class TurfController {

    @Autowired
    private TurfService turfService;

    @GetMapping
    public ResponseEntity<List<Turf>> getAllTurfs() {
        List<Turf> turfs = turfService.getAllTurfs();
        return ResponseEntity.ok(turfs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turf> getTurfById(@PathVariable String id) {
        Turf turf = turfService.getTurfById(id);
        if (turf != null) {
            return ResponseEntity.ok(turf);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<Turf>> getAvailableTurfs() {
        List<Turf> turfs = turfService.getAvailableTurfs();
        return ResponseEntity.ok(turfs);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Turf>> getTurfsByType(@PathVariable String type) {
        List<Turf> turfs = turfService.getTurfsByType(type);
        return ResponseEntity.ok(turfs);
    }

    @PostMapping
    public ResponseEntity<Turf> createTurf(@RequestBody Turf turf) {
        Turf createdTurf = turfService.createTurf(turf);
        return ResponseEntity.ok(createdTurf);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turf> updateTurf(@PathVariable String id, @RequestBody Turf turf) {
        Turf updatedTurf = turfService.updateTurf(id, turf);
        if (updatedTurf != null) {
            return ResponseEntity.ok(updatedTurf);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurf(@PathVariable String id) {
        boolean deleted = turfService.deleteTurf(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Booking endpoints
    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        try {
            Booking createdBooking = turfService.createBooking(booking);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = turfService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Booking booking = turfService.getBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{turfId}/bookings")
    public ResponseEntity<List<Booking>> getBookingsByTurfId(@PathVariable String turfId) {
        List<Booking> bookings = turfService.getBookingsByTurfId(turfId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable String id, @RequestParam String status) {
        Booking booking = turfService.updateBookingStatus(id, status);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        boolean cancelled = turfService.cancelBooking(id);
        if (cancelled) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Initialize sample data
    @PostMapping("/init")
    public ResponseEntity<String> initializeData() {
        turfService.initializeSampleData();
        return ResponseEntity.ok("Sample data initialized successfully");
    }
} 