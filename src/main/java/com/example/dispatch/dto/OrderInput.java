package com.example.dispatch.dto;

import com.example.dispatch.entity.Priority;
import jakarta.validation.constraints.*;

public record OrderInput(@NotBlank @Size(max=64) String orderId,
 @DecimalMin(value="-90.0") @DecimalMax(value="90.0") double latitude,
 @DecimalMin(value="-180.0") @DecimalMax(value="180.0") double longitude,
 @NotBlank @Size(max=500) String address,
 @Positive double packageWeight, @NotNull Priority priority) {}
