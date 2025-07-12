package com.turfmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turfmanagement.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TURF_KEY_PREFIX = "turf:";
    private static final String BOOKING_KEY_PREFIX = "booking:";
    private static final String EQUIPMENT_KEY_PREFIX = "equipment:";
    private static final String ORDER_KEY_PREFIX = "order:";
    private static final String ALL_TURFS_KEY = "all_turfs";
    private static final String ALL_EQUIPMENT_KEY = "all_equipment";
    private static final String ALL_BOOKINGS_KEY = "all_bookings";
    private static final String ALL_ORDERS_KEY = "all_orders";

    // Turf Operations
    public void saveTurf(Turf turf) {
        String key = TURF_KEY_PREFIX + turf.getId();
        redisTemplate.opsForValue().set(key, turf, Duration.ofHours(24));
        redisTemplate.opsForSet().add(ALL_TURFS_KEY, turf.getId());
    }

    public Turf getTurf(String id) {
        String key = TURF_KEY_PREFIX + id;
        return (Turf) redisTemplate.opsForValue().get(key);
    }

    public List<Turf> getAllTurfs() {
        Set<Object> turfIds = redisTemplate.opsForSet().members(ALL_TURFS_KEY);
        List<Turf> turfs = new ArrayList<>();
        if (turfIds != null) {
            for (Object turfId : turfIds) {
                Turf turf = getTurf(turfId.toString());
                if (turf != null) {
                    turfs.add(turf);
                }
            }
        }
        return turfs;
    }

    public void deleteTurf(String id) {
        String key = TURF_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ALL_TURFS_KEY, id);
    }

    // Booking Operations
    public void saveBooking(Booking booking) {
        String key = BOOKING_KEY_PREFIX + booking.getId();
        redisTemplate.opsForValue().set(key, booking, Duration.ofDays(30));
        redisTemplate.opsForSet().add(ALL_BOOKINGS_KEY, booking.getId());
    }

    public Booking getBooking(String id) {
        String key = BOOKING_KEY_PREFIX + id;
        return (Booking) redisTemplate.opsForValue().get(key);
    }

    public List<Booking> getAllBookings() {
        Set<Object> bookingIds = redisTemplate.opsForSet().members(ALL_BOOKINGS_KEY);
        List<Booking> bookings = new ArrayList<>();
        if (bookingIds != null) {
            for (Object bookingId : bookingIds) {
                Booking booking = getBooking(bookingId.toString());
                if (booking != null) {
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    public void deleteBooking(String id) {
        String key = BOOKING_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ALL_BOOKINGS_KEY, id);
    }

    // Equipment Operations
    public void saveEquipment(Equipment equipment) {
        String key = EQUIPMENT_KEY_PREFIX + equipment.getId();
        redisTemplate.opsForValue().set(key, equipment, Duration.ofHours(24));
        redisTemplate.opsForSet().add(ALL_EQUIPMENT_KEY, equipment.getId());
    }

    public Equipment getEquipment(String id) {
        String key = EQUIPMENT_KEY_PREFIX + id;
        return (Equipment) redisTemplate.opsForValue().get(key);
    }

    public List<Equipment> getAllEquipment() {
        Set<Object> equipmentIds = redisTemplate.opsForSet().members(ALL_EQUIPMENT_KEY);
        List<Equipment> equipmentList = new ArrayList<>();
        if (equipmentIds != null) {
            for (Object equipmentId : equipmentIds) {
                Equipment equipment = getEquipment(equipmentId.toString());
                if (equipment != null) {
                    equipmentList.add(equipment);
                }
            }
        }
        return equipmentList;
    }

    public void deleteEquipment(String id) {
        String key = EQUIPMENT_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ALL_EQUIPMENT_KEY, id);
    }

    // Order Operations
    public void saveOrder(Order order) {
        String key = ORDER_KEY_PREFIX + order.getId();
        redisTemplate.opsForValue().set(key, order, Duration.ofDays(30));
        redisTemplate.opsForSet().add(ALL_ORDERS_KEY, order.getId());
    }

    public Order getOrder(String id) {
        String key = ORDER_KEY_PREFIX + id;
        return (Order) redisTemplate.opsForValue().get(key);
    }

    public List<Order> getAllOrders() {
        Set<Object> orderIds = redisTemplate.opsForSet().members(ALL_ORDERS_KEY);
        List<Order> orders = new ArrayList<>();
        if (orderIds != null) {
            for (Object orderId : orderIds) {
                Order order = getOrder(orderId.toString());
                if (order != null) {
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public void deleteOrder(String id) {
        String key = ORDER_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ALL_ORDERS_KEY, id);
    }

    // Cache Operations
    public void setCache(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Data Size Management (to stay under 25MB limit)
    public long getDataSize() {
        // This is a simplified approach - in production you'd want more sophisticated monitoring
        return redisTemplate.getConnectionFactory().getConnection().dbSize();
    }

    public void cleanupOldData() {
        // Clean up old bookings and orders (older than 90 days)
        List<Booking> bookings = getAllBookings();
        List<Order> orders = getAllOrders();
        
        // Implementation would check dates and remove old entries
        // This is a placeholder for the cleanup logic
    }
} 