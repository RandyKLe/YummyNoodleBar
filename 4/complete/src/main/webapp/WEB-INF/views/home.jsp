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
		</p>

	</div>

	<div class="row-fluid">
		<div class="span8">

			<div id="message" class="alert alert-info">
				<spring:message code="message.home.instructions" />
			</div>

			<table class="table table-striped">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Cost</th>
						<th>Mins to prepare</th>
					</tr>
				</thead>
				<tbody>

					<c:forEach var="item" items="${menuItems}">

						<tr>
							<td>${item.id}</td>
							<td>${item.name}</td>
							<td>${item.cost}</td>
							<td>${item.minutesToPrepare}</td>
						</tr>

					</c:forEach>

				</tbody>
			</table>

		</div>
	</div>
</body>
</html>