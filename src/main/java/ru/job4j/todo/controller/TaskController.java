package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;

    public TaskController(TaskService taskService, CategoryService categoryService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @GetMapping("/index")
    public String index(Model model, HttpSession session,
                        @RequestParam(name = "select", required = false, defaultValue = "") String select) {
        addUserToModel(model, session);
        List<Task> tasks = new ArrayList<>();
        if (!"guest".equals(((User) (model.getAttribute("user"))).getName())) {
            int id = ((User) session.getAttribute("user")).getId();
            switch (select) {
                case "done" : tasks = taskService.findByParamAndUserId(true, id);
                    break;
                case "new" : tasks = taskService.findByParamAndUserId(false, id);
                    break;
                default: tasks = taskService.findAll(id);
            }
        } else {
            List<Category> first = List.of(new Category("home"), new Category("friends"));
            List<Category> second = List.of(new Category("work"), new Category("other"));
            Task task1 = Task.of("This is an example of a task", first);
            Task task2 = Task.of("Sign up to be able to add your own tasks", second);
            tasks.add(task1);
            tasks.add(task2);
        }
        model.addAttribute("tasks", tasks);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("task", new Task());
        return "index";
    }

    @PostMapping("/add")
    public String add(HttpSession session, @ModelAttribute Task task) {
        User user = (User) session.getAttribute("user");
        task.setUser(user);
        task.setCreated(LocalDateTime.now());
        taskService.add(task);
        return "redirect:/index";
    }

    @GetMapping("/task/{id}")
    public String task(Model model, HttpSession session, @PathVariable("id") int id) {
        addUserToModel(model, session);
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("newTask", new Task());
        return "task";
    }

    @PostMapping("/status")
    public String changeStatus(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        boolean done = Boolean.parseBoolean(req.getParameter("done"));
        taskService.changeStatus(id, done);
        return String.format("redirect:/task/%d", id);
    }

    @PostMapping("/remove")
    public String remove(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        taskService.delete(id);
        return "redirect:/index";
    }

    @PostMapping("/edit")
    public String edit(HttpSession session, HttpServletRequest req, @ModelAttribute Task task) {
        User user = (User) session.getAttribute("user");
        LocalDateTime created = "on".equals(req.getParameter("updatecreated"))
                ? LocalDateTime.now() : LocalDateTime.parse(req.getParameter("created1"));
        task.setCreated(created);
        task.setUser(user);
        taskService.replace(task);
        return String.format("redirect:/task/%d", task.getId());
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
