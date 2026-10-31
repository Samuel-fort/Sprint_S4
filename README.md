# Framework MVC Java — Projet S4

Petit framework MVC fait main, un peu comme une version simplifiée de Spring MVC.
L'idée est de comprendre, étape par étape, ce qui se passe "sous le capot" quand on tape une URL dans le navigateur et qu'une méthode Java précise répond à la place.

Le projet avance par sprints. Chaque sprint ajoute une brique au-dessus de la précédente, sans casser ce qui marchait avant.

## Comment ça marche, en gros

1. Tomcat démarre l'application.
2. Un `Listener` scanne le package des contrôleurs pour repérer les classes annotées `@Controller`, puis leurs méthodes annotées `@URLMapping`.
3. Toutes les requêtes (`/*`) passent par une seule servlet, le `FrontServlet`, qui joue le rôle d'aiguilleur.
4. Selon l'URL demandée, le `FrontServlet` va chercher la bonne classe / méthode et répond en conséquence.

C'est exactement le principe du **Front Controller**, qu'on retrouve dans Spring MVC (la `DispatcherServlet`), Symfony, Laravel, etc. Ici on le reconstruit nous-mêmes pour comprendre comment ça fonctionne réellement.

## Structure du projet

```
Framework/
├── src/
│   ├── java/
│   │   └── mg/itu/
│   │       ├── annotation/
│   │       │   ├── Controller.java        → marque une classe comme contrôleur
│   │       │   └── URLMapping.java        → associe une méthode à une URL
│   │       ├── framework/
│   │       │   ├── FrontServlet.java          → reçoit toutes les requêtes, aiguille
│   │       │   ├── AppContextListener.java    → scanne les contrôleurs au démarrage
│   │       │   ├── ClassScanner.java          → retrouve les classes annotées @Controller
│   │       │   ├── Mapping.java               → représente une association URL ↔ classe/méthode
│   │       │   └── UrlNotSupportedException.java
│   │       └── controllers/
│   │           ├── HelloController.java   → contrôleur de test (Sprint 0/1)
│   │           └── DeptController.java    → contrôleur de test (Sprint 2)
│   └── webapp/
│       └── WEB-INF/
│           ├── web.xml     → déclare la servlet et le listener
│           └── app.xml     → mapping URL → classe (système Sprint 0, en XML)
├── lib/
│   └── servlet-api.jar
├── build/                  → généré par deploy.sh, ne pas modifier à la main
└── deploy.sh                → compile et déploie l'app sur Tomcat
```

## Déployer et lancer le projet

Tomcat doit déjà être installé et `deploy.sh` configuré avec le bon chemin vers `webapps/`.

```bash
./deploy.sh
```

Le script compile les sources, génère un `.war`, et le copie dans le dossier `webapps` de Tomcat. Tomcat se charge ensuite du déploiement automatiquement.

Une fois lancé, l'app est accessible sur :
```
http://localhost:8080/framework/
```

## Où en est le projet, sprint par sprint

### Sprint 0 — Les bases

But : vérifier qu'on peut faire transiter une requête HTTP jusqu'à une classe Java, en passant par un mapping écrit en XML.

- `app.xml` déclare les associations URL → nom de classe.
- `FrontServlet` lit ce fichier au démarrage (`init()`), retient les correspondances, et au moment d'une requête cherche la bonne classe et l'affiche.

**À tester :**
```
http://localhost:8080/framework/hello
```
→ affiche la classe associée à `/hello`.

### Sprint 1 — Détection automatique par annotation

But : ne plus dépendre uniquement du XML. On introduit une annotation `@Controller` que l'on pose sur une classe, et le framework la détecte lui-même au démarrage, sans rien écrire dans un fichier de config.

- `Controller.java` : l'annotation, simple marqueur de classe.
- `ClassScanner.java` : parcourt le package `mg.itu.controllers`, charge chaque classe, vérifie si elle porte `@Controller`.
- `AppContextListener.java` : exécuté au démarrage de l'app (avant toute servlet), il lance le scan et dépose le résultat dans le `ServletContext` — une sorte de mémoire partagée accessible par toute l'application.
- `FrontServlet` récupère cette liste depuis le contexte, plutôt que de la calculer lui-même.

**À tester :**
```
http://localhost:8080/framework/controllers
```
→ liste les classes détectées comme contrôleurs.

### Sprint 2 — Associer une méthode précise à une URL

But : aller plus loin que "cette classe est un contrôleur". On veut savoir précisément quelle **méthode**, dans quelle classe, doit traiter telle URL — la vraie logique d'un routeur.

- `URLMapping.java` : nouvelle annotation, posée cette fois sur une **méthode** (pas une classe), qui porte l'URL en attribut. Exemple :
  ```java
  @URLMapping("/deptnew")
  public String create() { ... }
  ```
- `AppContextListener` est enrichi : après avoir trouvé les classes `@Controller`, il boucle maintenant sur **toutes leurs méthodes**, repère celles annotées `@URLMapping`, et construit une liste de correspondances (URL → classe + méthode).
- `Mapping.java` a été étendu avec un attribut `methodName` pour pouvoir stocker cette information (l'ancien système Sprint 0 continue de fonctionner avec le constructeur d'origine).
- `FrontServlet` sait désormais répondre : si l'URL demandée correspond à une méthode connue, il l'affiche ; sinon, il lève une `UrlNotSupportedException` et affiche la liste des URLs réellement supportées, pour aider à comprendre l'erreur.

**Important :** à ce stade, la méthode trouvée n'est pas encore **exécutée** — on se contente de la localiser. L'exécution réelle (via réflexion) arrivera au Sprint 3.

**À tester :**
```
http://localhost:8080/framework/mvc/deptnew
```
→ affiche la classe et la méthode trouvées pour cette URL.

```
http://localhost:8080/framework/mvc/uneurlquinexistepas
```
→ affiche un message "URL not supported", suivi de la liste des URLs connues.

## Décisions techniques retenues

- **Serveur** : Tomcat
- **API Servlet** : `jakarta.servlet` (Tomcat 10+)
- **Package du framework** : `mg.itu.framework`
- **Package des contrôleurs** : `mg.itu.controllers`
- **Package des annotations** : `mg.itu.annotation`
- Le `FrontServlet` intercepte toutes les URLs (`/*` dans `web.xml`) — un seul point d'entrée pour toute l'application, comme un vrai Front Controller.

## Organisation Git

Chaque sprint se développe sur sa propre branche (`sprint0`, `sprint1`, `sprint2`, ...), puis fait l'objet d'une Pull Request vers `main` une fois terminé et testé.

```bash
git checkout main
git pull origin main
git checkout -b sprintX
# ... travail ...
git add .
git commit -m "Sprint X : description du travail fait"
git push -u origin sprintX
```
Puis ouverture de la Pull Request sur GitHub (`sprintX` → `main`).


## Sprint 11 — contrôleurs singleton / non singleton

- Par défaut, un contrôleur est un **singleton** : une seule instance, créée au démarrage (`AppContextListener`) et partagée par toutes les requêtes. Ses attributs sont conservés d'une requête à l'autre.
- `@NotSingleton` sur la classe : une **nouvelle instance est créée à chaque requête** (`Mapping.instanceForRequest()`). Ses attributs repartent à zéro.
- Ne jamais stocker de données propres à un utilisateur dans un singleton : utiliser la `Session`.
- Test : `/singleton/compteur` (le compteur monte à chaque rechargement) et `/notsingleton/compteur` (toujours 1).
