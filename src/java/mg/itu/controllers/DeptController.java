package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;

@Controller
public class DeptController {

    @URLMapping("/deptnew")
    public String create() {
        return "dept created";
    }
}
