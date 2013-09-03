package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.orders.CreateOrderEvent;
import com.yummynoodlebar.web.domain.Basket;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.UUID;

import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.newOrder;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CheckoutIntegrationTest {

	private static final String POST_CODE = "90210";

	private static final String ADDRESS1 = "Where they live";

	private static final String CUSTOMER_NAME = "Customer Name";

	private static final String CHECKOUT_VIEW = "/WEB-INF/views/checkout.jsp";

	MockMvc mockMvc;

	@InjectMocks
	CheckoutController controller;

	@Mock
	OrderService orderService;

	@Mock
	Basket basket;

  //{!begin init}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		mockMvc = standaloneSetup(controller).setViewResolvers(viewResolver())
				.build();
	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
  //{!end init}

	@Test
	public void thatBasketIsPopulated() throws Exception {
		mockMvc.perform(get("/checkout")).andExpect(
				model().attributeExists("basket"));
	}

	@Test
	public void thatCheckoutViewIsCorrect() throws Exception {
		mockMvc.perform(get("/checkout"))
				.andExpect(forwardedUrl(CHECKOUT_VIEW));
	}

	@Test
	public void thatRedirectsToOrderOnSuccess() throws Exception {
		UUID id = UUID.randomUUID();

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(
				newOrder(id));

		mockMvc.perform(
				post("/checkout").param("name", CUSTOMER_NAME)
								 .param("address1", ADDRESS1)
								 .param("postcode", POST_CODE))
						         .andExpect(status().isMovedTemporarily())
						         .andExpect(redirectedUrl("/order/" + id.toString()));

		verify(orderService).createOrder(any(CreateOrderEvent.class));
	}

	@Test
	public void thatSendsCorrectOrderEventOnSuccess() throws Exception {
		UUID id = UUID.randomUUID();

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(
				newOrder(id));

		mockMvc.perform(post("/checkout").param("name", CUSTOMER_NAME)
				.param("address1", ADDRESS1).param("postcode", POST_CODE));

//@formatter:off
    verify(orderService).createOrder(Matchers.<CreateOrderEvent>argThat(
        allOf(
            org.hamcrest.Matchers.<CreateOrderEvent>hasProperty("details",
        											hasProperty("dateTimeOfSubmission", notNullValue())),

            org.hamcrest.Matchers.<CreateOrderEvent>hasProperty("details",
            										hasProperty("name", equalTo(CUSTOMER_NAME))),

            org.hamcrest.Matchers.<CreateOrderEvent>hasProperty("details",
            										hasProperty("address1", equalTo(ADDRESS1))),
            org.hamcrest.Matchers.<CreateOrderEvent>hasProperty("details",
            										hasProperty("postcode", equalTo(POST_CODE)))
        )));
//@formatter:on    
	}

	@Test
	public void thatReturnsToCheckoutIfValidationFail() throws Exception {
		UUID id = UUID.randomUUID();

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(
				newOrder(id));

		mockMvc.perform(post("/checkout").param("postcode", POST_CODE))
				.andExpect(forwardedUrl(CHECKOUT_VIEW));
	}
}
