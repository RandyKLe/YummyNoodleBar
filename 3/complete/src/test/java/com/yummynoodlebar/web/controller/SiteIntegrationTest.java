package com.yummynoodlebar.web.controller;

import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.allMenuItems;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.events.menu.RequestAllMenuItemsEvent;

public class SiteIntegrationTest {
	
	private static final String RESPONSE_BODY = "Yummy Noodles,Special Yummy Noodles,Low cal Yummy Noodles";

	MockMvc mockMvc;
	
	@InjectMocks
	SiteController controller;
	
	@Mock
	MenuService menuService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		mockMvc = standaloneSetup(controller).build();
		
		when(menuService.requestAllMenuItems(any(RequestAllMenuItemsEvent.class))).thenReturn(allMenuItems());

	}
	
	@Test
	public void thatTextReturned() throws Exception {
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(content().string(RESPONSE_BODY));

	}

}
