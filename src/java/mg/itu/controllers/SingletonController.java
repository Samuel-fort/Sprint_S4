package mg.itu.controllers;

import java.util.concurrent.atomic.AtomicInteger;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;

/** Sprint 11 : contrôleur singleton (par défaut). Le compteur monte à chaque requête. */
@Controller
public class SingletonController {

    private final AtomicInteger compteur = new AtomicInteger();

    @URLMapping("/singleton/compteur")
    public String compteur() {
        return "Singleton : compteur = " + compteur.incrementAndGet();
    }
}
