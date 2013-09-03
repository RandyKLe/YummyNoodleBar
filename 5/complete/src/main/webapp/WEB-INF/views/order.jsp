<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>Order Confirmed</title>
</head>
<body>
	<div class="hero-unit">
		<h3>Your order is confirmed</h3>

	</div>

	<div class="row-fluid">
		<div class="span8">
			<p class="text-info">${orderStatus.name} thanks for your order</p>
			<p class="text-info">Your order number is ${orderStatus.orderId}</p>
           	<p class="text-info">The estimate for cooking is 20 minutes</p>
           	
           	<p class="text-success">The status is currently ${orderStatus.status}</p>
           	
            <div>
                Refresh this page to see updates to the status
            </div>
		</div>
	</div>
</body>
</html>
