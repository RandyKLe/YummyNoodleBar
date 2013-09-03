<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	<div class="hero-unit">
		<h3>Basket</h3>
		<p>
			<a class="btn btn-primary btn-large" href="<spring:url value="/" htmlEscape="true" />">Continue eating</a>
			<c:if test="${basket.size > 0}">
				<a class="btn btn-primary btn-large" href="<spring:url value="/checkout" htmlEscape="true" />">Go ahead and order</a>
			</c:if>
		</p>
	</div>

	<div class="row-fluid">
		<div class="span8">
			
			<table class="table table-striped">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Cost</th>
						<th>Mins to prepare</th>
						<th>Action</th>
						
					</tr>
				</thead>
				<tbody>

					<c:forEach var="basketItem" items="${basket.items}">
						<c:set var="itemFormId" value="basketItem${status.index}"/>
						<tr>
							<td>${basketItem.id}</td>
							<td>${basketItem.name}</td>
							<td>${basketItem.cost}</td>
							<td>${basketItem.minutesToPrepare}</td>
							<td>
							<form id="${itemFormId}" action="/removeFromBasket" method="POST">
								<input id="id" name="id" type="hidden" value="${basketItem.id}" />
								<input type="submit" value="Remove" />
							</form>
							
							</td>
							
						</tr>

					</c:forEach>

				</tbody>
			</table>

		</div>
	</div>
</body>
</html>