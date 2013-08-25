package com.yummynoodlebar.core.services;

import com.yummynoodlebar.events.menu.*;

public interface MenuService {
  AllMenuItemsEvent requestAllMenuItems(RequestAllMenuItemsEvent requestAllMenuItemsEvent);
  MenuItemDetailsEvent requestMenuItemDetails(RequestMenuItemDetailsEvent requestMenuItemDetailsEvent);
  MenuItemDetailsEvent createMenuItem(CreateMenuItemEvent createMenuItemEvent);
}
