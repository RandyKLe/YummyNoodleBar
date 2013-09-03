Now that you have [configured and started your application](../2/), which appears in the new Configuration Domain on your life preserver, its time to make the application usable by adding a basket for users to add menu items, and also a view layer to show HTML.

![Life Preserver Full showing Core Domain, Configuration and Web Domains](TODO)

## Step 4: Creating rich HTML views using JSP and Spring Tags

Your application is now ready to :-

* Create a basket for the user to keep the items they want in
* Add views to generate HTML.
* Add view templates to keep common HTML in.

You will be working within the Web domain, first created in step 2.

## Creating a basket

In the Yummy Noodle Bar website, you are going to add the ability for users to add the food items from the menu that they want to a 'basket'.  This will be a list of items, that the user can then choose to convert into an Order.  The order process is something you'll do on the next step; for now you need to create a basket.

Up to now, you have created Spring Components (like `@Controller`) that are shared between all users the system.  The default scope [link] of Spring Components is *Singleton*, so a single instance of the class is shared, everywhere it is used.    A basket can't be like this, instead we need an instance of the Basket *per user*.

While there are several different ways to achieve this, you will use the `Scope` feature of the Spring Application Context to create a *Session* scoped bean and inject it into your Controllers as normal.

### Start with a test

As with the other changes you have made, you must start with a test describing the change in behaviour you wish to make.

Update `SiteControllerIntegrationTest` to read

`src/test/java/com/yummynoodlebar/web/controller/SiteIntegrationTest.java`
```java
package com.yummynoodlebar.web.controller;

import static com.yummynoodlebar.web.controller.fixture.WebDataFixture.allMenuItems;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
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

import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.events.menu.RequestAllMenuItemsEvent;
import com.yummynoodlebar.web.domain.Basket;

public class SiteIntegrationTest {
	
	private static final String STANDARD = "Yummy Noodles";
	private static final String CHEF_SPECIAL = "Special Yummy Noodles";
	private static final String LOW_CAL = "Low cal Yummy Noodles";
	private static final String FORWARDED_URL = "/WEB-INF/views/home.jsp";
	private static final String VIEW = "/home";
	
	
	MockMvc mockMvc;
	
	@InjectMocks
	SiteController controller;
	
	@Mock
	MenuService menuService;
	
	@Mock
	Basket basket;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
						
		mockMvc = standaloneSetup(controller)
				.setViewResolvers(viewResolver())
				.build();
		
		when(menuService.requestAllMenuItems(any(RequestAllMenuItemsEvent.class))).thenReturn(allMenuItems());

	}

	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rootUrlPopulatesViewModel() throws Exception {
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(model().size(2))
		.andExpect(model().attribute("menuItems", hasSize(3)))
		.andExpect(model().attribute("menuItems", hasItems(hasProperty("name", is(STANDARD)),
															hasProperty("name", is(CHEF_SPECIAL)),
															hasProperty("name", is(LOW_CAL))) ))
		
		.andExpect(model().attributeExists("basket"));													
	}
	
	@Test
	public void rootUrlforwardsCorrectly() throws Exception {
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(view().name(VIEW))
		.andExpect(forwardedUrl(FORWARDED_URL));

	}

}
```

This test again uses MockMVC and ensures that the SiteController creates a Model as expected, and also that it *forwards* to the correct url.  A forward is a Servlet concept that allows a piece of code to delegate processing to another at a given URL.  In this case, the test ensures that the SiteController sets the forward URL to the name of a JSP to render for the user.

### Create the basket

Now that you have a test, you can start implementation.

Create a new `Basket` in the web domain to represent this new concept.

`src/main/java/com/yummynoodlebar/web/domain/Basket.java`
```java
package com.yummynoodlebar.web.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class Basket  {

	private Map<String, MenuItem> items = new HashMap<String, MenuItem>();

	
	public Basket() {
		
	}

	public Basket(Map<String, MenuItem> items) {
		this.items = items;
	}

	
	public MenuItem add(MenuItem item) {
		items.put(item.getId(), item);
		return item;
	}

	
	public void delete(String key) {
		items.remove(key);
	}

	
	public MenuItem findById(String key) {
		for (MenuItem item : items.values()) {
			if (item.getId().equals(key)) {
				return item;
			}
		}
		return null;
	}

	
	public List<MenuItem> findAll() {
		return new ArrayList<MenuItem>(items.values());
	}
	
	public List<MenuItem> getItems() {
		return findAll();
	}
	
	public int getSize() {
		return items.size();
	}
}
```

