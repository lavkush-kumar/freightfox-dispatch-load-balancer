package com.example.dispatch.dto;

import jakarta.validation.constraints.*;

public record VehicleInput(@NotBlank @Size(max=64) String vehicleId, @Positive double capacity,
 @DecimalMin(value="-90.0") @DecimalMax(value="90.0") double currentLatitude,
 @DecimalMin(value="-180.0") @DecimalMax(value="180.0") double currentLongitude,
 @NotBlank @Size(max=500) String currentAddress) {}
