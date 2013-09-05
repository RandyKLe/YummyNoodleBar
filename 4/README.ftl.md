Now that you have [configured and started your application](../2/), which appears in the new Configuration Domain on your life preserver, its time to make the application usable by adding a basket for users to add menu items, and also a view layer to show HTML.

![Life Preserver Full showing Core Domain, Configuration and Web Domains](TODO)

## Step 4: Creating rich HTML views using Thymeleaf

Your application is now ready to:

* Create a basket for the user to keep the items they want
* Add views to generate HTML
* Add view templates to keep common HTML

You will be working within the Web domain, first created in [step 2](../2/).

## Creating a basket

In the Yummy Noodle Bar website, you are going to add the ability for users to add the food items from the menu that they want to a 'basket'.  This will be a list of items, that the user can then choose to convert into an Order.  The order process is something you'll do on the next step; for now you need to create a basket.

Up to now, you have created Spring Components (like `@Controller`) that are shared between all users the system.  The default scope [link] of Spring Components is *Singleton*, so a single instance of the class is shared, everywhere it is used.    A basket can't be like this, instead we need an instance of the Basket *per user*.

While there are several different ways to achieve this, you will use the `Scope` feature of the Spring Application Context to create a *Session* scoped bean and inject it into your Controllers as normal.

### Start with a test

As with the other changes you have made, you must start with a test describing the change in behaviour you wish to make.

Update `SiteControllerIntegrationTest` to read

<@snippet path="src/test/java/com/yummynoodlebar/web/controller/SiteIntegrationTest.java" prefix="complete"/>

This test again uses MockMVC and ensures that the SiteController creates a Model as expected, and also that it *forwards* to the correct url.  A forward is a Servlet concept that allows a piece of code to delegate processing to another at a given URL.  In this case, the test ensures that the SiteController sets the forward URL to the name of a JSP to render for the user.

### Create the basket

Now that you have a test, you can start implementation.

Create a new `Basket` in the web domain to represent this new concept.

    <@snippet path="src/main/java/com/yummynoodlebar/web/domain/Basket.java" prefix="complete"/>

The section 

    <@snippet "src/main/java/com/yummynoodlebar/web/domain/Basket.java" "scope" "/complete"/>
    
Specifies that a new instance of the bean will be created for every user session (`HttpSession`), and that this will be managed by an automatically generated proxy.

The result of this is that you may inject the `Basket` as a dependency use `@Autowired` and can use normally, calls will be routed to the correct instance based on the current session by the automatically generated proxy.

Next, you need to update the SiteController to take advantage of the new `Basket`
Update `SiteController` to read 

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/SiteController.java" prefix="complete"/>

There are a few things going on here.
Firstly, the injection of the Basket dependency.  As above, only a single SiteController exists in the system, however multiple `Basket` instances exist and can all be accessed via this auto injected proxy.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "inject" "/complete"/>

The implementation of the request has been altered.  Instead of showing text on the users browser, a `Model` instance is obtained from Spring MVC and populated with all the current MenuItems.    The method then returns a string "/home".  This is a reference to a *view*, which you will create next.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "method" "/complete"/>

Lastly, you need to put the Basket into the model for the view to be able to read from.  
This method takes the auto injected Basket and annotates it so that it is automatically merged into the `Model`.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "model" "/complete"/>

Your `SiteController` now obtains the current Basket and pushes it into the model. Now you need to display that, with a *View*.

## Introducing views

A View is a component that generates HTML that can be sent to the users browser for them to interact with.

You need to create a new View for the SiteController to render.

Create a new file `home.jsp`

!!!IMPORT path="src/main/webapp/WEB-INF/views/home.jsp" prefix="complete"

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

    <@snippet path="src/test/java/com/yummynoodlebar/web/controller/BasketQueryIntegrationTest.java" prefix="complete"/>

    <@snippet path="src/test/java/com/yummynoodlebar/web/controller/BasketCommandIntegrationTest.java" prefix="complete"/>

These define three new URLs `/showBasket`, `/addToBasket?id=XX` and `/removeFromBasket?id=XX`

Next, you need to implement the controllers themselves.

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/BasketQueryController.java" prefix="complete"/>

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/BasketCommandController.java" prefix="complete"/>

TODO, discuss implementation a little. nothing special about these particularly. mention use of post variables maybe.

## Update the Configuration

You have made the Basket available, Controllers are correctly populating the Model, and you have written View JSPs; next, you need to set up Spring MVC to provide all the necessary configuration for these new components.

Update your `WebConfig` with the following

    <@snippet path="src/main/java/com/yummynoodlebar/config/WebConfig.java" prefix="complete"/>

This sets up components scanning for the domain package as well, to pick up the Basket, and creates a set of infrastructure that is needed to support JSP views.

## Extracting common views with SiteMesh.

Try running the application.

```
    ./gradlew tomcatRunWar
```

If you visit [http://localhost:8080/](http://localhost:8080) you will see the site home url, with the current menu rendered as rather spartan HTML.

It is not yet good looking, so you have a bit of work to do.  You are going to make a style that will be applied to all the pages and so you will need to share that common HTML and css between them.

SiteMesh (link) is a library that allows merging of HTML pages together in a very natural way.  It integrates very well with Spring MVC.

First, update `build.gradle` to include the dependency.

<@snippet 
"build.gradle" "sitemesh" "complete"/>

Next, you need to create the new common HTML.

TODO, link to style.jsp.  fpp is barfing on this.

SiteMesh requires a configuration file that describes which files to use as templates and for what URLs.

 <@snippet path="src/main/webapp/WEB-INF/decorators.xml" prefix="complete"/>

Lastly, SiteMesh operates as a Servlet Filter.  You must enable this in `WebAppInitializer`.

Update this to read

 <@snippet path="src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" prefix="complete"/>

Running the application again (you need to stop and start it to pick up the changes)

```sh
$ ./gradlew tomcatRunWar
```

And visiting [http://localhost:8080/](http://localhost:8080), you will see a much richer HTML page, including the site url, and the basket page.

## Summary

You have extended the application to show the menu in HTML and allow a user to select the items they are interested in and put them into a session backed Basket object, that is only present in the Web Domain.

See the current state of your application below LP

Next, you will extend the application to allow creating an Order from the Basket, which will require you to accept and validate customer information.

[Next.. Accepting user submitted data](../5/)
