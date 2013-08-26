package com.yummynoodlebar.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

public class SiteSimpleIntegrationTest {

	MockMvc mockMvc;

	SiteController controller = new SiteController();

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(controller).build();
	}

	@Test
	public void thatTextReturned() throws Exception {
		mockMvc.perform(get("/")).andExpect(content().string("Hello World"));

	}

	@Test
	public void thatHWReturned() throws Exception {
		mockMvc.perform(get("/hw")).andExpect(
				content().string("Hello World baby"));

	}

}
