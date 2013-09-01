package com.yummynoodlebar.config;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.events.menu.AllMenuItemsEvent;
import com.yummynoodlebar.events.menu.RequestAllMenuItemsEvent;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, CoreConfig.class})
public class CoreDomainIntegrationTest {
	
	@Autowired
	MenuService menuService;
			
	@Test
	public void thatAllMenuItemsReturned() {
		
	AllMenuItemsEvent allMenuItems = menuService.requestAllMenuItems(new RequestAllMenuItemsEvent());
	
	assertEquals(3, allMenuItems.getMenuItemDetails().size());
			
	}	

}
