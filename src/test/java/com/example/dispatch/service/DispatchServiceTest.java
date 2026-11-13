package com.example.dispatch.service;

import com.example.dispatch.dto.*;
import com.example.dispatch.entity.*;
import com.example.dispatch.repository.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchServiceTest {
    private final OrderRepository orders = mock(OrderRepository.class);
    private final VehicleRepository vehicles = mock(VehicleRepository.class);
    private final DispatchService service = new DispatchService(orders, vehicles);

    @Test void assignsHighPriorityFirstAndNeverExceedsCapacity() {
        when(orders.findAll()).thenReturn(List.of(
                new DeliveryOrder("LOW", 0, 0.01, "Low", 6, Priority.LOW),
                new DeliveryOrder("HIGH", 0, 0.02, "High", 6, Priority.HIGH)));
        when(vehicles.findAll()).thenReturn(List.of(new Vehicle("V1", 10, 0, 0, "Base")));
        DispatchPlanResponse response = service.createPlan();
        assertEquals("HIGH", response.dispatchPlan().get(0).assignedOrders().get(0).orderId());
        assertEquals(6.0, response.dispatchPlan().get(0).totalLoad());
        assertEquals("LOW", response.unassignedOrders().get(0).orderId());
    }

    @Test void assignsOrderToNearestEligibleVehicle() {
        when(orders.findAll()).thenReturn(List.of(new DeliveryOrder("O1", 0, 0.1, "Drop", 2, Priority.HIGH)));
        when(vehicles.findAll()).thenReturn(List.of(new Vehicle("FAR", 10, 0, 1, "Far"), new Vehicle("NEAR", 10, 0, 0.11, "Near")));
        DispatchPlanResponse response = service.createPlan();
        var nearest = response.dispatchPlan().stream().filter(v -> !v.assignedOrders().isEmpty()).findFirst().orElseThrow();
        assertEquals("NEAR", nearest.vehicleId());
    }

    @Test void reportsOrdersUnassignedWhenNoVehiclesExist() {
        when(orders.findAll()).thenReturn(List.of(new DeliveryOrder("O1", 0, 0, "Drop", 1, Priority.HIGH)));
        when(vehicles.findAll()).thenReturn(List.of());
        assertTrue(service.createPlan().unassignedOrders().get(0).reason().contains("No vehicles"));
    }
}
