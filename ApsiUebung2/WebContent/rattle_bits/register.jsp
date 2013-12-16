<%@ page import="java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>RattleBits</title>
<style>
.error {
	color: red;
}
</style>
</head>
<body>
<form method="POST" action='/AbsiUebung2/Register' name="register">
<ul class="error">
<%
	@SuppressWarnings("unchecked")
    List<String> messages = (List<String>)request.getAttribute("messages");
	if (messages != null && messages.size() > 0) {
		Iterator<String> it = messages.iterator();
    	while (it.hasNext()) {
%>
	<li><%= it.next() %></li>
<% 		
		} 
    }
%>
</ul>
<table>
	<tr>
		<th>Firma:</th>
		<td><input type="text" name="firma" value="${param.firma}"/></td>
	</tr>
	<tr>
		<th>Adresse:</th>
		<td><input type="text" name="address" value="${param.address}" /></td>
	</tr>
	<tr>
		<th>PLZ:</th>
		<td><input type="text" name="plz" value="${param.plz}" /></td>
	</tr>
	<tr>
		<th>Stadt:</th>
		<td><input type="text" name="town" value="${param.town}" /></td>
	</tr>
	<tr>
		<th>E-Mail:</th>
		<td><input type="text" name="mail" value="${param.mail}" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="register" value="Registrieren" /></td>
	</tr>
</table>
</form>
</body>
</html>