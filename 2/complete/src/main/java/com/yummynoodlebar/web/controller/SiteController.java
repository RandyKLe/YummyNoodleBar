package com.yummynoodlebar.rest.controller;

import com.yummynoodlebar.core.events.orders.OrderStatusEvent;
import com.yummynoodlebar.core.events.orders.RequestOrderStatusEvent;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.rest.domain.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class SiteController {

  @RequestMapping(method = RequestMethod.GET)
  public Object getOrderStatus() {

    return null;
  }
}
