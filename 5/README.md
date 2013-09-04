Now that you have [added rich views and a basket](../4/), as on your life preserver below, its time to add the ability to place an order.

![Life Preserver Full showing Core Domain and Web Domain](../images/life-preserver-rest-domain-and-controllers-and-core-domain-zoom-out.png)


## Step 5: Accepting user submitted data

For Yummy Noodle Bar to accept the orders that a user is making, it needs to know where to send it.   Your users will need to give their name and address.

To do this, you must :-

* Add a checkout URL - "/checkout"
* Show an HTML form on GET
* Process the form information on POST
* Convert the Basket into an Order and send it to the core.

You will continue working within the Web domain, first created in step 2.

### Create the Checkout Controller

The Basket you created in the last section will contain all the items that a user wants to order.  When they want to place their Order, you need to also collect

To do this you will create a new Controller, and have that Controller accept a Command Object.  

A command Object is a bean that is used to model an HTTP request.  It does this by automatically mapping request parameters onto the properties of the bean.   These properties can then be tested using *Validation*.

Java has a standard *Validation* API, which you will need to include in the dependencies section of `build.gradle`.

`build.gradle`
```gradle
  compile 'javax.validation:validation-api:1.1.0.Final'
  compile 'org.hibernate:hibernate-validator:5.0.1.Final'
```

Since the Validation specification only defines an API, you need to include an implementation of that API as well.  Above, you have included Hibernate Validator.

#### Start with a test

As you should expect, you will first write a test to describe the features you want to implement.  Once those tests are ready, you can then safely implement the features themselves.

Add this test into your project.

`src/test/java/com/yummynoodlebar/web/controller/CheckoutIntegrationTest.java`
```java
package com.yummynoodlebar.web.controller;

import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.newOrder;
import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.standardWebMenuItem;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import com.yummynoodlebar.events.orders.CreateOrderEvent;
import com.yummynoodlebar.web.domain.Basket;

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

	@Before
	public void setup() {
		
		
		MockitoAnnotations.initMocks(this);
		
		controller.setBasket(new Basket());
		
		mockMvc = standaloneSetup(controller).setViewResolvers(viewResolver())
				.build();
	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

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

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(newOrder(id));

		mockMvc.perform(
				post("/checkout").param("name", CUSTOMER_NAME)
								 .param("address1", ADDRESS1)
								 .param("postcode", POST_CODE))
						         .andExpect(status().isMovedTemporarily())
						         .andExpect(redirectedUrl("/order/" + id.toString()));
	}

	@Test
	public void thatSendsCorrectOrderEventOnSuccess() throws Exception {
		UUID id = UUID.randomUUID();

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(newOrder(id));
		
		mockMvc.perform(post("/checkout")
				.param("name", CUSTOMER_NAME)
				.param("address1", ADDRESS1)
				.param("postcode", POST_CODE))
				.andDo(print());

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
	public void thatBasketIsEmptyOnSuccess() throws Exception {
		UUID id = UUID.randomUUID();

		when(orderService.createOrder(any(CreateOrderEvent.class))).thenReturn(newOrder(id));

		controller.getBasket().add(standardWebMenuItem());
		
		mockMvc.perform(
				post("/checkout").param("name", CUSTOMER_NAME)
								 .param("address1", ADDRESS1)
								 .param("postcode", POST_CODE));
		assertThat(controller.getBasket().getItems(), is(empty()));
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
```

TODO Describe setup of a test with a view resolver.

`src/test/java/com/yummynoodlebar/web/controller/CheckoutIntegrationTest.java`
```java
	@Before
	public void setup() {
		
		
		MockitoAnnotations.initMocks(this);
		
		controller.setBasket(new Basket());
		
		mockMvc = standaloneSetup(controller).setViewResolvers(viewResolver())
				.build();
	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
```

Following that are tests that check.

* the basket is correctly added to the model
* the view forward is correct
* the checkout controller will redirect to the url `/order` if the POST is complete and correct.
* the checkout controller will forward back to the url `/checkout` if the POST is incomplete.

