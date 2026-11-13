package com.example.dispatch.dto;
import com.example.dispatch.entity.Priority;
public record AssignedOrder(String orderId, double latitude, double longitude, String address, double packageWeight, Priority priority) {}
