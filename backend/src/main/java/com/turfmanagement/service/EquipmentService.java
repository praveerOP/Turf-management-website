package com.turfmanagement.service;

import com.turfmanagement.model.Equipment;
import com.turfmanagement.model.Order;
import com.turfmanagement.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EquipmentService {

    @Autowired
    private RedisService redisService;

    public List<Equipment> getAllEquipment() {
        return redisService.getAllEquipment();
    }

    public Equipment getEquipmentById(String id) {
        return redisService.getEquipment(id);
    }

    public Equipment createEquipment(Equipment equipment) {
        if (equipment.getId() == null) {
            equipment.setId(UUID.randomUUID().toString());
        }
        redisService.saveEquipment(equipment);
        return equipment;
    }

    public Equipment updateEquipment(String id, Equipment equipment) {
        Equipment existingEquipment = redisService.getEquipment(id);
        if (existingEquipment != null) {
            equipment.setId(id);
            redisService.saveEquipment(equipment);
            return equipment;
        }
        return null;
    }

    public boolean deleteEquipment(String id) {
        Equipment equipment = redisService.getEquipment(id);
        if (equipment != null) {
            redisService.deleteEquipment(id);
            return true;
        }
        return false;
    }

    public List<Equipment> getEquipmentByCategory(String category) {
        List<Equipment> allEquipment = getAllEquipment();
        return allEquipment.stream()
                .filter(equipment -> equipment.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public List<Equipment> getAvailableEquipment() {
        List<Equipment> allEquipment = getAllEquipment();
        return allEquipment.stream()
                .filter(Equipment::isAvailable)
                .filter(equipment -> equipment.getStockQuantity() > 0)
                .toList();
    }

    public boolean updateStock(String equipmentId, int quantity) {
        Equipment equipment = redisService.getEquipment(equipmentId);
        if (equipment != null) {
            int newStock = equipment.getStockQuantity() + quantity;
            if (newStock >= 0) {
                equipment.setStockQuantity(newStock);
                equipment.setAvailable(newStock > 0);
                redisService.saveEquipment(equipment);
                return true;
            }
        }
        return false;
    }

    public Order createOrder(Order order) {
        // Validate order
        if (!isOrderValid(order)) {
            throw new IllegalArgumentException("Invalid order data");
        }

        // Check stock availability
        for (OrderItem item : order.getItems()) {
            Equipment equipment = redisService.getEquipment(item.getEquipmentId());
            if (equipment == null || equipment.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for equipment: " + item.getEquipmentName());
            }
        }

        // Generate order ID and set order date
        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            Equipment equipment = redisService.getEquipment(item.getEquipmentId());
            if (equipment != null) {
                item.setUnitPrice(equipment.getPrice());
                item.setTotalPrice(equipment.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                subtotal = subtotal.add(item.getTotalPrice());
            }
        }
        order.setSubtotal(subtotal);
        
        // Calculate tax (10%)
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10"));
        order.setTax(tax);
        
        // Calculate total
        BigDecimal total = subtotal.add(tax);
        order.setTotalAmount(total);

        // Save order
        redisService.saveOrder(order);

        // Update stock
        for (OrderItem item : order.getItems()) {
            updateStock(item.getEquipmentId(), -item.getQuantity());
        }

        return order;
    }

    public Order getOrderById(String id) {
        return redisService.getOrder(id);
    }

    public List<Order> getAllOrders() {
        return redisService.getAllOrders();
    }

    public Order updateOrderStatus(String orderId, String status) {
        Order order = redisService.getOrder(orderId);
        if (order != null) {
            order.setStatus(status);
            redisService.saveOrder(order);
            return order;
        }
        return null;
    }

    public boolean cancelOrder(String orderId) {
        Order order = redisService.getOrder(orderId);
        if (order != null && "PENDING".equals(order.getStatus())) {
            // Restore stock
            for (OrderItem item : order.getItems()) {
                updateStock(item.getEquipmentId(), item.getQuantity());
            }
            
            order.setStatus("CANCELLED");
            redisService.saveOrder(order);
            return true;
        }
        return false;
    }

    private boolean isOrderValid(Order order) {
        return order.getCustomerName() != null &&
               order.getCustomerEmail() != null &&
               order.getCustomerPhone() != null &&
               order.getItems() != null &&
               !order.getItems().isEmpty();
    }

    // Initialize sample data
    public void initializeSampleData() {
        if (getAllEquipment().isEmpty()) {
            // Sporting Equipment
            createEquipment(new Equipment("1", "Football", "SPORTING_EQUIPMENT", 
                                        "Professional football", new BigDecimal("25.00"), 10, "/images/football.jpg", true));
            createEquipment(new Equipment("2", "Cricket Bat", "SPORTING_EQUIPMENT", 
                                        "Professional cricket bat", new BigDecimal("45.00"), 8, "/images/cricket-bat.jpg", true));
            createEquipment(new Equipment("3", "Tennis Racket", "SPORTING_EQUIPMENT", 
                                        "Professional tennis racket", new BigDecimal("35.00"), 12, "/images/tennis-racket.jpg", true));
            createEquipment(new Equipment("4", "Basketball", "SPORTING_EQUIPMENT", 
                                        "Professional basketball", new BigDecimal("30.00"), 15, "/images/basketball.jpg", true));
            
            // Energy Drinks
            createEquipment(new Equipment("5", "Red Bull", "ENERGY_DRINKS", 
                                        "Energy drink 250ml", new BigDecimal("3.50"), 50, "/images/red-bull.jpg", true));
            createEquipment(new Equipment("6", "Monster Energy", "ENERGY_DRINKS", 
                                        "Energy drink 500ml", new BigDecimal("4.50"), 40, "/images/monster.jpg", true));
            createEquipment(new Equipment("7", "Powerade", "ENERGY_DRINKS", 
                                        "Sports drink 500ml", new BigDecimal("2.50"), 60, "/images/powerade.jpg", true));
            createEquipment(new Equipment("8", "Gatorade", "ENERGY_DRINKS", 
                                        "Sports drink 500ml", new BigDecimal("2.75"), 55, "/images/gatorade.jpg", true));
            
            // Accessories
            createEquipment(new Equipment("9", "Sports Bag", "ACCESSORIES", 
                                        "Large sports bag", new BigDecimal("20.00"), 20, "/images/sports-bag.jpg", true));
            createEquipment(new Equipment("10", "Water Bottle", "ACCESSORIES", 
                                         "1L water bottle", new BigDecimal("8.00"), 30, "/images/water-bottle.jpg", true));
        }
    }
} 