<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Page protégée</title></head>
<body>
<h1>Page protégée</h1>
<p>Bonjour <%= h(request.getAttribute("user")) %>, vous êtes connecté (rôle : <%= h(request.getAttribute("role")) %>).</p>
<p>
    <a href="<%= request.getContextPath() %>/admin">Page réservée aux ADMIN</a> |
    <a href="<%= request.getContextPath() %>/logout">Se déconnecter</a>
</p>
</body>
</html>
