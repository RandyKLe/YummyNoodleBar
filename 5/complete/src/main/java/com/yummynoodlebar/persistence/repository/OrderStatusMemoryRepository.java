package com.yummynoodlebar.persistence.repository;

import com.yummynoodlebar.persistence.domain.OrderStatus;

import java.util.*;

public class OrderStatusMemoryRepository implements OrderStatusRepository {

  private Map<UUID, OrderStatus> orderStatuses = new HashMap<UUID, OrderStatus>();

  @Override
  public OrderStatus save(OrderStatus order) {
    orderStatuses.put(order.getId(), order);
    return order;
  }

  @Override
  public void delete(UUID key) {
    orderStatuses.remove(key);
  }

  @Override
  public OrderStatus findLatestById(UUID key) {
    for(OrderStatus item: orderStatuses.values()) {
      if (item.getOrderId().equals(key)) {
        return item;
      }
    }
    return null;
  }

  @Override
  public List<OrderStatus> findAll() {
    return new ArrayList<OrderStatus>(orderStatuses.values());
  }
}
