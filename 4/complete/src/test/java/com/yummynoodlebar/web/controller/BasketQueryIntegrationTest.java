package com.yummynoodlebar.web.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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

public class BasketQueryIntegrationTest {
	
	private static final String VIEW_NAME = "/showBasket";
	private static final String FORWARDED_URL = "/WEB-INF/views/showBasket.html";
	
	MockMvc mockMvc;
	
	@InjectMocks
	BasketQueryController controller;
		
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
	public void thatViewBasket() throws Exception {
		mockMvc.perform(get("/showBasket"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("basket"))													
		.andExpect(view().name(is(VIEW_NAME)))
		.andExpect(forwardedUrl(FORWARDED_URL));

	}

}
