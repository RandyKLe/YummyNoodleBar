<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>Place your order</title>
</head>
<body>
	<div class="hero-unit">
		<h3>Where do we deliver your Order?</h3>

	</div>

	<div class="row-fluid">
		<div class="span8">

            <c:if test="${not empty message}">
                <div id="message" class="alert alert-info">
                    ${message}
                </div>
            </c:if>

            <form:form commandName="customerInfo">
                <table>
                    <tr>
                        <td>Name:</td>
                        <td><form:input path="name" /></td>
                        <td><form:errors path="name" /></td>
                    </tr>

                    <tr>
                        <td>Address:</td>
                        <td><form:input path="address1" /></td>
                        <td><form:errors path="address1"  /></td>
                    </tr>
                    <tr>
                        <td>Postal Code:</td>
                        <td><form:input path="postcode" /></td>
                        <td><form:errors path="postcode"  /></td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <input type="submit" value="Save Changes" />
                        </td>
                    </tr>
                </table>
            </form:form>
		</div>
	</div>
</body>
</html>