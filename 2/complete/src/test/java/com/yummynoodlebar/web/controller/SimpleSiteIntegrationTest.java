package com.yummynoodlebar.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

public class SimpleSiteIntegrationTest {

	MockMvc mockMvc;

	SimpleSiteController controller = new SimpleSiteController();

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(controller).build();
	}

	@Test
	public void thatTextReturned() throws Exception {
		mockMvc.perform(get("/simple"))
		.andDo(print())
		.andExpect(content().string("Yummy Noodles"));

	}

	@Test
	public void thatExpectedTextReturned() throws Exception {
		mockMvc.perform(get("/simple/directly"))
		.andDo(print())
		.andExpect(content().string("Yummy Noodle World"));

	}

}
