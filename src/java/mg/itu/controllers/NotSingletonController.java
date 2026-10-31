package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.NotSingleton;
import mg.itu.annotation.URLMapping;

/** Sprint 11 : une nouvelle instance par requête. Le compteur vaut toujours 1. */
@Controller
@NotSingleton
public class NotSingletonController {

    private int compteur;

    @URLMapping("/notsingleton/compteur")
    public String compteur() {
        compteur++;
        return "Non singleton : compteur = " + compteur;
    }
}
