package com.yummynoodlebar.core.domain.fixtures;

import com.yummynoodlebar.core.domain.Ingredient;
import com.yummynoodlebar.core.domain.MenuItem;
import com.yummynoodlebar.core.domain.Order;
import com.yummynoodlebar.events.orders.OrderDetails;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OrdersFixtures {

  public static final String YUMMY_ITEM = "yummy_core";

  public static Order standardOrder() {
    Order order = new Order(new Date());

    order.setExpectedCompletionTime(new Date(order.getDateTimeOfSubmission().getTime() + 300000));
    order.setMenuItems(standardMenu());
    order.setTotalCost(new BigDecimal("12.99"));

    return order;
  }

  public static List<MenuItem> standardMenu() {
    return Arrays.asList(yummyItem());
  }

  static MenuItem yummyItem() {
    MenuItem item = new MenuItem();

    item.setCost(new BigDecimal("12.99"));
    item.setDescription("Crispy Noodles with Sauce - Our signature dish");
    item.setId(YUMMY_ITEM);
    item.setMinutesToPrepare(10);
    item.setName("Yummy Noodles");
    item.setIngredients(Collections.singleton(new Ingredient("Noodles", "Egg Fried Noodles.")));

    return item;
  }

  /*
   * Twin of the above, to improve readability
   */
  public static OrderDetails standardOrderDetails() {
    return standardOrder().toOrderDetails();
  }

}
