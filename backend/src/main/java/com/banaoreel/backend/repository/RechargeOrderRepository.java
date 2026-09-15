package com.banaoreel.backend.repository;

import com.banaoreel.backend.entity.RechargeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RechargeOrderRepository extends JpaRepository<RechargeOrder, UUID> {
    Optional<RechargeOrder> findByGatewayOrderId(String gatewayOrderId);
}
