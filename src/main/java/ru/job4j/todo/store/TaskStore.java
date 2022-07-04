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
            final Query query =  session.createQuery("select distinct t from Task t left join fetch t.categories "
                    + "where t.user.id = :id order by t.created");
            query.setParameter("id", id);
            return query.list();
        }, sf);
    }

    public List<Task> findByParamAndUserId(Boolean param, int id) {
        return this.tx(session -> {
            final Query query = session.createQuery("select distinct t From Task t left join fetch t.categories "
                    + "where t.done = :param and t.user.id = :id order by t.created");
            query.setParameter("param", param);
            query.setParameter("id", id);
            return query.list();
        }, sf);
    }

    public Task findById(int id) {
        return this.tx(session -> {
            final Query query = session.createQuery("select distinct t From Task t left join fetch t.categories "
                    + "where t.id = :id");
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

    public void replace(Task task) {
        this.tx(session -> {
            session.update(task);
            return null;
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
