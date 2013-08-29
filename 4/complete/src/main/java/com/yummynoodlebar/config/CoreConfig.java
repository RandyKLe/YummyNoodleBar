package com.yummynoodlebar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yummynoodlebar.core.services.MenuEventHandler;
import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.persistence.services.MenuPersistenceService;

@Configuration
public class CoreConfig {
	@Bean
	public MenuService menuService(MenuPersistenceService menuPersistenceService) {
		return new MenuEventHandler(menuPersistenceService);
	}

}
