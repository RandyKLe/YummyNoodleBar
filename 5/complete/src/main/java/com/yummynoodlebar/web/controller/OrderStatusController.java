package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.web.domain.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/order/{orderId}")
public class OrderStatusController {

  private static final Logger LOG = LoggerFactory.getLogger(OrderStatusController.class);

  @Autowired
  private OrderService orderService;

  @RequestMapping(method= RequestMethod.GET)
  public String orderStatus() {
    return "/order";
  }

  @ModelAttribute("orderStatus")
  private OrderStatus getOrderStatus(@PathVariable("orderId") String orderId) {
    OrderStatus status = new OrderStatus();
    status.setOrderId(orderId);
    status.setStatus("BIG STATUS");
    return status;
  }
}
