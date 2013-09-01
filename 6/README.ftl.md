You've set up functionality for the Yummy Noodle Bar Web front end and your Life Preserver is looking great at this point:

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-rest-domain-and-controllers-and-core-domain-and-config-domain-initial-zoom-out.png)

The problem is, at this point the Yummy Noodle Bar Web site is a little *too* functional; Orders need to be recorded against users, and only the user that placed an Order should be able to view and manage it.

## Step 8: Securing the Web Application

Once again, all changes here are constrained to the Configuration domain:

![Life Preserver showing Configuration Domain with Initial Components](../images/life-preserver-initial-config-domain-focus.png)


## Web security basics

To limit access to the Yummy Noodle Bar Web font end, you extend the initial web design as follows:

* URLs that need to have a user will be protected, and if no user authentication is present, will issue a 302 to the login form.
* A security token will be placed in the HTTP Session in the web container.
* The session will be loaded on every request against the JSESSIONID cookie that is placed in the users browser by a response cookie.

Spring Security helps you perform these steps without you having to change so much as a single controller!

## Add Spring Security to your project

First you add Spring Security dependencies to the project, by adding the following entries to your `build.gradle` script:

    <@snippet "build.gradle" "repos" "/complete" />
    
    <@snippet "build.gradle" "security" "/complete" />

You add the Pivotal milestone repository so that you can use Spring Security 3.2.0.M2. This lets you use some dynamic configuration features of Spring Security, including setting up the web security through JavaConfig.

Now you need to secure your controllers. Until now you've been writing tests first before making any code changes, including configuration. So, instead of immediately adding your security configuration, you'll create a test so that you'll know when your security is being applied correctly.


## Test for security


You can now add a new concern to your application: security configuration. Create a new Spring configuration in com.yummynoodlebar.config named `SecurityConfig` that contains the following:

    <@snippet path="src/main/java/com/yummynoodlebar/config/SecurityConfig.java" prefix="complete" />

This configuration enables security using the `@EnableWebSecurity` annotation, and extends the `WebSecurityConfigurerAdapter` so that you can perform more detailed configuration of the web security you're applying.

The `registerAuthentication` method is overridden from `WebSecurityConfigurerAdapter` in order to configure an in-memory database of users that contains a single user, 'letsnosh', with the USER role. 

RESTful services are usually consumed programmatically via clients, such as your test code. It doesn't make much sense to implement a usual challenge+login page system as there is no one to enter the information into the login page if it was actually presented. 

Instead here inside the `configure` overridden method from `WebSecurityConfigurerAdapter` you've configured URL level protection using the `http.authorizeUrls()` method. The `http.authorizeUrls()` method protects the /aggregators/** urls, ensuring that only users with the USER role can access them. This means that access to your RESTful URIs must include an HTTP BASIC Authorization Header, and that any request without this header will be responded to with a 403 (Forbidden) HTTP Status Code.


## Configure the Spring Security filter chain

Spring Security relies on a Servlet filter to apply your security configuration. A filter is used so that security is applied before the Spring MVC Dispatcher Servlet gets involved in processing incoming requests. The Spring Security filter is referred to as the Spring Security filter chain as it actually delegates to a chain of filters internally that each apply one aspect of the security responsibility.

You now need to configure this filter chain by updating the web application configuration you created earlier. In the previous section, you configured things in the `WebAppInitializer` class.

The first step is to simply add your new `SecurityConfig` JavaConfig class to the root context

    <@snippet "src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" "addToRootContext" "/complete" />

Now you can add the Spring Security Filter:

    <@snippet "src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" "configureSpringSecurity" "/complete" />

This sets up a Spring `DelegatingFilterProxy` with the `rootContext` and is called from the `onStartup()` method.

    <@snippet "src/main/java/com/yummynoodlebar/config/WebAppInitializer.java" "onStartup" "/complete" />

The name `springSecurityFilterChain` for the filter chain is important as this means that the filter will pass all calls down to a Spring Bean named `springSecurityFilterChain` that it finds in the `rootContext`. You configured this bean using `@Configuration` in the Spring JavaConfig class `SecurityConfig`.









wire up security, as per REST.

that'll do for authentication.

Now that we can get at authentication

ensure that order creation saves the authentication.name into the order.


### moving onto authorisation..

ensure that previously we have created a method to get hold of an Order. 

annotate the method that gets the order.

@PreAuthorize("#order.owner == authentication.name")
public void doSomething(Order order);

This method needs to be on some service or other as we can't load the Order directly, as we need to fire events at the core to do the loading.

need to ensure that the order.owner and authentication.name (what is authentication, could this be principle?) are the same.
