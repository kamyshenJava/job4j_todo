package ru.job4j.todo.store;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;

@Repository
public class TaskStore implements DefaultQuery {

    private final SessionFactory sf;

    public TaskStore(SessionFactory sf) {
        this.sf = sf;
    }

    public List<Task> findAll(int id) {
        return this.tx(session -> {
            final Query query =  session.createQuery("From Task t where t.user.id = :id");
            query.setParameter("id", id);
            return query.list();
        }, sf);
    }

    public List<Task> findByParamAndUserId(Boolean param, int id) {
        return this.tx(session -> {
            final Query query = session.createQuery("From Task t where t.done = :param and t.user.id = :id");
            query.setParameter("param", param);
            query.setParameter("id", id);
            return query.list();
        }, sf);
    }

    public Task findById(int id) {
        return this.tx(session -> {
            final Query query = session.createQuery("From Task t where t.id = :id");
            query.setParameter("id", id);
            return (Task) query.uniqueResult();
        }, sf);
    }

    public Task add(Task task) {
        return this.tx(session -> {
            int id = (int) session.save(task);
            task.setId(id);
            return task;
        }, sf);
    }

    public boolean replace(int id, Task task) {
        return this.tx(session -> {
            int rsl =
                session.createQuery("update Task t set t.description = :newDescription, t.created = :newCreated, "
                                + "t.done = :newDone where t.id = :fId")
                        .setParameter("newDescription", task.getDescription())
                        .setParameter("newCreated", task.getCreated())
                        .setParameter("newDone", task.isDone())
                        .setParameter("fId", id)
                        .executeUpdate();
            return rsl > 0;
        }, sf);
    }

    public boolean delete(int id) {
        return this.tx(session -> {
            int rsl = session.createQuery("delete from Task t where t.id = :fId")
                .setParameter("fId", id)
                .executeUpdate();
            return rsl > 0;
        }, sf);
    }

    public boolean changeStatus(int id, boolean done) {
        return this.tx(session -> {
            int rsl = session.createQuery("update Task t set t.done = :newDone where t.id = :fId")
                        .setParameter("newDone", !done)
                        .setParameter("fId", id)
                        .executeUpdate();
            return rsl > 0;
        }, sf);
    }
}
