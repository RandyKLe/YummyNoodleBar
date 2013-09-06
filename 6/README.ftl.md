You've set up functionality for the Yummy Noodle Bar Web front end and your Life Preserver is looking great at this point:

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-rest-domain-and-controllers-and-core-domain-and-config-domain-initial-zoom-out.png)

The problem is, at this point the Yummy Noodle Bar Web site is a little *too* functional; Orders need to be recorded against users, and only the user that placed an Order should be able to view and manage it.

## Step 6: Securing the Web Application

Once again, all changes here are constrained to the Configuration domain:

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-initial-config-domain-focus.png)


### Authentication on the Web

To limit access to the Yummy Noodle Bar Web font end, you extend the initial web design as follows:

* URLs that need to have a known user will be protected, and if no user authentication is present, will issue a 302 to the login form.
* A security token will be placed in the HTTP Session in the web container.
* The session will be loaded on every request against the JSESSIONID cookie that is placed in the users browser by a response cookie.

Spring Security helps you perform these steps without you having to change so much as a single controller!

#### Add Spring Security to your project

First you add Spring Security dependencies to the project, by adding the following entries to your `build.gradle` script:

    <@snippet "build.gradle" "repos" "/complete" />
    
    <@snippet "build.gradle" "deps" "/complete" />

You add the Pivotal milestone repository so that you can use Spring Security 3.2.0.M2. This lets you use some dynamic configuration features of Spring Security, including setting up the web security through JavaConfig.

Now you need to secure your controllers. Until now you've been writing tests first before making any code changes, including configuration. So, instead of immediately adding your security configuration, you'll create a test so that you'll know when your security is being applied correctly.


#### Configure Spring Security

You can now add a new concern to your application: security configuration. Create a new Spring configuration in com.yummynoodlebar.config named `SecurityConfig` that contains the following:

    <@snippet path="src/main/java/com/yummynoodlebar/config/SecurityConfig.java" prefix="complete" />

This configuration enables security using the `@EnableWebSecurity` annotation, and extends the `WebSecurityConfigurerAdapter` so that you can perform more detailed configuration of the web security you're applying.

The `registerAuthentication` method is overridden from `WebSecurityConfigurerAdapter` in order to configure an in-memory database of users, their passwords and associated roles.

The `configure`, overridden method from `WebSecurityConfigurerAdapter`, method provides a fine grained fluent API for controlling how the security system will be applied.

    <@snippet "src/main/java/com/yummynoodlebar/config/SecurityConfig.java" "configure" "complete" />

Here, you've configured URL level protection using the `http.authorizeUrls()` method. The `http.authorizeUrls()` method protects the /checkout and /order/* urls, ensuring that only users with the USER role can access them.  

This will force users to log in before checking out, and ensure that only logged in users can view orders.  The `formLogin()` method call instructs Spring Security that users will login via an HTML form.  We give no further information on how this will work, and so Spring Security will generate a new HTML form and URL for you available on `/login`.


### Configure the Spring Security filter chain

Spring Security relies on a Servlet filter to apply your security configuration. A filter is used so that security is applied before the Spring MVC Dispatcher Servlet gets involved in processing incoming requests. The Spring Security filter is referred to as the Spring Security filter chain, as it actually delegates to a chain of filters internally that each apply one aspect of the security responsibility.

You now need to configure this filter chain by updating the web application configuration you created earlier. In the previous section, you configured things in the `WebAppInitializer` class.

The first step is to simply add your new `SecurityConfig` JavaConfig class to the root context

    <@snippet "src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" "addToRootContext" "/complete" />

Next, Spring Security needs to be inserted into the web context setup.   This could be done in `WebAppInitializer`, however a better option in this case is to add a second web app initializer class specifically for the security setup.

    <@snippet path="src/main/java/com/yummynoodlebar/config/SecurityWebAppInitializer.java" prefix="/complete" />

This configures the Spring Security filter chain and manages inserting it into the web context.

It is important that the Spring Security setup is done before the DispatcherServlet configuration in `WebAppInitializer`.  The `@Order` annotation from Spring Core can be used to manage the order of execution.

The full classes now look like

<@snippet path="src/main/java/com/yummynoodlebar/config/SecurityWebAppInitializer.java" prefix="/complete" />

<@snippet path="src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" prefix="/complete" />

### Pulling it together

Run the application.

```sh
$ ./gradlew tomcatRunWar
```

When you visit [http://localhost:8080/](http://localhost:8080) you will be able to see the available menu and add items to your basket, as before.  However now, if you click checkout on the basket screen and visit [http://localhost:8080/checkout](http://localhost:8080/checkout), you will instead be redirected to a login screen and forced to log in before beign able to proceed.

Enter the user name and password you configured in `SecurityConfig`: user `letsnosh` and password `noshing`.  You will then be taken to the checkout page, as before.  This time, however, you are logged in.

## Summary

You have secured your application using Spring Security, and Yummy Noodle Bar is getting excited!

See the current state of your application below LP

Next, you will use some of the more advanced features of the web to push data from the Server to the browser to create a lower latency, more highly scalable website.

[Next.. Accepting user submitted data](../7/)
