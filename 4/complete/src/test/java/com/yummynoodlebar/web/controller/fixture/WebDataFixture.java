package com.yummynoodlebar.web.controller.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yummynoodlebar.events.menu.AllMenuItemsEvent;
import com.yummynoodlebar.events.menu.MenuItemDetails;
import com.yummynoodlebar.web.domain.MenuItem;

public class WebDataFixture {
	
	private static final String NAME = "Yummy Noodles";
	private static final String CHEF_SPECIAL = "Special ";
	private static final String LOW_CAL = "Low cal ";
	private static final BigDecimal COST = new BigDecimal("10.99");
	private static final int MINUTES_TO_PREPARE = 5;

	public static AllMenuItemsEvent allMenuItems() {
		List<MenuItemDetails> menuItemDetails = new ArrayList<MenuItemDetails>();
		menuItemDetails.add(standardMenuItemDetails());
		menuItemDetails.add(standardMenuItemDetails(CHEF_SPECIAL + NAME));
		menuItemDetails.add(standardMenuItemDetails(LOW_CAL + NAME));
		return new AllMenuItemsEvent(menuItemDetails);
	}

	public static MenuItemDetails standardMenuItemDetails(String name) {
		return new MenuItemDetails(UUID.randomUUID().toString(), name, COST, MINUTES_TO_PREPARE);
	}
	
	public static MenuItemDetails standardMenuItemDetails() {
		return standardMenuItemDetails(NAME);
	}
	
	public static MenuItem standardWebMenuItem () {
		return MenuItem.fromMenuDetails(standardMenuItemDetails());
	}

}
