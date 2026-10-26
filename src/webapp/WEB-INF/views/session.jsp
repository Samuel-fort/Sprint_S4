<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="java.util.*, mg.itu.framework.Session" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Session</title></head>
<body>
<h1>Contenu de la session</h1>
<%
    Session maSession = (Session) request.getAttribute("maSession");
%>
<p>Identifiant : <%= h(maSession.getId()) %></p>
<table border="1" cellpadding="6">
    <tr><th>Clé</th><th>Valeur</th></tr>
<%
    for (Map.Entry<String, Object> entry : maSession.getData().entrySet()) {
%>
    <tr><td><%= h(entry.getKey()) %></td><td><%= h(entry.getValue()) %></td></tr>
<%
    }
%>
</table>
<p>
    <a href="<%= request.getContextPath() %>/session/compteur">Incrémenter le compteur</a> |
    <a href="<%= request.getContextPath() %>/session/vider">Vider la session</a>
</p>
</body>
</html>
