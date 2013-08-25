

GET "/"
Show menu list

POST "/addToBasket/{menuId}" 
add to curretn basket and redirect to /

POST "/removeFromBasket/{menuId}" 
remove item from basket and redirect to /showBasket

GET "/showBasket"
show current basket

GET "/checkout"
show form to enter customer information, posts to /doCheckout

POST "/doCheckout"
Take the current basket and create an order from it, redirect to "/order/{id}"

GET "/order/{id}"
view the status of a given order


