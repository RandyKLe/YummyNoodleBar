## Step 1: Modelling the Core and Web Domains

For the first version of your new Yummy Noodle Bar Web front end, the ability to view the Menu and create and monitor Orders is the focus.

It is tempting simply to expose the Core Order domain to the outside world and work from there, but that would ignore the boundary between the Core and the Web domain and would lead to the Web front end being driven by the internal application structure, and so becoming coupled to that internal structure.

The user visible front end of your server (the Web domain) that you will expose to users needs to change at a rate that is friendly to those users. The Core needs to evolve at a rate that corresponds to the Yummy Noodle Bar system's need to evolve internally. Potential friction exists between the two domains as they may need to evolve at different rates.

To manage this friction you need to create concepts and components in the Web domain that are unique to, and can evolve at the rate needed by, the Web domain itself. This may result in similar types of components to those in the Core domain but because their purpose will be very different, the similarities are superficial.

In the Core domain the concepts are captured as part of the internal ubiquitous language of the application's domain. In the Web domain the concepts are captured as they are used purely for the purpose of exposing the public Web front end. 

### Components of the Core application domain for Yummy Noodle Bar

![Life Preserver showing Core Domain](../images/life-preserver-2.5.png)

Open the `initial` project. Under src/main/java/com/yummynoodlebar/core/domain, you see the components of the core, application-internal domain of Yummy Noodle Bar:

* **Customer**. A username, address and name that an Order will be delivered to.

* **Order**. An individual order in the system that has an associated status and status history for tracking purposes.

* **OrderStatus**. Current status allocated to an order.

This tutorial focuses on the Order domain classes, which can be acted upon by a number of events under the com.yummynoodlebar.events.orders package as shown on the following diagram:

Events in this case decouple out the domain concepts in the core of the Yummy Noodle Bar application from the various integrations that may need to access and work upon the core. 

The event components associated with Orders that you will use for the Web include:

* **CreateOrderEvent** and **OrderCreatedEvent**. Corresponding events to request the creation of a new Order, and a confirmation that the new Order has been created.

* **RequestOrderDetailsEvent** and **OrderDetailsEvent**. Corresponding events to request the current details of an Order, and then to receive those details.

* **RequestOrderStatusEvent** and **OrderStatusEvent**. Corresponding events to request the current status of an Order, and then to receive the current status.


### Model your Users interactions

When you are building a web application, the users you build it for are humans.  While this may seem obvious, it has massive implications for the design and model of your Web domain.

Most importantly :

* Users expect to be able to visit any URL they see again. You should expect URLs to be copy and pasted.
* Users expect to move around a website arbitrarily.
* Users expect to use the back and forward buttons at will.
* The users experience of HTTP GET and POST (from HTML forms) is dramatically different. A POST should only be used for submitting information, and never for navigation.

Given the above:
* Your URLs should be standalone and the server should be able to construct the entire page from the URL.
* You should provide links between the related pages on your site and not attempt to constrain users into a particular flow.

For the Yummy Noodle Bar, Users need to:

* View the Menu
* Add and remove items from an order Basket
* Send the Order to the kitchen.
* See the progress of the Order.


### Design your URLs

The following URLs will give that functionality in a way that the user can easily use and return to:

<table>
<thead>
<tr><th>Action</th><th>URL</th></tr>
</thead>
<tbody>
<tr><td>Show menu list</td><td>GET "/"</td></tr>
<tr><td>Add a Menu Item to the current basket and redirect to /</td><td>POST "/addToBasket?menuId={menuId}"</td></tr>
<tr><td>Remove a Menu Item from Basket and redirect to /showBasket</td><td>POST "/removeFromBasket?menuId={menuId}"</td></tr>
<tr><td>Show current Basket</td><td>GET "/showBasket"</td></tr>
<tr><td>Form to gather customer information, which posts to /doCheckout</td><td>GET "/checkout"</td></tr>
<tr><td>Take the current basket and create an order from it, redirect to "/order/{id}"</td><td>POST "/doCheckout"</td></tr>
<tr><td>View the status of a given order</td><td>GET "/order/{id}"</td></tr>
</tbody>
</table>
 

Note that every POST URL immediately redirects to another.  This allows the user to manually refresh the page at will after the POST has occurred without causing a double submission.


#### URI templates

Each of the above URIs are expressed as *templates*; they contain blocks demarcated with {} in the URI.  

For example, here the {} notation specifies where an Order with Order ID of 1 would have the following specific URL once the URI template is furnished with the Order Number:

    http://www.yummynoodlebar.com/order/1

An Order with an Order ID of 37 would have the following specific URI:

    http://www.yummynoodlebar.com/order/37

### Model View Controller (MVC)

Model View Controller (MVC) is an architecture design that is popularly used in user interface development, whether desktop applications or for the web.   It defines three major responsibilities in a UI and specifies how they should interact with each other.  

This separation allows an application to be built in a more scalable and testable way.

* **Controller** - Controllers are responsible for accepting user inputs, generating a data Model and then selecting a View to render the Model.  In Spring MVC, this is a class annotated with `@Controller`, with each method annotated with `@RequestMapping` handling a particular a user input.

* **Model** - The Model is provided to the view, and contains all the information it needs to render itself to show to the user.  This is represented in Spring MVC by the `Model` class.  Sometimes this class is not visible, and is generated from other information returned by the Controller method.

* **View** - The View is responsible for presenting information to the user, in the web, the View components will generate HTML and may contain JavaScript.  Views in Spring MVC can take many forms, however in this tutorial, all views will be coded using Thymeleaf.

### Summary

Congratulations!  You've determined the URLs and links between them that you are going to show to your users and captured those components in the following Life Preserver :

![Life Preserver showing initial Web Domain](../images/life-preserver-3.png)

[Next… Implementing URLs and returning data](../2/)
