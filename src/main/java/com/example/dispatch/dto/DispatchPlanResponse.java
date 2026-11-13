package com.example.dispatch.dto;
import java.util.List;
public record DispatchPlanResponse(List<VehicleDispatch> dispatchPlan, List<UnassignedOrder> unassignedOrders, String optimizationNote) {}
