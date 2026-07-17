package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;

@Controller
public class DeptController {

    @URLMapping(value = "/deptnew", method = "GET")
    public String createGet() {
        System.out.println("DeptController.createGet() appelée");
        return "dept created with GET";
    }

    @URLMapping(value = "/deptnew", method = "POST")
    public String createPost() {
        System.out.println("DeptController.createPost() appelée");
        return "dept created with POST";
    }
}
