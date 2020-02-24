package gr.uoi.dthink.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().compareTo("anonymousUser")==0) {
            User u=userService.findByUserID(1L);
            model.addAttribute("guest", u);
            return "index";
        }*/
        return "pages/index";
    }

    @RequestMapping(value = "/dashboard")
    public String temp(Model model){
        return "pages/user/dashboard";
    }
}
