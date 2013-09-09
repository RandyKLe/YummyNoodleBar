Now that you have [configured and started your application](../2/), which appears in the new Configuration Domain on your life preserver, its time to make the application usable by adding a basket for users to add menu items, and also a view layer to show HTML.

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-7.png)

## Step 4: Creating rich HTML views using Thymeleaf

Your application is now ready to:

* Create a basket for the user to keep the items they want in
* Add views to generate HTML.
* Add view fragments to keep common HTML in.

You will be working within the Web domain, first created in step 2.

## Creating a basket

In the Yummy Noodle Bar website, you are going to add the ability for users to add the food items from the menu that they want to a 'basket'.  This will be a list of items, that the user can then choose to convert into an Order.  The order process is something you'll do on the next step; for now you need to create a basket.

Up to now, you have created Spring Components (like `@Controller`) that are shared between all users the system.  The default scope [link] of Spring Components is *Singleton*, so a single instance of the class is shared, everywhere it is used.    A basket can't be like this, instead we need an instance of the Basket *per user*.

While there are several different ways to achieve this, you will use the `Scope` feature of the Spring Application Context to create a *Session* scoped bean and inject it into your Controllers as normal.

### Start with a test

As with the other changes you have made, you must start with a test describing the change in behaviour you wish to make.

Update `SiteControllerIntegrationTest` to read

<@snippet path="src/test/java/com/yummynoodlebar/web/controller/SiteIntegrationTest.java" prefix="complete"/>

This test again uses MockMVC and ensures that the SiteController creates a Model as expected, and also that it *forwards* to the correct url.  A forward is a Servlet concept that allows a piece of code to delegate processing to another at a given URL.  In this case, the test ensures that the SiteController sets the forward URL to the name of a Thymeleaf template to render for the user.

### Create the basket

Now that you have a test, you can start implementation.

Create a new `Basket` in the web domain to represent this new concept.

    <@snippet path="src/main/java/com/yummynoodlebar/web/domain/Basket.java" prefix="complete"/>

The section 

    <@snippet "src/main/java/com/yummynoodlebar/web/domain/Basket.java" "scope" "/complete"/>
    
Specifies that a new instance of the bean will be created for every user session (`HttpSession`), and that this will be managed by an automatically generated proxy.

The result of this is that you may inject the `Basket` as a dependency using `@Autowired` and can use normally. Calls will be routed to the correct instance based on the current session by the automatically generated proxy.

Next, you need to update the SiteController to take advantage of the new `Basket`
Update `SiteController` to read 

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/SiteController.java" prefix="complete"/>

There are a few things going on here.
Firstly, the injection of the Basket dependency.  As above, only a single SiteController exists in the system, however multiple `Basket` instances exist and can all be accessed via this auto injected proxy.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "inject" "/complete"/>

The implementation of the request has been altered.  Instead of showing text in the user's browser, a `Model` instance is obtained from Spring MVC and populated with all the current MenuItems.    The method then returns a string "/home".  This is a reference to a *view*, which you will create next.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "method" "/complete"/>

Lastly, you need to put the Basket into the model for the view to be able to read from.  
This method takes the auto injected Basket and annotates it so that it is automatically merged into the `Model`.

    <@snippet "src/main/java/com/yummynoodlebar/web/controller/SiteController.java" "model" "/complete"/>

Your `SiteController` now obtains the current Basket and pushes it into the model. Now you need to display that, with a *View*.

## Introducing views

A View is a component that generates HTML that can be sent to the users browser for them to interact with.

You need to create a new View for the SiteController to render.

You will be using the Thymeleaf templating engine. This is a rich and powerful templating engine that provides all of its functionality as attributes on standard HTML.

Before you can use it, you need to add it to your `build.gradle`

    <@snippet "build.gradle" "thymeleaf" "complete"/>

Now that Thymeleaf is available, create a new file `home.html`

!!!IMPORT path="src/main/webapp/WEB-INF/views/home.html" prefix="complete"

This will end up looking similar to

![Home Page](../images/page_site.png)

This Thymeleaf template reads the model provided by the Controller, namely the `basket` and `menuItems` properties.

Create a view for viewing the current basket too. This will look like :

!!!IMPORT path="src/main/webapp/WEB-INF/views/showBasket.html" prefix="complete"

> These files can be opened in your browser without starting Tomcat.  Thymeleaf refers to these as prototypes and has great support for allowing you to create realistic looking pages for development while they are just loaded off the disk, no server required.

## Updating the basket

Viewing a menu in HTML and seeing the state of the basket is good, however for it to be useful, you need to add the ability to add and remove items from the basket.  For good measure, you will also add support to view the current basket.

Create two empty classes `com.yummynoodlebar.web.controller.BasketQueryController` and `com.yummynoodlebar.web.controller.BasketCommandController`.

Create a test for each.

    <@snippet path="src/test/java/com/yummynoodlebar/web/controller/BasketQueryIntegrationTest.java" prefix="complete"/>

    <@snippet path="src/test/java/com/yummynoodlebar/web/controller/BasketCommandIntegrationTest.java" prefix="complete"/>

These define three new URLs `/showBasket`, `/addToBasket?id=XX` and `/removeFromBasket?id=XX`

Next, you need to implement the controllers themselves.

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/BasketQueryController.java" prefix="complete"/>

    <@snippet path="src/main/java/com/yummynoodlebar/web/controller/BasketCommandController.java" prefix="complete"/>

## Update the Configuration

You have made the Basket available, Controllers are correctly populating the Model, and you have written View Thymeleaf templates; next, you need to set up Spring MVC to provide all the necessary configuration for these new components.

Update your `WebConfig` with the following

    <@snippet path="src/main/java/com/yummynoodlebar/config/WebConfig.java" prefix="complete"/>

This sets up components scanning for the domain package as well, to pick up the Basket, and creates a set of infrastructure that is needed to support Thymeleaf views.

## Extracting fragments

You may notice in the above Thymeleafe template, that most, but not all HTML is included.  Some has been replaced with a placeholder.

    <@snippet "src/main/webapp/WEB-INF/views/home.html" "layout" "/complete"/>

This is a Thymeleaf import of an externally defined fragment.

While developing with Thymeleaf, it is generally recommended that any page you make (including templates) are fully formed and viewable by a human.  This is different to many other frameworks, but gives some great benefits during development.
    
The above template references another called `layout.html`, and a fragment named `head` inside that.  Two others are also referenced in the `home` above, `left` and `footer`.  

Create a new html file:

!!!IMPORT path="src/main/webapp/WEB-INF/views/layout.html" prefix="complete"

Now, run the application:

```
    ./gradlew tomcatRunWar
```

And visit (http://localhost:8080/)[http://localhost:8080]. You will see a rich HTML page, including the site url, and the basket page.

Once you add a couple of items to the basket, it will look similar to:

![Basket](../images/page_basket.png)

## Summary

You have extended the application to show the menu in HTML and allow a user to select the items they are interested in and put them into a session backed Basket object, that is only present in the Web Domain.

See the current state of your application in the following Life Preserver:

![Life Preserver showing Web Domain with initial Components](../images/life-preserver-8.png)

Next, you will extend the application to allow creating an Order from the Basket, which will require you to accept and validate customer information.

[Next.. Accepting user submitted data](../5/)
