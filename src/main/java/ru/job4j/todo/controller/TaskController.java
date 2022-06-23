package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpServletRequest;
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
    public String index(Model model,
                        @RequestParam(name = "select", required = false, defaultValue = "") String select) {
        List<Task> tasks;
        switch (select) {
            case "done" : tasks = taskService.findByParam(true);
            break;
            case "new" : tasks = taskService.findByParam(false);
            break;
            default: tasks = taskService.findAll();
        }
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @PostMapping("/add")
    public String add(HttpServletRequest req) {
        String description = req.getParameter("newTask");
        if ("".equals(description)) {
            return "redirect:/index";
        }
        Task task = Task.of(description);
        taskService.add(task);
        return "redirect:/index";
    }

    @GetMapping("/task/{id}")
    public String task(Model model, @PathVariable("id") int id) {
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
    public String edit(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        String description = req.getParameter("description");
        boolean done = "on".equals(req.getParameter("done"));
        LocalDateTime created = "on".equals(req.getParameter("updatecreated"))
                ? LocalDateTime.now() : LocalDateTime.parse(req.getParameter("created"));
        Task newTask = new Task(description, created, done);
        taskService.replace(id, newTask);
        return String.format("redirect:/task/%d", id);
    }
}
