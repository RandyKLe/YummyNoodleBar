
In this tutorial you'll use Spring to create a production strength Web application.

## Spring and the Web

The web has become a core part of our lives, from shopping to finding the closest ATM. Web applications, server software sending HTML over HTTP, implement the web.

Spring helps you build web applications that scale from a small internal applications to millions

Whether you are beginning the construction of a web application using Spring, or ar just curious on what a Spring backed web application looks like, you've come to the right place.

## What you'll build

Yummy Noodle Bar is going global. It wants to build an interactive web application to provide customers with the latest Yummky Noodle Menu and allow them to place orders and monitor the status of their order as it is being prepared.

You will extend Yummy Noodle Bar's existing application by adding an web components that will allow viewing the menu, creating and viewing the status of orders.

![Yummy Noodle Bar](images/yummynoodle.jpg)

## What you'll need

* About an hour.
* A copy of the code (TODO - downloadable as Zip and/or git clone).
* An IDE of your choice; Spring recommends [Spring Tool Suite](http://www.springsource.org/sts), which is a [free download](http://www.springsource.org/sts).

## Yummy Noodle Bar application architecture and the Core domain

The current architecture of the application is shown in the following "Life Preserver" diagram:

![Life Preserver showing Core packages](images/life-preserver-initial.png)

The Life Preserver diagram is a tool for building applications that following the principles of the [Hexagonal Architecture, sometimes referred to as 'Ports and Adapters' originally characterised by Alistair Cockburn](http://alistair.cockburn.us/Hexagonal+architecture). The Life Preserver diagram shows your application's core internal domains along with the surrounding 'integration' domains that map directly to the packages and components that you'll be working within and upon throughout this tutorial, so it's a great way to understand where things are.

Open the Initial project and you'll see that the life preserver diagram maps to the different packages under src/main/java/com/yummynoodlebar

Under the core application's top-level packages, that is, com.yummynoodlebar.core, here's what the packages contain:

* **domain**. Components that cleanly capture the application's Core domain concepts. These classes are a manifestation of the [ubiquitous language](http://martinfowler.com/bliki/UbiquitousLanguage.html) of the Core domain.

* **service**. Components that handle the actions that can be performed when an event is received.

Take a moment to familiarize yourself with the components in each package. The tests for the core domain components are available in the src/test area in the `initial` project. They will give you an idea of how these components will be used.

## Web domain

A web front end integrates your application with web browsers and their users. As such, the web front end lives in its own integration domain on the periphery of your application's core, as show in the following update to you life preserver.

TODO - new LF image.

Given the integration between your application and the outside world, consider the following design and implementation constraints:

* The user experience (UX) is your focus; the core application structure should not influence the design of the web front end.
* The components that make up your Web domain need to evolve at a rate that is appropriate for the many consumers that rely on your services.
* Your Web components should not contain any core logic for your application, but they will collaborate with other components in the Core domains of your application in order to orchestrate the necessary functionality for the service interface.


## Tutorial Roadmap

* [Step 1: Modelling the Core and Web Domains](1/)
* [Step 2: Implementing URLs and returning data](2/)
* [Step 3: Configuring a basic application](3/)
* [Step 4: Creating rich HTML views using JSP and Spring Tags](4/)
* [Step 5: Accepting user submitted data](5/)
* [Step 6: Uploading files](8/)
* [Step 7: Securing the Web Application](7/)
* [Step 8: Updating the Browser from the Server](8/)
* [Recap and What's Next?](9/)
