package com.turfmanagement.service;

import com.turfmanagement.model.Booking;
import com.turfmanagement.model.Turf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TurfService {

    @Autowired
    private RedisService redisService;

    public List<Turf> getAllTurfs() {
        return redisService.getAllTurfs();
    }

    public Turf getTurfById(String id) {
        return redisService.getTurf(id);
    }

    public Turf createTurf(Turf turf) {
        if (turf.getId() == null) {
            turf.setId(UUID.randomUUID().toString());
        }
        redisService.saveTurf(turf);
        return turf;
    }

    public Turf updateTurf(String id, Turf turf) {
        Turf existingTurf = redisService.getTurf(id);
        if (existingTurf != null) {
            turf.setId(id);
            redisService.saveTurf(turf);
            return turf;
        }
        return null;
    }

    public boolean deleteTurf(String id) {
        Turf turf = redisService.getTurf(id);
        if (turf != null) {
            redisService.deleteTurf(id);
            return true;
        }
        return false;
    }

    public List<Turf> getAvailableTurfs() {
        List<Turf> allTurfs = getAllTurfs();
        return allTurfs.stream()
                .filter(Turf::isAvailable)
                .toList();
    }

    public List<Turf> getTurfsByType(String type) {
        List<Turf> allTurfs = getAllTurfs();
        return allTurfs.stream()
                .filter(turf -> turf.getType().equalsIgnoreCase(type))
                .toList();
    }

    public Booking createBooking(Booking booking) {
        // Validate booking
        if (!isBookingValid(booking)) {
            throw new IllegalArgumentException("Invalid booking data");
        }

        // Check if turf is available for the requested time
        if (!isTurfAvailableForTime(booking.getTurfId(), booking.getStartTime(), booking.getEndTime())) {
            throw new IllegalStateException("Turf is not available for the requested time");
        }

        // Generate booking ID and set booking date
        if (booking.getId() == null) {
            booking.setId(UUID.randomUUID().toString());
        }
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setPaymentStatus("PENDING");

        // Calculate total amount
        Turf turf = redisService.getTurf(booking.getTurfId());
        if (turf != null) {
            BigDecimal totalAmount = turf.getPricePerHour().multiply(BigDecimal.valueOf(booking.getHours()));
            booking.setTotalAmount(totalAmount);
        }

        // Save booking
        redisService.saveBooking(booking);

        // Update turf availability if needed
        updateTurfAvailability(booking.getTurfId(), false);

        return booking;
    }

    public Booking getBookingById(String id) {
        return redisService.getBooking(id);
    }

    public List<Booking> getAllBookings() {
        return redisService.getAllBookings();
    }

    public List<Booking> getBookingsByTurfId(String turfId) {
        List<Booking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getTurfId().equals(turfId))
                .toList();
    }

    public Booking updateBookingStatus(String bookingId, String status) {
        Booking booking = redisService.getBooking(bookingId);
        if (booking != null) {
            booking.setStatus(status);
            redisService.saveBooking(booking);
            return booking;
        }
        return null;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = redisService.getBooking(bookingId);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            redisService.saveBooking(booking);
            
            // Make turf available again
            updateTurfAvailability(booking.getTurfId(), true);
            return true;
        }
        return false;
    }

    private boolean isBookingValid(Booking booking) {
        return booking.getTurfId() != null &&
               booking.getCustomerName() != null &&
               booking.getCustomerEmail() != null &&
               booking.getStartTime() != null &&
               booking.getEndTime() != null &&
               booking.getStartTime().isBefore(booking.getEndTime()) &&
               booking.getStartTime().isAfter(LocalDateTime.now());
    }

    private boolean isTurfAvailableForTime(String turfId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> existingBookings = getBookingsByTurfId(turfId);
        
        return existingBookings.stream()
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()) || "PENDING".equals(booking.getStatus()))
                .noneMatch(booking -> 
                    (startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())));
    }

    private void updateTurfAvailability(String turfId, boolean available) {
        Turf turf = redisService.getTurf(turfId);
        if (turf != null) {
            turf.setAvailable(available);
            redisService.saveTurf(turf);
        }
    }

    // Initialize sample data
    public void initializeSampleData() {
        if (getAllTurfs().isEmpty()) {
            createTurf(new Turf("1", "Football Ground A", "football", "large", 
                               new BigDecimal("50.00"), true, "Professional football ground", "/images/football-a.jpg"));
            createTurf(new Turf("2", "Cricket Ground B", "cricket", "large", 
                               new BigDecimal("75.00"), true, "Professional cricket ground", "/images/cricket-b.jpg"));
            createTurf(new Turf("3", "Tennis Court C", "tennis", "medium", 
                               new BigDecimal("30.00"), true, "Professional tennis court", "/images/tennis-c.jpg"));
            createTurf(new Turf("4", "Basketball Court D", "basketball", "medium", 
                               new BigDecimal("25.00"), true, "Professional basketball court", "/images/basketball-d.jpg"));
        }
    }
} 