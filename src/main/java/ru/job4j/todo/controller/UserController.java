package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signup(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "signup";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/fail/{type}")
    public String fail(@PathVariable("type") String type, Model model, HttpSession session) {
        addUserToModel(model, session);
        String message;
        switch (type) {
            case "signup" :
                message = "Failed to sign up. This name is already taken. Please, choose another name.";
                model.addAttribute("message", message);
                return "fail";
            case "login" :
                message = "Failed to log in. Please, enter correct name and password.";
                model.addAttribute("message", message);
                return "fail";
            default :
                message = "An error happened. Please, try again";
                model.addAttribute("message", message);
                return "fail";
        }
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute User user, HttpServletRequest req) {
        Optional<User> regUser = userService.add(user);
        if (regUser.isEmpty()) {
            return "redirect:/fail/signup";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", regUser.get());
        return "redirect:/index";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpServletRequest req) {
        System.out.println(user.getName());
        System.out.println(user.getPassword());
        Optional<User> regUser = userService.findUserByNameAndPassword(user.getName(), user.getPassword());
        if (regUser.isEmpty()) {
            return "redirect:/fail/login";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", regUser.get());
        return "redirect:/index";
    }

    private void addUserToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("guest");
        }
        model.addAttribute("user", user);
    }
}
