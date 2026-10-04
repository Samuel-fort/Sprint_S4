package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.RestAPI;
import mg.itu.annotation.URLMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DeptController {

    @URLMapping("/deptnew")
    public String create() {
        return "dept created";
    }

    @RestAPI
    @URLMapping("/api/dept")
    public Map<String, Object> apiDept() {
        Map<String, Object> dept = new LinkedHashMap<>();
        dept.put("id", 1);
        dept.put("nom", "Informatique");
        return dept;
    }
}