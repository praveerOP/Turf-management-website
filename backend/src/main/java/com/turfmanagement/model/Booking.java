package com.turfmanagement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking implements Serializable {
    private String id;
    private String turfId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int hours;
    private BigDecimal totalAmount;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private LocalDateTime bookingDate;
    private String paymentStatus; // PENDING, PAID, REFUNDED

    public Booking() {}

    public Booking(String id, String turfId, String customerName, String customerEmail, 
                   String customerPhone, LocalDateTime startTime, LocalDateTime endTime, 
                   int hours, BigDecimal totalAmount, String status, LocalDateTime bookingDate, String paymentStatus) {
        this.id = id;
        this.turfId = turfId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hours = hours;
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookingDate = bookingDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTurfId() { return turfId; }
    public void setTurfId(String turfId) { this.turfId = turfId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", turfId='" + turfId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", hours=" + hours +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", bookingDate=" + bookingDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
} 