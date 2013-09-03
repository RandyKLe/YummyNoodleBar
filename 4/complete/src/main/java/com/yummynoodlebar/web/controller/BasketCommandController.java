package com.yummynoodlebar.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yummynoodlebar.web.domain.Basket;
import com.yummynoodlebar.web.domain.MenuItem;

@Controller
public class BasketCommandController {

	private static final Logger LOG = LoggerFactory.getLogger(BasketCommandController.class);
			
	@Autowired
	private Basket basket;
		
	@RequestMapping(value = "/removeFromBasket" , method = RequestMethod.POST)
	
	public String remove(@ModelAttribute("fred") MenuItem menuItem) {
		LOG.debug("Remove {} from the basket", menuItem.getId());
		basket.delete(menuItem.getId());
		return "redirect:/showBasket";
	}
	
	@RequestMapping(value = "/addToBasket" , method = RequestMethod.POST)
	
	public String add(@ModelAttribute("joe") MenuItem menuItem) {
		LOG.debug("Add {} from the basket", menuItem.getId());
		basket.add(menuItem);
		return "redirect:/";
	}
			
	
	
	@ModelAttribute("basket")
	private Basket getBasket() {
		return basket;
	}

}
