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
			<a class="btn btn-primary btn-large"
				href="<spring:url value="/" htmlEscape="true" />">Continue eating</a>
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
							<c:url var="removeFromBasketUrl" value="removeFromBasket/${basketItem.id}" />
							<form id="${itemFormId}" action="${removeFromBasketUrl}" method="POST">
								<input id="id" name="id" type="hidden" value="${basketItem.id}" />
							</form>
							<a href="javascript:document.forms['${itemFormId}'].submit();">Remove</a>
							
							
							</td>
							
						</tr>

					</c:forEach>

				</tbody>
			</table>

		</div>
	</div>
</body>
</html>