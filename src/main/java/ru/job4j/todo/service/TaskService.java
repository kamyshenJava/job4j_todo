package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.store.TaskStore;

import java.util.List;

@Service
public class TaskService {
    private final TaskStore taskStore;

    public TaskService(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    public List<Task> findAll() {
        return taskStore.findAll();
    }

    public List<Task> findByParam(Boolean param) {
        return taskStore.findByParam(param);
    }

    public Task findById(int id) {
        return taskStore.findById(id);
    }

    public Task add(Task task) {
        return taskStore.add(task);
    }

    public boolean changeStatus(int id, Boolean done) {
        return taskStore.changeStatus(id, done);
    }

    public boolean delete(int id) {
        return taskStore.delete(id);
    }

    public boolean replace(int id, Task task) {
        return taskStore.replace(id, task);
    }
}
