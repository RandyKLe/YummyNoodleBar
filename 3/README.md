Now that you have [written and tested a web controller](../2/), proudly added to your Life Preserver as shown below, it's time to bring the whole application together.

![Life Preserver Full showing Core Domain and Web Domain](../images/life-preserver-rest-domain-and-controllers-and-core-domain-zoom-out.png)

## Step 3: Configuring a basic application

At this point you are ready to:

* Configure the core of your application
* Configure your Web components
* Initialize your infrastructure to create a working WAR file.
* Run your Web application in a web container

To complete these tasks, you'll need a new domain, the Configuration domain.

![Life Preserver showing Configuration Domain](../images/life-preserver-empty-config-domain-focus.png)

## Create a configuration for your application's Core and Persistence domains using Spring JavaConfig

The Yummy Noodle Bar application contains a core set of components that include domain classes and services. It also contains an in memory persistence stored integrated with the core.

You could just create a configuration for these components; however, as in the previous step, you'll apply the Test Driven Development approach to your configuration.

### Test your Core and Persistence configurations

First, construct an integration test that contains the following:

`src/test/java/com/yummynoodlebar/config/CoreDomainIntegrationTest.java`
```java
package com.yummynoodlebar.config;

import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.events.menu.AllMenuItemsEvent;
import com.yummynoodlebar.events.menu.RequestAllMenuItemsEvent;
import com.yummynoodlebar.events.orders.AllOrdersEvent;
import com.yummynoodlebar.events.orders.CreateOrderEvent;
import com.yummynoodlebar.events.orders.OrderDetails;
import com.yummynoodlebar.events.orders.RequestAllOrdersEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, CoreConfig.class})
public class CoreDomainIntegrationTest {

	@Autowired
	MenuService menuService;

  @Autowired
  OrderService orderService;

	@Test
	public void thatAllMenuItemsReturned() {

	AllMenuItemsEvent allMenuItems = menuService.requestAllMenuItems(new RequestAllMenuItemsEvent());

	assertEquals(3, allMenuItems.getMenuItemDetails().size());

	}

  @Test
  public void addANewOrderToTheSystem() {

    CreateOrderEvent ev = new CreateOrderEvent(new OrderDetails());

    orderService.createOrder(ev);

    AllOrdersEvent allOrders = orderService.requestAllOrders(new RequestAllOrdersEvent());

    assertEquals(1, allOrders.getOrdersDetails().size());
  }

}
```

This integration test constructs an `ApplicationContext` using JavaConfig as specified on the `@ContextConfiguration` annotation. The Core domain's configuration will be created using Spring JavaConfig in a class called `CoreConfig`. The persistence domain's configuration will be created in a class call `PersistenceConfig`.

With the `ApplicationContext` constructed, the test can have its `MenuService` and `OrderService` test entry points autowired, ready for the test methods.

Finally you have two test methods that assert that the `menuService` and `orderService` dependencies has been provided and appear to work correctly.

Next, create the Core and Persistence domain configurations.

### Implement your Core domain configuration

The Core domain configuration for the Yummy Noodle Bar application only contains two services. It relies on the Persistence domain being configured to provide dependencies.

The following code shows the complete configuration class:

`src/main/java/com/yummynoodlebar/config/CoreConfig.java`
```java
package com.yummynoodlebar.config;

import com.yummynoodlebar.core.services.OrderEventHandler;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.persistence.services.OrderPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yummynoodlebar.core.services.MenuEventHandler;
import com.yummynoodlebar.core.services.MenuService;
import com.yummynoodlebar.persistence.services.MenuPersistenceService;

@Configuration
public class CoreConfig {
	@Bean
	public MenuService menuService(MenuPersistenceService menuPersistenceService) {
		return new MenuEventHandler(menuPersistenceService);
	}
  @Bean
  public OrderService orderService(OrderPersistenceService orderPersistenceService) {
    return new OrderEventHandler(orderPersistenceService);
  }

}
```

The core event handler will dispatch events to the persistence domain, which will perform the actual persistence.  This currently uses a set of HashMaps wrapped by in memory 'repositories'.