The section 

`src/main/java/com/yummynoodlebar/web/domain/Basket.java`
```java
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
```
    
Specifies that a new instance of the bean will be created for every user session (`HttpSession`), and that this will be managed by an automatically generated proxy.

The result of this is that you may inject the `Basket` as a dependency use `@Autowired` and can use normally, calls will be routed to the correct instance based on the current session by the automatically generated proxy.

Next, you need to update the SiteController to take advantage of the new `Basket`
Update `SiteController` to read 

`src/main/java/com/yummynoodlebar/web/controller/SiteController.java`
```java
package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.events.menu.AllMenuItemsEvent;
import com.yummynoodlebar.events.menu.MenuItemDetails;
import com.yummynoodlebar.events.menu.RequestAllMenuItemsEvent;
import com.yummynoodlebar.web.domain.Basket;
import com.yummynoodlebar.web.domain.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class SiteController {

	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
	
	@Autowired
	private MenuService menuService;

	@Autowired
	private Basket basket;

	@RequestMapping(method = RequestMethod.GET)
	public String getCurrentMenu(Model model) {
		LOG.debug("Yummy MenuItemDetails to home view");
		model.addAttribute("menuItems",getMenuItems(menuService.requestAllMenuItems(new RequestAllMenuItemsEvent())));
		return "/home";
	}
			
	private List<MenuItem> getMenuItems(AllMenuItemsEvent requestAllMenuItems) {
		List<MenuItem> menuDetails = new ArrayList<MenuItem>();
		
		for (MenuItemDetails menuItemDetails : requestAllMenuItems.getMenuItemDetails()) {
			menuDetails.add(MenuItem.fromMenuDetails(menuItemDetails));
		}

		return menuDetails;
	}

	@ModelAttribute("basket")
	private Basket getBasket() {
		return basket;
	}
}
```

There are a few things going on here.
Firstly, the injection of the Basket dependency.  As above, only a single SiteController exists in the system, however multiple `Basket` instances exist and can all be accessed via this auto injected proxy.

`src/main/java/com/yummynoodlebar/web/controller/SiteController.java`
```java
	@Autowired
	private Basket basket;
```

The implementation of the request has been altered.  Instead of showing text on the users browser, a `Model` instance is obtained from Spring MVC and populated with all the current MenuItems.    The method then returns a string "/home".  This is a reference to a *view*, which you will create next.

`src/main/java/com/yummynoodlebar/web/controller/SiteController.java`
```java
	@RequestMapping(method = RequestMethod.GET)
	public String getCurrentMenu(Model model) {
		LOG.debug("Yummy MenuItemDetails to home view");
		model.addAttribute("menuItems",getMenuItems(menuService.requestAllMenuItems(new RequestAllMenuItemsEvent())));
		return "/home";
	}
```

Lastly, you need to put the Basket into the model for the view to be able to read from.  
This method takes the auto injected Basket and annotates it so that it is automatically merged into the `Model`.

`src/main/java/com/yummynoodlebar/web/controller/SiteController.java`
```java
	@ModelAttribute("basket")
	private Basket getBasket() {
		return basket;
	}
```

Your `SiteController` now obtains the current Basket and pushes it into the model. Now you need to display that, with a *View*.

## Introducing views

A View is a component that generates HTML that can be sent to the users browser for them to interact with.

You need to create a new View for the SiteController to render.

Create a new file `home.jsp`

