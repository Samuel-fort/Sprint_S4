<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Connexion</title></head>
<body>
<h1>Connexion</h1>
<% if (request.getAttribute("erreur") != null) { %>
<p style="color:red"><%= h(request.getAttribute("erreur")) %></p>
<% } %>
<% if (request.getAttribute("info") != null) { %>
<p style="color:green"><%= h(request.getAttribute("info")) %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/login">
    <p><label>Identifiant : <input type="text" name="login" required></label></p>
    <p><label>Mot de passe : <input type="password" name="motdepasse" required></label></p>
    <p><button type="submit">Se connecter</button></p>
</form>
</body>
</html>
