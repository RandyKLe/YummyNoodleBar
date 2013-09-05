Now that you have [added rich views and a basket](../4/), as on your life preserver below, its time to add the ability to place an order.

![Life Preserver Full showing Core Domain and Web Domain](../images/life-preserver-rest-domain-and-controllers-and-core-domain-zoom-out.png)


## Step 5: Accepting user submitted data

For Yummy Noodle Bar to accept the orders that a user is making, it needs to know where to send it.   Your users will need to give their name and address.

To do this, you must:

* Add a checkout URL - "/checkout"
* Show an HTML form on GET
* Process the form information on POST
* Convert the Basket into an Order and send it to the core.

You will continue working within the Web domain, first created in [step 2](../2/).

### Create the Checkout Controller

The Basket you created in the last section will contain all the items that a user wants to order.  When they want to place their Order, you need to also collect

To do this you will create a new Controller, and have that Controller accept a Command Object.  

A command Object is a bean that is used to model an HTTP request.  It does this by automatically mapping request parameters onto the properties of the bean.   These properties can then be tested using *Validation*.

Java has a standard *Validation* API, which you will need to include in the dependencies section of `build.gradle`.

<@snippet "build.gradle" "validation" "complete"/>

Since the Validation specification only defines an API, you need to include an implementation of that API as well.  Above, you have included Hibernate Validator.

#### Start with a test

As you should expect, you will first write a test to describe the features you want to implement.  Once those tests are ready, you can then safely implement the features themselves.

Add this test into your project.

<@snippet path="src/test/java/com/yummynoodlebar/web/controller/CheckoutIntegrationTest.java" prefix="complete"/>

TODO Describe setup of a test with a view resolver.

<@snippet "src/test/java/com/yummynoodlebar/web/controller/CheckoutIntegrationTest.java" "init" "complete"/>

Following that are tests that check:

* The basket is correctly added to the model.
* The view forward is correct.
* The checkout controller will redirect to the url `/order` if the POST is complete and correct.
* The checkout controller will forward back to the url `/checkout` if the POST is incomplete.

This breaking up of tests follows the Clean Code principles laid out by Rob C. Martin.

The applicable principles here are that each test method is checking a single *concept* in the functionality of the URL.  The number of assertions involved in each test is irrelevant so long as they are all part of building confidence in a single function of the system.

#### Storing Customer Information, enter the Command Object

The first thing you need to do is introduce the new concepts you need into the system.  The first is the command object.

When you submit a POST request to /checkout, it will contain a set of POST variables in the request that will include information about a user.

You could parse these variables yourself and check their contents according to whatever rules you want to apply.   This happens so often, however, that Spring supplies a lot of functionality to ease your implementation.

The Command Object is a class that Spring will map the POST variables onto, parsing them into the given types on the class.  For example, if you have an `int` property on the Command Object, Spring will take the textual value supplied in the request and attempt to parse an `int` out of it.   This process of automatic parsing and conversion is known as *Binding*, and you can find out more in the [reference documentation](https://docs.springframework.io/spring/docs/3.2.4.RELEASE/spring-framework-reference/html/)

Command Objects are also the ideal place for validation.  

Create a new entity class, `CustomerInfo`, like so.

<@snippet path="src/main/java/com/yummynoodlebar/web/domain/CustomerInfo.java" prefix="complete"/>

This entity concept will only exist in the Web Domain.  LP

In it you have added validation annotations `@NotNull` and `@NotEmpty` from the standard validation API and a Hibernate Validator extension, respectively.

These do not do anything by themselves, they need to be interpreted by some Validator class.   This is done on request in a Spring MVC Controller, which you will implement in the next section.

#### Implement the controller

Now that the Command Object is ready to represent the incoming POST request, you can implement the Controller.

Create a new class `CheckoutController`, like so.

<@snippet path="src/main/java/com/yummynoodlebar/web/controller/CheckoutController.java" prefix="complete"/>

The controller provides two implementations for the same URL `/checkout`. 

If you access the URL with a HTTP GET, you will provided with the /checkout view (which will be resolved to the checkout.jsp).

If you access the URL with a HTTP POST, then the Controller expects that a form has been submitted.  To process this form, it uses the Command Object `customerInfo`, of type `CustomerInfo`.

You will notice the `@ModelAttribute` annotation on the `customerInfo` parameter, and a matching method below 

<@snippet "src/main/java/com/yummynoodlebar/web/controller/CheckoutController.java" "customerInfo" "complete"/>

Together, these declare the CustomerInfo class to be a Command Object. When the page is rendered for the first time on a `GET /checkout`, the method `getCustomerInfo` is called to generate the 'customerInfo' property in the model.  You could pre-populate this is you wanted to in the `getCustomerInfo` method.   This property is then available in the model for the View to use during rendering, which you will see in the next section.

Using CustomerInfo as a parameter means that Spring will perform Binding of the request parameters against it.  If the binding did not complete successfully, then the result is stored in the `BindingResult` parameter.

In this case, the method immediately re-renders the checkout view.  The CustomerInfo instance, that did not bind correctly, will be available in the view to render, and you will see what support you have in the next section.

You will also notice a `@Valid` annotation on the `CustomerInfo`, this indicates to Spring that this instance should be *validated*.  This will use the annotations you added earlier to check the fields.  If the fields all pass the validation rules you have specified, then the bean is deemed to be valid, if not then it is invalid and the binding will fail.

#### Create the Checkout View

Now that the checkout URL is available and tested, its time to add the View to show the form to collect the customer information you need.

This view needs to populate the CustomerInfo bean

!!!IMPORT path="src/main/webapp/WEB-INF/views/checkout.jsp" prefix="complete"

### Show the Order Status

Once the user has successfully checked out and placed their Order, you need to show them a screen that displays the Order they have placed and the current status of it.   This allows the kitchen to update the status of the Order and the user to see that new status.

#### Start with a test

This is a relatively straightforward addition to the code you've written before.  As with the CheckoutController, you first need to write a test, like so.

<@snippet path="src/test/java/com/yummynoodlebar/web/controller/OrderStatusIntegrationTest.java" prefix="complete"/>

#### Implement the controller

Now that you have a test, you need to implement the controller, which will look something like this

<@snippet path="src/main/java/com/yummynoodlebar/web/controller/OrderStatusController.java" prefix="complete"/>

#### Create the View

Lastly, create the view for the order and its status.

!!!IMPORT path="src/main/webapp/WEB-INF/views/order.jsp" prefix="complete"

## Summary

You have successfully captured some user information in a form, mapped this onto a command object, validated it and combined it with the basket to create a fully functioning Order.

See the current state of your application below LP

Your application is a little too open with its information, however.  In the next section, you will learn how to apply security to your application and control who has access to which parts of your website, using Spring Security.

[Next.. Securing the Web Application](../6/)

