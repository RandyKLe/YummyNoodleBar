package com.yummynoodlebar.persistence.repository;

import com.yummynoodlebar.persistence.domain.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderStatusRepository {

  OrderStatus save(OrderStatus orderStatus);

  void delete(UUID key);

  OrderStatus findLatestById(UUID key);

  List<OrderStatus> findAll();
}