TODO, need to embed a view, but fpp barfs on both importing via snippet and directly embedding... :-(
```html

```

This JSP reads the model provided by the Controller, namely the `basket` and `menuItems` properties.

TODO, need to embed a view, but fpp barfs ... :-(
```html

```

This tag creates a URL based on the current Spring MVC setup, application root and base URL.   This allows you to customise the environment and have all your URLs automatically update.


## Updating the basket

Viewing a menu in HTML and seeing the state of the basket is good, however for it to be useful, you need to add the ability to add and remove items from the basket.  For good measure, you will also add support to view the current basket.

//TODO, talk about cqrs.

Create two empty classes `com.yummynoodlebar.web.controller.BasketQueryController` and `com.yummynoodlebar.web.controller.BasketCommandController`.

Create a test for each.

`src/test/java/com/yummynoodlebar/web/controller/BasketQueryIntegrationTest.java`
```java
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
	private static final String FORWARDED_URL = "/WEB-INF/views/showBasket.jsp";
	
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
		viewResolver.setSuffix(".jsp");
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
```

`src/test/java/com/yummynoodlebar/web/controller/BasketCommandIntegrationTest.java`
```java
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
		viewResolver.setSuffix(".jsp");
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
```

These define three new URLs `/showBasket`, `/addToBasket?id=XX` and `/removeFromBasket?id=XX`

Next, you need to implement the controllers themselves.

`src/main/java/com/yummynoodlebar/web/controller/BasketQueryController.java`
```java
package com.yummynoodlebar.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yummynoodlebar.web.domain.Basket;

@Controller
public class BasketQueryController {

	private static final Logger LOG = LoggerFactory.getLogger(BasketQueryController.class);
			
	@Autowired
	private Basket basket;
		
	@RequestMapping(value = "/showBasket" , method = RequestMethod.GET)
	
	public String show(Model model) {
		LOG.debug("Show the basket contents");
		return "/showBasket";
	}
			
	
	
	@ModelAttribute("basket")
	private Basket getBasket() {
		return basket;
	}

}
```

`src/main/java/com/yummynoodlebar/web/controller/BasketCommandController.java`
```java
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
```

TODO, discuss implementation a little. nothing special about these particularly. mention use of post variables maybe.

## Update the Configuration

You have made the Basket available, Controllers are correctly populating the Model, and you have written View JSPs; next, you need to set up Spring MVC to provide all the necessary configuration for these new components.

Update your `WebConfig` with the following

`src/main/java/com/yummynoodlebar/config/WebConfig.java`
```java
package com.yummynoodlebar.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.yummynoodlebar.web.controller","com.yummynoodlebar.web.domain"})
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		registry.addInterceptor(localeChangeInterceptor);
	}
	
	@Bean
	public LocaleResolver localeResolver() {

		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale(StringUtils.parseLocaleString("en"));
		return cookieLocaleResolver;
	}
	
	@Bean
	public ViewResolver viewResolver() {

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@Bean
	public MessageSource messageSource() {

		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages/messages", "classpath:messages/validation");
		// if true, the key of the message will be displayed if the key is not
		// found, instead of throwing a NoSuchMessageException
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setDefaultEncoding("UTF-8");
		// # -1 : never reload, 0 always reload
		messageSource.setCacheSeconds(0);
		return messageSource;
	}

}
```

This sets up components scanning for the domain package as well, to pick up the Basket, and creates a set of infrastructure that is needed to support JSP views.

## Extracting common views with SiteMesh.

Try running the application.

```
    ./gradlew tomcatRunWar
```

If you visit (http://localhost:8080/)[http://localhost:8080] you will see the site home url, with the current menu rendered as rather spartan HTML.

It is not yet good looking, so you have a bit of work to do.  You are going to make a style that will be applied to all the pages and so you will need to share that common HTML and css between them.

SiteMesh (link) is a library that allows merging of HTML pages together in a very natural way.  It integrates very well with Spring MVC.

First, update `build.gradle` to include the dependency.

`build.gradle`
```gradle
	compile 'opensymphony:sitemesh:2.4.2'
```

Next, you need to create the new common HTML.

TODO, link to style.jsp.  fpp is barfing on this.

SiteMesh requires a configuration file that describes which files to use as templates and for what URLs.

`src/main/webapp/WEB-INF/decorators.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<decorators defaultdir="/WEB-INF/decorators/">

	<excludes>
        <pattern>/rss/*</pattern>
    </excludes>

    <decorator name="default" page="twitterBoostrapLayout.jsp">
        <pattern>*</pattern>
    </decorator>

</decorators>
```

Lastly, SiteMesh operates as a Servlet Filter.  You must enable this in `WebAppInitializer`.

Update this to read

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
package com.yummynoodlebar.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

public class WebAppInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { PersistenceConfig.class, CoreConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	@Override
	protected Filter[] getServletFilters() {
		
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		return new Filter[] { characterEncodingFilter, new SiteMeshFilter()};
	}

}
```

Running the application again (you need to stop and start it to pick up the changes)

```
    ./gradlew tomcatRunWar
```

And visiting (http://localhost:8080/)[http://localhost:8080], you will see a much richer HTML page, including the site url, and the basket page.

## Summary

You have extended the application to show the menu in HTML and allow a user to select the items they are interested in and put them into a session backed Basket object, that is only present in the Web Domain.

See the current state of your application below LP

Next, you will extend the application to allow creating an Order from the Basket, which will require you to accept and validate customer information.

[Next.. Accepting user submitted data](../5/)
