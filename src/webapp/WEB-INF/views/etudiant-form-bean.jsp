<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Nouvel étudiant (objet)</title></head>
<body>
<h1>Création d'un étudiant (binding d'objet)</h1>

<form method="post" action="<%= request.getContextPath() %>/etudiants/save-bean">
    <p><label>Nom : <input type="text" name="etudiant.nom" required></label></p>
    <p><label>Âge : <input type="number" name="etudiant.age" required></label></p>
    <p><label>Email : <input type="email" name="etudiant.email"></label></p>
    <p><button type="submit">Enregistrer</button></p>
</form>

<p><a href="<%= request.getContextPath() %>/etudiants">Voir la liste</a></p>
</body>
</html>
