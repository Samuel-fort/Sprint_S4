<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.*" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Demo ModelView</title></head>
<body>
<h1><%= h(request.getAttribute("titre")) %></h1>
<ul>
<%
    List<?> items = (List<?>) request.getAttribute("items");
    if (items != null) {
        for (Object item : items) {
%>
    <li><%= h(item) %></li>
<%
        }
    }
%>
</ul>
</body>
</html>
