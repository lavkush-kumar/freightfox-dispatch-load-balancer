package com.example.dispatch.repository;
import com.example.dispatch.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VehicleRepository extends JpaRepository<Vehicle, String> {}
