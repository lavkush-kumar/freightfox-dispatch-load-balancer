package com.example.dispatch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_orders", indexes = @Index(name = "idx_order_priority", columnList = "priority"))
public class DeliveryOrder {
    @Id @Column(length = 64)
    private String orderId;
    @Column(nullable = false) private double latitude;
    @Column(nullable = false) private double longitude;
    @Column(nullable = false, length = 500) private String address;
    @Column(nullable = false) private double packageWeight;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private Priority priority;

    protected DeliveryOrder() {}
    public DeliveryOrder(String orderId, double latitude, double longitude, String address, double packageWeight, Priority priority) {
        this.orderId = orderId; this.latitude = latitude; this.longitude = longitude; this.address = address; this.packageWeight = packageWeight; this.priority = priority;
    }
    public String getOrderId() { return orderId; } public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; } public String getAddress() { return address; }
    public double getPackageWeight() { return packageWeight; } public Priority getPriority() { return priority; }
}
