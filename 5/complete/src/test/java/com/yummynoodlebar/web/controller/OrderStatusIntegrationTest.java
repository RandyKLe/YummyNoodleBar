package com.yummynoodlebar.web.controller;

import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.orderDetailsEvent;
import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.orderStatusEvent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.orders.RequestOrderDetailsEvent;
import com.yummynoodlebar.events.orders.RequestOrderStatusEvent;
import com.yummynoodlebar.web.controller.fixture.WebDataFixture;

public class OrderStatusIntegrationTest {

	private static final String ORDER_VIEW = "/WEB-INF/views/order.html";
	
	private static UUID uuid;

	MockMvc mockMvc;

	@InjectMocks
	OrderStatusController controller;

	@Mock
	OrderService orderService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		mockMvc = standaloneSetup(controller).setViewResolvers(viewResolver())
				.build();
		uuid = UUID.randomUUID();
	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}

	@Test
	public void thatOrderViewIsForwardedTo() throws Exception {
		
		when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class))).thenReturn(orderDetailsEvent(uuid));
		when(orderService.requestOrderStatus(any(RequestOrderStatusEvent.class))).thenReturn(orderStatusEvent(uuid));
		
		mockMvc.perform(get("/order/" + uuid))
		.andExpect(status().isOk())
		.andExpect(forwardedUrl(ORDER_VIEW));
	}
	
	@Test
	public void thatOrderStatusIsPutInModel() throws Exception {
		
		when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class))).thenReturn(orderDetailsEvent(uuid));
		when(orderService.requestOrderStatus(any(RequestOrderStatusEvent.class))).thenReturn(orderStatusEvent(uuid));
		
		mockMvc.perform(get("/order/" + uuid))
			.andExpect(model().attributeExists("orderStatus"))
			.andExpect(model().attribute("orderStatus", hasProperty("name", equalTo(WebDataFixture.CUSTOMER_NAME))))
			.andExpect(model().attribute("orderStatus", hasProperty("status", equalTo(WebDataFixture.STATUS_RECEIVED))));
		
		verify(orderService).requestOrderDetails(Matchers.<RequestOrderDetailsEvent>argThat(
				org.hamcrest.Matchers.<RequestOrderDetailsEvent>hasProperty("key", equalTo(uuid))));
		verify(orderService).requestOrderStatus(any(RequestOrderStatusEvent.class));
	}
		
}
