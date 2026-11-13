package com.example.dispatch.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record OrdersRequest(@NotEmpty List<@Valid OrderInput> orders) {}
