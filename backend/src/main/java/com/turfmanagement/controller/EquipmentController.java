package com.turfmanagement.controller;

import com.turfmanagement.model.Equipment;
import com.turfmanagement.model.Order;
import com.turfmanagement.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        List<Equipment> equipment = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable String id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        if (equipment != null) {
            return ResponseEntity.ok(equipment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Equipment>> getEquipmentByCategory(@PathVariable String category) {
        List<Equipment> equipment = equipmentService.getEquipmentByCategory(category);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipment() {
        List<Equipment> equipment = equipmentService.getAvailableEquipment();
        return ResponseEntity.ok(equipment);
    }

    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        Equipment createdEquipment = equipmentService.createEquipment(equipment);
        return ResponseEntity.ok(createdEquipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable String id, @RequestBody Equipment equipment) {
        Equipment updatedEquipment = equipmentService.updateEquipment(id, equipment);
        if (updatedEquipment != null) {
            return ResponseEntity.ok(updatedEquipment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<String> updateStock(@PathVariable String id, @RequestParam int quantity) {
        boolean updated = equipmentService.updateStock(id, quantity);
        if (updated) {
            return ResponseEntity.ok("Stock updated successfully");
        }
        return ResponseEntity.badRequest().body("Failed to update stock");
    }

    // Order endpoints
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = equipmentService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = equipmentService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        Order order = equipmentService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        Order order = equipmentService.updateOrderStatus(id, status);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        boolean cancelled = equipmentService.cancelOrder(id);
        if (cancelled) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Initialize sample data
    @PostMapping("/init")
    public ResponseEntity<String> initializeData() {
        equipmentService.initializeSampleData();
        return ResponseEntity.ok("Sample equipment data initialized successfully");
    }
} 