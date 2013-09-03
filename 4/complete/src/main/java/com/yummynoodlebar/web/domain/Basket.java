package com.yummynoodlebar.web.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
// {!begin scope}
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
// {!end scope}
public class Basket  {

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
}
