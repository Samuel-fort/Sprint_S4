<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Administration</title></head>
<body>
<h1>Espace administrateur</h1>
<p>Bienvenue <%= h(request.getAttribute("user")) %>.</p>
<p><a href="<%= request.getContextPath() %>/secret">Retour</a></p>
</body>
</html>
