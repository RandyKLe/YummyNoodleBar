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
            Your order has been received, and your order number is ${orderStatus.orderId}<br/>
            The estimate for cooking is 20 minutes. ${orderStatus.status}
            <div>
                Refresh this page to see updates to the status
            </div>
		</div>
	</div>
</body>
</html>
