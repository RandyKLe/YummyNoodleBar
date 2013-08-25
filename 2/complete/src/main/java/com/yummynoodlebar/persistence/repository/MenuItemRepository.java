package com.yummynoodlebar.persistence.repository;

import com.yummynoodlebar.persistence.domain.MenuItem;

public interface MenuItemRepository {

  MenuItem save(MenuItem order);

  void delete(String key);

  MenuItem findById(String key);

  Iterable<MenuItem> findAll();
}