`src/main/java/com/yummynoodlebar/config/PersistenceConfig.java`
```java
package com.yummynoodlebar.config;

import com.yummynoodlebar.persistence.domain.MenuItem;
import com.yummynoodlebar.persistence.domain.Order;
import com.yummynoodlebar.persistence.repository.*;
import com.yummynoodlebar.persistence.services.MenuPersistenceEventHandler;
import com.yummynoodlebar.persistence.services.MenuPersistenceService;
import com.yummynoodlebar.persistence.services.OrderPersistenceEventHandler;
import com.yummynoodlebar.persistence.services.OrderPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class PersistenceConfig {

  @Bean
  public OrdersRepository ordersRepo() {
    return new OrdersMemoryRepository(new HashMap<UUID, Order>());
  }

  @Bean
  public OrderStatusRepository orderStatusRepo() {
    return new OrderStatusMemoryRepository();
  }

  @Bean
  public OrderPersistenceService orderPersistenceService() {
    return new OrderPersistenceEventHandler(ordersRepo(), orderStatusRepo());
  }

	@Bean
	public MenuItemRepository menuItemRepository() {
		return new MenuItemMemoryRepository(defaultMenu());
	}

	@Bean
	public MenuPersistenceService menuPersistenceService(MenuItemRepository menuItemRepository) {
		return new MenuPersistenceEventHandler(menuItemRepository);
	}

	private Map<String, MenuItem> defaultMenu() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		items.put("YM1", menuItem("YM1", new BigDecimal("1.99"), 11, "Yummy Noodles"));
		items.put("YM2", menuItem("YM2", new BigDecimal("2.99"), 12, "Special Yummy Noodles"));
		items.put("YM3", menuItem("YM3", new BigDecimal("3.99"), 13, "Low cal Yummy Noodles"));
		return items;
	}

	private MenuItem menuItem(String id, BigDecimal cost, int minutesToPrepare, String name) {
		MenuItem item = new MenuItem();
		item.setId(id);
		item.setCost(cost);
		item.setMinutesToPrepare(minutesToPrepare);
		item.setName(name);
		return item;
	}

}
```

Spring JavaConfig will detect each `@Bean` annotated method as a method that generates configured Spring Beans.

Running the `CoreDomainIntegrationTest` in the `com.yummynoodlebar.config` test package will verify that your Core Domain configuration is good to go.

## Create a configuration for your Web components

Configuring your new set of controllers is very straightforward as you have used `@Controller` on each of the controller classes. To initialize your Web domain's components, all you need to do is turn on component scanning so that Spring can find and initialize these Spring beans.

### Implement your Web domain configuration

You can create the following Spring JavaConfig to execute component scanning for the components in your application's RESTful domain:

`src/main/java/com/yummynoodlebar/config/WebConfig.java`
```java
package com.yummynoodlebar.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.yummynoodlebar.web.controller"})
public class WebConfig {

}
```

The `@ComponentScan` attribute in JavaConfig specifies that your components should be found underneath the base Java package of `com.yummynoodlebar.web.controllers`.

> **Note:** It's always a good idea to be as specific as possible when defining the place where component scanning should occur so that you don't accidentally initialize components you didn't expect!

### Test your Web domain configuration

No configuration should be trusted without an accompanying test. The following test asserts that the output of the Web configuration is as it should be:

`src/test/java/com/yummynoodlebar/config/WebDomainIntegrationTest.java`
```java
package com.yummynoodlebar.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PersistenceConfig.class, CoreConfig.class,
		WebConfig.class })
public class WebDomainIntegrationTest {

	private static final String STANDARD = "Yummy Noodles";
	private static final String CHEF_SPECIAL = "Special Yummy Noodles";
	private static final String LOW_CAL = "Low cal Yummy Noodles";

	private MockMvc mockMvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void thatTextReturned() throws Exception {
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(content().string(containsString(STANDARD)))
		.andExpect(content().string(containsString(CHEF_SPECIAL)))
		.andExpect(content().string(containsString(LOW_CAL)));

	}

}
```

You've already asserted the correctness of the collaboration between your controllers and the underlying service components in the Core Domain.

This test ensures that once everything is wired together, the wiring in the `WebConfig` is correct and the appropriate controllers are in attendance.

The test validates the `WebConfig` by mocking requests which exercise the handler mappings. The full responses are also confirmed to be correct. More testing could be done, but you've already asserted that your controllers should work appropriately in the previous steps. This test is simply there to show you that now you are configuring those components using Spring JavaConfig properly.

## Initialize your Web service web infrastructure

As of Spring 3.2, if you're using a web container that supports the Servlet 3 specification such as Tomcat 7+, it's possible to initialize the underlying web infrastructure for your application without writing a single line of XML.

Here you're going to use the `WebApplicationInitializer` to set up your application's web application context parameters to bootstrap your application's web infrastructure as shown in the following code.

First you create a new piece of configuration as a class inside `com.yummynoodlebar.config` called `WebAppInitializer` that extends the `AbstractAnnotationConfigDispatcherServletInitializer` from Spring as shown below.

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
public class WebAppInitializer extends
    AbstractAnnotationConfigDispatcherServletInitializer {
```

Next you override the `getRootConfigClasses` method which provides a set of Spring Configuration classes to construct the root application context.  This context will be shared by all elements of the application, including Servlets, Filters and Context Listeners, if present. It will containg the majority of your components, including your Core and Persistence domains.

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class<?>[] { PersistenceConfig.class, CoreConfig.class };
  }
```

Now with a root Application Context being initialised, override `getServletConfigClasses`.  This again returns a list of Spring Configuration classes, in this case, just the ones that are used as Servlet delegates.

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class<?>[] { WebConfig.class };
  }
