package com.yummynoodlebar.events.menu;


import com.yummynoodlebar.events.ReadEvent;

import java.util.List;

public class AllMenuItemsEvent extends ReadEvent {
  private List<MenuItemDetails> menuItemDetails;

  public AllMenuItemsEvent(List<MenuItemDetails> menuItemDetails) {
    this.menuItemDetails = menuItemDetails;
  }

  public List<MenuItemDetails> getMenuItemDetails() {
    return menuItemDetails;
  }
}
