package com.yummynoodlebar.web.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.yummynoodlebar.web.domain.Basket;
import com.yummynoodlebar.web.domain.MenuItem;

public class BasketCommandIntegrationTest {
		
	private static final String MENU_ID = "LOOK_FOR_ME_IN_THE_LOG";
	private static final String ADD_REDIRECTED_URL = "/";
	private static final String ADD_VIEW = "redirect:/";
	private static final String REMOVE_REDIRECTED_URL = "/showBasket";
	private static final String REMOVE_VIEW = "redirect:/showBasket";
	
	MockMvc mockMvc;
	
	@InjectMocks
	BasketCommandController controller;
			
	@Mock
	Basket basket;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
						
		mockMvc = standaloneSetup(controller)
				.setViewResolvers(viewResolver())
				.build();
	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}
	
	
	@Test
	public void thatAddToBasketRedirects() throws Exception {
		mockMvc.perform(post("/addToBasket"))
		.andDo(print())
		.andExpect(status().isMovedTemporarily())
		.andExpect(view().name(ADD_VIEW))
		.andExpect(redirectedUrl(ADD_REDIRECTED_URL));
	}
	
	@Test
	public void thatAddToBasketCollaborates() throws Exception {
				
		mockMvc.perform(post("/addToBasket"))
		.andDo(print());
		
		verify(basket).add(any(MenuItem.class));
	}
	
	@Test
	public void thatRemoveFromBasketRedirects() throws Exception {
		mockMvc.perform(post("/removeFromBasket"))
		.andDo(print())
		.andExpect(status().isMovedTemporarily())
		.andExpect(view().name(REMOVE_VIEW))
		.andExpect(redirectedUrl(REMOVE_REDIRECTED_URL));
	}
	@Test
	public void thatRemoveFromBasketCollaborates() throws Exception {
				
		mockMvc.perform(post("/removeFromBasket/").param("id", MENU_ID))
		.andDo(print());
		
		verify(basket).delete(MENU_ID);
	}
	

}
