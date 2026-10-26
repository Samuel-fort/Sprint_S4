<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="java.util.*, mg.itu.framework.Mapfile" %>
<%!
    static String h(Object o) {
        return o == null ? "" : o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Résultat de l'upload</title></head>
<body>
<h1><%= h(request.getAttribute("titre")) %></h1>
<table border="1" cellpadding="6">
    <tr><th>Champ</th><th>Valeur reçue</th></tr>
<%
    Map<?, ?> donnees = (Map<?, ?>) request.getAttribute("donnees");
    if (donnees != null) {
        for (Map.Entry<?, ?> entry : donnees.entrySet()) {
            Object valeur = entry.getValue();
            String texte;
            if (valeur instanceof Mapfile) {
                Mapfile fichier = (Mapfile) valeur;
                texte = "fichier : " + fichier.getNomComplet() + " (nom=" + fichier.getNom()
                        + ", extension=" + fichier.getExtension() + ", " + fichier.getTaille() + " octets)";
            } else if (valeur instanceof String[]) {
                texte = String.join(", ", (String[]) valeur);
            } else {
                texte = valeur == null ? "(vide / aucun fichier)" : valeur.toString();
            }
%>
    <tr><td><%= h(entry.getKey()) %></td><td><%= h(texte) %></td></tr>
<%
        }
    }
%>
</table>
<p><a href="<%= request.getContextPath() %>/upload/form">Retour au formulaire</a></p>
</body>
</html>
