package gr.uoi.dthink.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MyRestController {
    @RequestMapping(value = "/testrest", method = RequestMethod.GET)
    public String testRest() {
        return "test";
    }
}
