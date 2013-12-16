<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
<form method="POST" action="/AbsiUebung2/Overview" name="change">
<ul class="error">
<%
	@SuppressWarnings("unchecked")
    List<String> messages = (List<String>)request.getAttribute("messages");
	Iterator<String> it = messages.iterator();
    while (it.hasNext()) {
%>
	<li><%= it.next() %></li>
<% } %>
</ul>
<h1>RattleBits AG</h1>
<table>
	<tr>
		<th>Altes Passwort:</th>
		<td><input type="password" name="oldpassword" value=""/></td>
	</tr>
	<tr>
		<th>Password:</th>
		<td><input type="password" name="newpassword" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="change" value="Passwort &auml;ndern" /></td>
	</tr>
</table>
</form>
</body>
</html>