```

Lastly, add some extra configuration to map the servlet URL context and add a standard filter.

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
  @Override
  protected String[] getServletMappings() {
    return new String[] { "/" };
  }

  @Override
  protected Filter[] getServletFilters() {

    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setEncoding("UTF-8");
    return new Filter[] { characterEncodingFilter};
  }
```

`AbstractAnnotationConfigDispatcherServletInitializer` performs setup of the Spring `DispatcherServlet` and `ContextLoader` that are a standard part of Spring web applications.

The `DispatcherServlet` is a 'front controller' servlet that receives all incoming requests that should be considered for the various controllers registered. The DispatcherServlet also orchestrates how each incoming request is channelled to the appropriate handler method on the available controllers.

The full `WebAppInitializer` source code is shown below:

`src/main/java/com/yummynoodlebar/config/WebAppInitializer.java`
```java
package com.yummynoodlebar.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

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
    return new Filter[] { characterEncodingFilter};
  }
}
```



## Running your Web service in a Web Container

It's the moment of truth: can you execute your Web application?

To find out, first tell Gradle that you will use Tomcat. Update your `build.gradle` file to look like this:

`build.gradle`
```gradle
apply plugin: 'war'
apply plugin: 'tomcat'
apply plugin: 'java'
apply plugin: 'propdeps'
apply plugin: 'propdeps-maven'
apply plugin: 'propdeps-idea'
apply plugin: 'propdeps-eclipse'
apply plugin: 'eclipse-wtp'

println "PROJECT=" + project.name

buildscript {
  repositories {
	mavenCentral()
	maven {
	  url "http://download.java.net/maven/2"
	}
	maven { url 'http://repo.springsource.org/plugins-release' }
  }

  dependencies {
	classpath 'org.gradle.api.plugins:gradle-tomcat-plugin:0.9.8'
	classpath 'org.springframework.build.gradle:propdeps-plugin:0.0.1'
  }
}


repositories { mavenCentral() }

dependencies {
	def tomcatVersion = '7.0.42'
	tomcat "org.apache.tomcat.embed:tomcat-embed-core:${tomcatVersion}",
			"org.apache.tomcat.embed:tomcat-embed-logging-juli:${tomcatVersion}"
	tomcat("org.apache.tomcat.embed:tomcat-embed-jasper:${tomcatVersion}") {
	  exclude group: 'org.eclipse.jdt.core.compiler', module: 'ecj'
	}

	compile 'org.springframework:spring-core:3.2.3.RELEASE'
	compile 'org.springframework:spring-webmvc:3.2.3.RELEASE'

	compile 'org.slf4j:slf4j-api:1.7.5'
	runtime 'org.slf4j:slf4j-log4j12:1.7.5'

	testCompile 'org.springframework:spring-test:3.2.3.RELEASE'

	testCompile 'junit:junit:4.11'
	testCompile "org.mockito:mockito-all:1.9.5"
	testCompile "org.hamcrest:hamcrest-library:1.3"

	provided 'javax.servlet:javax.servlet-api:3.0.1'
}

test {
  testLogging {
    // Show that tests are run in the command-line output
    events 'started', 'passed'
  }
}

task wrapper(type: Wrapper) { gradleVersion = '1.6' }

tomcatRunWar.contextPath = ''

```

You may notice at the bottom of the build file a setting to ensure the app runs at the root context:

```groovy
tomcatRunWar.contextPath = ''
```

Now you can run the following from the command line to execute the new service, on port 8080 by default:

```sh
$ ./gradlew tomcatRunWar
```

Then, if you visit [http://localhost:8080/](http://localhost:8080/), you should get the following plain text response, which is the initial menu populated in `PersistenceConfig`:

```
Yummy Noodles,Special Yummy Noodles,Low cal Yummy Noodles
```

If you need to set your web application to run on a different port or configure other settings, that information is available on the [Gradle Tomcat Plugin](https://github.com/bmuschko/gradle-tomcat-plugin/) project page.

If you plan to execute your service in another container and want to generate a WAR file instead, run the following command:

```sh
$ ./gradlew war
```

## Summary

You've come a long way! You've now got a fully configured Web front end that is running in Tomcat and can be packaged for distribution in a WAR file.

You've added three new components to your Configuration domain, `CoreConfig`, `PersistenceConfig` and `WebConfig` as shown in the updated life preserver below.

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-initial-config-domain-focus.png)

Your full Life Preserver should now look like the following:

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-rest-domain-and-controllers-and-core-domain-and-config-domain-initial-zoom-out.png)

Your web front end isn't very pretty yet, or even functional.  You will add both a pretty face and expand the number of URLs your application responds to, in the next section of this tutorial

[Next.. Creating rich HTML views using Thymeleaf](../4/)
