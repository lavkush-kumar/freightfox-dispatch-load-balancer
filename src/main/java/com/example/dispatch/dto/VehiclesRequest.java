package com.example.dispatch.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record VehiclesRequest(@NotEmpty List<@Valid VehicleInput> vehicles) {}
