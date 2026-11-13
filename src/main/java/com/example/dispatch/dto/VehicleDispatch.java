package com.example.dispatch.dto;
import java.util.List;
public record VehicleDispatch(String vehicleId, double totalLoad, String totalDistance, List<AssignedOrder> assignedOrders) {}
