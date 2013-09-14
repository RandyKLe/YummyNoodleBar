package com.yummynoodlebar.persistence.services;

import com.yummynoodlebar.events.orders.*;
import com.yummynoodlebar.persistence.domain.Order;
import com.yummynoodlebar.persistence.domain.OrderStatus;
import com.yummynoodlebar.persistence.repository.OrderStatusRepository;
import com.yummynoodlebar.persistence.repository.OrdersRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderPersistenceEventHandler implements OrderPersistenceService {

  private final OrdersRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;

  public OrderPersistenceEventHandler(
      final OrdersRepository orderRepository,
      final OrderStatusRepository orderStatusRepository) {
    this.orderRepository = orderRepository;
    this.orderStatusRepository = orderStatusRepository;
  }

  @Override
  public OrderStatusEvent setOrderStatus(SetOrderStatusEvent event) {

    OrderStatus status = OrderStatus.fromStatusDetails(event.getOrderStatus());

    status = orderStatusRepository.save(status);

    return new OrderStatusEvent(status.getId(), status.toStatusDetails());
  }

  @Override
  public OrderCreatedEvent createOrder(CreateOrderEvent createOrderEvent) {
    Order order = Order.fromOrderDetails(createOrderEvent.getDetails());

    order = orderRepository.save(order);

    return new OrderCreatedEvent(order.getKey(), order.toOrderDetails());
  }

  @Override
  public AllOrdersEvent requestAllOrders(RequestAllOrdersEvent requestAllCurrentOrdersEvent) {
    List<OrderDetails> generatedDetails = new ArrayList<OrderDetails>();
    for (Order order : orderRepository.findAll()) {
      generatedDetails.add(order.toOrderDetails());
    }
    return new AllOrdersEvent(generatedDetails);
  }

  @Override
  public OrderDetailsEvent requestOrderDetails(RequestOrderDetailsEvent requestOrderDetailsEvent) {

    Order order = orderRepository.findById(requestOrderDetailsEvent.getKey());

    if (order == null) {
      return OrderDetailsEvent.notFound(requestOrderDetailsEvent.getKey());
    }

    return new OrderDetailsEvent(
            requestOrderDetailsEvent.getKey(),
            order.toOrderDetails());
  }

  @Override
  public OrderUpdatedEvent setOrderPayment(SetOrderPaymentEvent setOrderPaymentEvent) {
    Order order = orderRepository.findById(setOrderPaymentEvent.getKey());

    if(order == null) {
      return OrderUpdatedEvent.notFound(setOrderPaymentEvent.getKey());
    }

    //TODO, handling payment details...

    return new OrderUpdatedEvent(order.getKey(), order.toOrderDetails());
  }

  @Override
  public OrderDeletedEvent deleteOrder(DeleteOrderEvent deleteOrderEvent) {

    Order order = orderRepository.findById(deleteOrderEvent.getKey());

    if (order == null) {
      return OrderDeletedEvent.notFound(deleteOrderEvent.getKey());
    }

    orderRepository.delete(deleteOrderEvent.getKey());

    return new OrderDeletedEvent(deleteOrderEvent.getKey(), order.toOrderDetails());
  }

  @Override
  public OrderStatusEvent requestOrderStatus(RequestOrderStatusEvent requestOrderDetailsEvent) {
    Order order = orderRepository.findById(requestOrderDetailsEvent.getKey());

    if (order == null) {
      return OrderStatusEvent.notFound(requestOrderDetailsEvent.getKey());
    }

    return new OrderStatusEvent(requestOrderDetailsEvent.getKey(), order.getStatus().toStatusDetails());
  }
}
