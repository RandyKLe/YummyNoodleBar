package com.yummynoodlebar.persistence.repository;

import com.yummynoodlebar.persistence.domain.Order;

import java.util.UUID;

public interface OrdersRepository {

  Order save(Order order);

  void delete(UUID key);

  Order findById(UUID key);

  Iterable<Order> findAll();
}
