package com.yummynoodlebar.core.services;

import com.yummynoodlebar.events.menu.*;
import com.yummynoodlebar.persistence.services.MenuPersistenceService;

public class MenuEventHandler implements MenuService {

  private MenuPersistenceService menuPersistenceService;

  public MenuEventHandler(MenuPersistenceService menuPersistenceService) {
    this.menuPersistenceService = menuPersistenceService;
  }

  @Override
  public AllMenuItemsEvent requestAllMenuItems(RequestAllMenuItemsEvent requestAllMenuItemsEvent) {
    return menuPersistenceService.requestAllMenuItems(requestAllMenuItemsEvent);
  }

  @Override
  public MenuItemDetailsEvent requestMenuItemDetails(RequestMenuItemDetailsEvent requestMenuItemDetailsEvent) {
    return menuPersistenceService.requestMenuItemDetails(requestMenuItemDetailsEvent);
  }

  @Override
  public MenuItemDetailsEvent createMenuItem(CreateMenuItemEvent createMenuItemEvent) {
    return menuPersistenceService.createMenuItem(createMenuItemEvent);
  }
}
