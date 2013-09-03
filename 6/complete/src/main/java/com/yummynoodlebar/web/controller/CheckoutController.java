package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.orders.CreateOrderEvent;
import com.yummynoodlebar.events.orders.OrderCreatedEvent;
import com.yummynoodlebar.events.orders.OrderDetails;
import com.yummynoodlebar.web.domain.Basket;
import com.yummynoodlebar.web.domain.CustomerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

  private static final Logger LOG = LoggerFactory.getLogger(BasketCommandController.class);

  @Autowired
  private Basket basket;

  @Autowired
  private OrderService orderService;

  @RequestMapping(method= RequestMethod.GET)
  public String checkout() {
    return "/checkout";
  }

  @PreAuthorize("hasRole('ROLE_ADMINWIBBLE')")
  @RequestMapping(method = RequestMethod.POST)
  public String doCheckout(@Valid @ModelAttribute("customerInfo") CustomerInfo customer, BindingResult result,
                           RedirectAttributes redirectAttrs) {
    if (result.hasErrors()) {
      //errors in the form
      //show the checkout form again
      return "/checkout";
    }

    OrderDetails order = new OrderDetails();
    order.setDateTimeOfSubmission(new Date());

    Map<String, Integer> items = new HashMap<String, Integer>();

    //TODO ... for (item : basket.getItems())
    //TODO, update with customer information.

    order.setOrderItems(items);

    OrderCreatedEvent event = orderService.createOrder(new CreateOrderEvent(order));

    UUID key = event.getNewOrderKey();

    redirectAttrs.addFlashAttribute("message", "Your order has been accepted!");

    //TODO, clear basket.. how?

    return "redirect:/order/" + key.toString();
  }

  @ModelAttribute("customerInfo")
  private CustomerInfo getCustomerInfo() {
    return new CustomerInfo();
  }

  @ModelAttribute("basket")
  private Basket getBasket() {
    return basket;
  }
}
