<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="java.util.*, mg.itu.models.Etudiant" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Étudiants</title></head>
<body>
<h1>Liste des étudiants</h1>
<table border="1" cellpadding="6">
    <tr><th>Nom</th><th>Âge</th><th>Email</th></tr>
<%
    List<?> etudiants = (List<?>) request.getAttribute("etudiants");
    if (etudiants != null) {
        for (Object o : etudiants) {
            Etudiant e = (Etudiant) o;
%>
    <tr><td><%= h(e.getNom()) %></td><td><%= e.getAge() %></td><td><%= h(e.getEmail()) %></td></tr>
<%
        }
    }
%>
</table>
<p><a href="<%= request.getContextPath() %>/etudiants/form">Ajouter un étudiant</a></p>
</body>
</html>
