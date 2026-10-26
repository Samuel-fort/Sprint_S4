<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Upload de fichiers</title></head>
<body>
<h1>Upload de fichiers</h1>

<h2>Option 1 : Map&lt;String, Object&gt;</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/map">
    <p><label>Nom : <input type="text" name="nom"></label></p>
    <p><label>Photo : <input type="file" name="photo"></label></p>
    <p><button type="submit">Envoyer</button></p>
</form>

<h2>Option 2 : objet avec attributs Mapfile (recto, verso)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/bean">
    <p><label>Nom : <input type="text" name="personne.nom"></label></p>
    <p><label>Recto : <input type="file" name="personne.recto"></label></p>
    <p><label>Verso : <input type="file" name="personne.verso"></label></p>
    <p><button type="submit">Envoyer</button></p>
</form>
</body>
</html>
