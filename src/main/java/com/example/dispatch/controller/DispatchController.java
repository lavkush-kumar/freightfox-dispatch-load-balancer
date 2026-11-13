package com.example.dispatch.controller;

import com.example.dispatch.dto.*;
import com.example.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Delivery order and fleet dispatch operations")
public class DispatchController {
    private final DispatchService dispatchService;
    public DispatchController(DispatchService dispatchService) { this.dispatchService = dispatchService; }

    @PostMapping("/orders") @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create or update delivery orders")
    public ApiMessage saveOrders(@Valid @RequestBody OrdersRequest request) { return dispatchService.saveOrders(request); }

    @PostMapping("/vehicles") @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create or update fleet vehicles")
    public ApiMessage saveVehicles(@Valid @RequestBody VehiclesRequest request) { return dispatchService.saveVehicles(request); }

    @GetMapping("/plan")
    @Operation(summary = "Generate an optimized dispatch plan")
    public DispatchPlanResponse getPlan() { return dispatchService.createPlan(); }
}
