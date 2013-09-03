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
		<h3>Yummy</h3>

		<p>
			<spring:message code="message.welcome" />
		</p>
		<p>
			<a class="btn btn-primary btn-large"
				href="http://www.simplicityitself.com/"><spring:message
					code="message.home.learnMore" /></a>
			<c:if test="${basket.size > 0}">
				<a class="btn btn-primary btn-large" href="<spring:url value="/showBasket" htmlEscape="true" />">Look in your basket</a>
			</c:if>
		</p>

	</div>

	<div class="row-fluid">
		<div class="span8">

			<div id="message" class="alert alert-info">
				<spring:message code="message.home.instructions" /> . Number of items in basket: ${basket.size}
			</div>

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

					<c:forEach var="item" items="${menuItems}" varStatus="status">
						<c:set var="itemFormId" value="item${status.index}"/>
						<tr>
							<td>${item.id}</td>
							<td>${item.name}</td>
							<td>${item.cost}</td>
							<td>${item.minutesToPrepare}</td>
							<td>
							
							<form id="${itemFormId}" action="/addToBasket" method="POST">
								<input id="id" name="id" type="hidden" value="${item.id}" />
								<input id="name" name="name" type="hidden" value="${item.name}" />
								<input id="cost" name="cost" type="hidden" value="${item.cost}" />
								<input id="minutesToPrepare" name="minutesToPrepare" type="hidden" value="${item.minutesToPrepare}" />
								<input type="submit" value="Add to basket" />
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