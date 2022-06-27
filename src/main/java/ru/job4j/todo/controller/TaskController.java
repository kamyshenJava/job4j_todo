package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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
            Task task1 = Task.of("This is an example of a task", new User());
            Task task2 = Task.of("Sign up to be able to add your own tasks", new User());
            tasks.add(task1);
            tasks.add(task2);
        }
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @PostMapping("/add")
    public String add(HttpServletRequest req, HttpSession session) {
        String description = req.getParameter("newTask");
        User user = (User) session.getAttribute("user");
        Task task = Task.of(description, user);
        taskService.add(task);
        return "redirect:/index";
    }

    @GetMapping("/task/{id}")
    public String task(Model model, HttpSession session, @PathVariable("id") int id) {
        addUserToModel(model, session);
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
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
    public String edit(HttpServletRequest req, Model model) {
        User user = (User) model.getAttribute("user");
        int id = Integer.parseInt(req.getParameter("id"));
        String description = req.getParameter("description");
        boolean done = "on".equals(req.getParameter("done"));
        LocalDateTime created = "on".equals(req.getParameter("updatecreated"))
                ? LocalDateTime.now() : LocalDateTime.parse(req.getParameter("created"));
        Task newTask = new Task(description, created, done, user);
        taskService.replace(id, newTask);
        return String.format("redirect:/task/%d", id);
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
