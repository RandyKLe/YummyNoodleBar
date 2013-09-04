package com.yummynoodlebar.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.yummynoodlebar.events.orders.OrderDetails;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class Basket  implements Serializable {

	private static final long serialVersionUID = -1779666204730031281L;
	
	private Map<String, MenuItem> items = new HashMap<String, MenuItem>();

	
	public Basket() {
		
	}

	public Basket(Map<String, MenuItem> items) {
		this.items = items;
	}

	
	public MenuItem add(MenuItem item) {
		items.put(item.getId(), item);
		return item;
	}

	
	public void delete(String key) {
		items.remove(key);
	}

	
	public MenuItem findById(String key) {
		for (MenuItem item : items.values()) {
			if (item.getId().equals(key)) {
				return item;
			}
		}
		return null;
	}

	
	public List<MenuItem> findAll() {
		return new ArrayList<MenuItem>(items.values());
	}
	
	public List<MenuItem> getItems() {
		return findAll();
	}
	
	public int getSize() {
		return items.size();
	}
	
	public void clear() {
		items = new HashMap<String, MenuItem>();
	}
	
	public OrderDetails createOrderDetailsWithCustomerInfo(CustomerInfo info) {
	    OrderDetails order = new OrderDetails();
	    BeanUtils.copyProperties(info, order);
	    order.setDateTimeOfSubmission(new Date());
	    copyItemsFromBasketToOrder(order);
		return order;
	}
	
	private void copyItemsFromBasketToOrder(OrderDetails orderDetails) {
		Map<String, Integer> items = new HashMap<String, Integer>();
		for (MenuItem item : getItems()) {
			//TODO need to get quantity from user input
			items.put(item.getId(), 1);
		}
		orderDetails.setOrderItems(items);
	}
}
