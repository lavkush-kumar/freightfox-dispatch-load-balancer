package com.example.dispatch.service;

import com.example.dispatch.dto.*;
import com.example.dispatch.entity.*;
import com.example.dispatch.repository.OrderRepository;
import com.example.dispatch.repository.VehicleRepository;
import com.example.dispatch.util.DistanceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DispatchService {
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    public DispatchService(OrderRepository orderRepository, VehicleRepository vehicleRepository) {
        this.orderRepository = orderRepository; this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public ApiMessage saveOrders(OrdersRequest request) {
        ensureUnique(request.orders().stream().map(OrderInput::orderId).toList(), "orderId");
        var entities = request.orders().stream().map(o -> new DeliveryOrder(o.orderId(), o.latitude(), o.longitude(), o.address().trim(), o.packageWeight(), o.priority())).toList();
        orderRepository.saveAll(entities);
        return new ApiMessage("Delivery orders accepted.", "success");
    }

    @Transactional
    public ApiMessage saveVehicles(VehiclesRequest request) {
        ensureUnique(request.vehicles().stream().map(VehicleInput::vehicleId).toList(), "vehicleId");
        var entities = request.vehicles().stream().map(v -> new Vehicle(v.vehicleId(), v.capacity(), v.currentLatitude(), v.currentLongitude(), v.currentAddress().trim())).toList();
        vehicleRepository.saveAll(entities);
        return new ApiMessage("Vehicle details accepted.", "success");
    }

    @Transactional(readOnly = true)
    public DispatchPlanResponse createPlan() {
        List<DeliveryOrder> orders = orderRepository.findAll().stream()
                .sorted(Comparator.comparingInt((DeliveryOrder o) -> priorityRank(o.getPriority())).thenComparing(DeliveryOrder::getOrderId))
                .toList();
        List<VehicleState> states = vehicleRepository.findAll().stream().map(VehicleState::new)
                .sorted(Comparator.comparing(s -> s.vehicle.getVehicleId())).toList();
        List<UnassignedOrder> unassigned = new ArrayList<>();

        for (DeliveryOrder order : orders) {
            VehicleState best = states.stream()
                    .filter(v -> v.remainingCapacity + 1e-9 >= order.getPackageWeight())
                    .min(Comparator.comparingDouble((VehicleState v) -> v.distanceTo(order))
                            .thenComparing(v -> v.vehicle.getVehicleId()))
                    .orElse(null);
            if (best == null) {
                String reason = states.isEmpty() ? "No vehicles are registered." : "No vehicle has enough remaining capacity.";
                unassigned.add(new UnassignedOrder(order.getOrderId(), reason));
                continue;
            }
            double leg = best.distanceTo(order);
            best.totalDistanceKm += leg;
            best.remainingCapacity -= order.getPackageWeight();
            best.totalLoad += order.getPackageWeight();
            best.latitude = order.getLatitude(); best.longitude = order.getLongitude();
            best.assigned.add(new AssignedOrder(order.getOrderId(), order.getLatitude(), order.getLongitude(), order.getAddress(), order.getPackageWeight(), order.getPriority()));
        }

        List<VehicleDispatch> plan = states.stream().map(v -> new VehicleDispatch(v.vehicle.getVehicleId(), round(v.totalLoad), String.format(Locale.ROOT, "%.2f km", v.totalDistanceKm), List.copyOf(v.assigned))).toList();
        return new DispatchPlanResponse(plan, List.copyOf(unassigned),
                "Priority-first, nearest-next heuristic using Haversine distance. Distances are one-way route legs; return-to-depot distance and road-network routing are not included. This is a practical heuristic, not a proof of global optimality.");
    }

    private static int priorityRank(Priority p) { return switch (p) { case HIGH -> 0; case MEDIUM -> 1; case LOW -> 2; }; }
    private static double round(double n) { return Math.round(n * 100.0) / 100.0; }
    private static void ensureUnique(List<String> ids, String field) {
        if (new HashSet<>(ids).size() != ids.size()) throw new IllegalArgumentException("Duplicate " + field + " values in request.");
    }
    private static final class VehicleState {
        final Vehicle vehicle; double remainingCapacity, totalLoad, totalDistanceKm, latitude, longitude;
        final List<AssignedOrder> assigned = new ArrayList<>();
        VehicleState(Vehicle vehicle) { this.vehicle = vehicle; remainingCapacity = vehicle.getCapacity(); latitude = vehicle.getCurrentLatitude(); longitude = vehicle.getCurrentLongitude(); }
        double distanceTo(DeliveryOrder order) { return DistanceCalculator.haversineKm(latitude, longitude, order.getLatitude(), order.getLongitude()); }
    }
}