This breaking up of tests follows the Clean Code principles laid out by Rob C. Martin.

The applicable principles here are that each test method is checking a single *concept* in the functionality of the URL.  The number of assertions involved in each test is irrelevant so long as they are all part of building confidence in a single function of the system.

#### Storing Customer Information, enter the Command Object

The first thing you need to do is introduce the new concepts you need into the system.  The first is the command object.

When you submit a POST request to /checkout, it will contain a set of POST variables in the request that will include information about a user.

You could parse these variables yourself and check their contents according to whatever rules you want to apply.   This happens so often, however, that Spring supplies a lot of functionality to ease your implementation.

The Command Object is a class that Spring will map the POST variables onto, parsing them into the given types on the class.  For example, if you have an `int` property on the Command Object, Spring will take the textual value supplied in the request and attempt to parse an `int` out of it.   This process of automatic parsing and conversion is known as *Binding*, and you can find out more in the [reference documentation](https://docs.springframework.io/spring/docs/3.2.4.RELEASE/spring-framework-reference/html/)

Command Objects are also the ideal place for validation.  

Create a new entity class, `CustomerInfo`, like so.

`src/main/java/com/yummynoodlebar/web/domain/CustomerInfo.java`
```java
package com.yummynoodlebar.web.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CustomerInfo implements Serializable {

  @NotNull
  @NotEmpty
  private String name;

  @NotNull
  @NotEmpty
  private String address1;

  @NotNull
  @NotEmpty
  private String postcode;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

}
```

This entity concept will only exist in the Web Domain.  LP

In it you have added validation annotations `@NotNull` and `@NotEmpty` from the standard validation API and a Hibernate Validator extension, respectively.

These do not do anything by themselves, they need to be interpreted by some Validator class.   This is done on request in a Spring MVC Controller, which you will implement in the next section.

#### Implement the controller

Now that the Command Object is ready to represent the incoming POST request, you can implement the Controller.

Create a new class `CheckoutController`, like so.

`src/main/java/com/yummynoodlebar/web/controller/CheckoutController.java`
```java
package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.orders.CreateOrderEvent;
import com.yummynoodlebar.events.orders.OrderCreatedEvent;
import com.yummynoodlebar.events.orders.OrderDetails;
import com.yummynoodlebar.web.domain.Basket;
import com.yummynoodlebar.web.domain.CustomerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

	private static final Logger LOG = LoggerFactory
			.getLogger(BasketCommandController.class);

	@Autowired
	private Basket basket;

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public String checkout() {
		return "/checkout";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String doCheckout(@Valid @ModelAttribute("customerInfo") CustomerInfo customer, BindingResult result, RedirectAttributes redirectAttrs) {
		if (result.hasErrors()) {
			// errors in the form
			// show the checkout form again
			return "/checkout";
		}

		LOG.debug("No errors, continue with processing for Customer {}:",
				customer.getName());

		OrderDetails order = basket
				.createOrderDetailsWithCustomerInfo(customer);

		OrderCreatedEvent event = orderService
				.createOrder(new CreateOrderEvent(order));

		UUID key = event.getNewOrderKey();

		redirectAttrs.addFlashAttribute("message",
				"Your order has been accepted!");

		basket.clear();
		LOG.debug("Basket now has {} items", basket.getSize());

		return "redirect:/order/" + key.toString();
	}

	@ModelAttribute("customerInfo")
	private CustomerInfo getCustomerInfo() {
		return new CustomerInfo();
	}

	@ModelAttribute("basket")
	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}
}
```

The controller provides two implementations for the same URL `/checkout`. 

If you access the URL with a HTTP GET, you will provided with the /checkout view (which will be resolved to the checkout.jsp).

If you access the URL with a HTTP POST, then the Controller expects that a form has been submitted.  To process this form, it uses the Command Object `customerInfo`, of type `CustomerInfo`.

You will notice the `@ModelAttribute` annotation on the `customerInfo` parameter, and a matching method below 

`src/main/java/com/yummynoodlebar/web/controller/CheckoutController.java`
```java
	@ModelAttribute("customerInfo")
	private CustomerInfo getCustomerInfo() {
		return new CustomerInfo();
	}
```

Together, these declare the CustomerInfo class to be a Command Object. When the page is rendered for the first time on a `GET /checkout`, the method `getCustomerInfo` is called to generate the 'customerInfo' property in the model.  You could pre-populate this is you wanted to in the `getCustomerInfo` method.   This property is then available in the model for the View to use during rendering, which you will see in the next section.

Using CustomerInfo as a parameter means that Spring will perform Binding of the request parameters against it.  If the binding did not complete successfully, then the result is stored in the `BindingResult` parameter.

In this case, the method immediately re-renders the checkout view.  The CustomerInfo instance, that did not bind correctly, will be available in the view to render, and you will see what support you have in the next section.

You will also notice a `@Valid` annotation on the `CustomerInfo`, this indicates to Spring that this instance should be *validated*.  This will use the annotations you added earlier to check the fields.  If the fields all pass the validation rules you have specified, then the bean is deemed to be valid, if not then it is invalid and the binding will fail.

#### Create the Checkout View

Now that the checkout URL is available and tested, its time to add the View to show the form to collect the customer information you need.

This view needs to populate the CustomerInfo bean

IMPORT/complete/src/webapp/WEB-INF/views/checkout.jsp

### Show the Order Status

Once the user has successfully checked out and placed their Order, you need to show them a screen that displays the Order they have placed and the current status of it.   This allows the kitchen to update the status of the Order and the user to see that new status.

#### Start with a test

This is a relatively straightforward addition to the code you've written before.  As with the CheckoutController, you first need to write a test, like so.

`src/test/java/com/yummynoodlebar/web/controller/OrderStatusIntegrationTest.java`
```java
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

	private static final String ORDER_VIEW = "/WEB-INF/views/order.jsp";
	
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
		viewResolver.setSuffix(".jsp");
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
```

#### Implement the controller

Now that you have a test, you need to implement the controller, which will look something like this

`src/main/java/com/yummynoodlebar/web/controller/OrderStatusController.java`
```java
package com.yummynoodlebar.web.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.orders.OrderDetailsEvent;
import com.yummynoodlebar.events.orders.OrderStatusEvent;
import com.yummynoodlebar.events.orders.RequestOrderDetailsEvent;
import com.yummynoodlebar.events.orders.RequestOrderStatusEvent;
import com.yummynoodlebar.web.domain.OrderStatus;

@Controller
@RequestMapping("/order/{orderId}")
public class OrderStatusController {

	private static final Logger LOG = LoggerFactory
			.getLogger(OrderStatusController.class);

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public String orderStatus(@ModelAttribute("orderStatus") OrderStatus orderStatus) {
		LOG.debug("Get order status for order id {} customer {}", orderStatus.getOrderId(), orderStatus.getName());
		return "/order";
	}

	@ModelAttribute("orderStatus")
	private OrderStatus getOrderStatus(@PathVariable("orderId") String orderId) {
		OrderDetailsEvent orderDetailsEvent = orderService.requestOrderDetails(new RequestOrderDetailsEvent(UUID.fromString(orderId)));
		OrderStatusEvent orderStatusEvent = orderService.requestOrderStatus(new RequestOrderStatusEvent(UUID.fromString(orderId)));
		OrderStatus status = new OrderStatus();
		status.setName(orderDetailsEvent.getOrderDetails().getName());
		status.setOrderId(orderId);
		status.setStatus(orderStatusEvent.getOrderStatus().getStatus());
		return status;
	}
}
```

#### Create the View

Lastly, create the view for the order and its status.

IMPORT/complete/src/webapp/WEB-INF/views/order.jsp

## Summary

You have successfully captured some user information in a form, mapped this onto a command object, validated it and combined it with the basket to create a fully functioning Order.

See the current state of your application below LP

Your application is a little too open with its information, however.  In the next section, you will learn how to apply security to your application and control who has access to which parts of your website, using Spring Security.

[Next.. Securing the Web Application](../6/)

