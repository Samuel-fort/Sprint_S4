package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.RestAPI;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.HttpMethod;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DeptController {

    @URLMapping(value = "/deptnew", method = HttpMethod.GET)
    public String createGet() {
        return "dept created with GET";
    }

    @URLMapping(value = "/deptnew", method = HttpMethod.POST)
    public String createPost() {
        return "dept created with POST";
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
