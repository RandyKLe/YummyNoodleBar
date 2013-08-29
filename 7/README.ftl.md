## Step 8: Securing the Web Application

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
