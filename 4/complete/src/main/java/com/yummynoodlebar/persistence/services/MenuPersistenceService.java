package com.yummynoodlebar.persistence.services;

import com.yummynoodlebar.events.menu.*;

public interface MenuPersistenceService {

  AllMenuItemsEvent requestAllMenuItems(RequestAllMenuItemsEvent requestAllMenuItemsEvent);
  MenuItemDetailsEvent requestMenuItemDetails(RequestMenuItemDetailsEvent requestMenuItemDetailsEvent);
  MenuItemDetailsEvent createMenuItem(CreateMenuItemEvent createMenuItemEvent);

}
