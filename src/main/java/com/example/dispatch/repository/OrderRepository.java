package com.example.dispatch.repository;
import com.example.dispatch.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<DeliveryOrder, String> {}
