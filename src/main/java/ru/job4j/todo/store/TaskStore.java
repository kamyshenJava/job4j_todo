package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.function.Function;

@Repository
public class TaskStore {

    private final SessionFactory sf;

    public TaskStore(SessionFactory sf) {
        this.sf = sf;
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Task> findAll() {
        return this.tx(session -> session.createQuery("From Task").list());
    }

    public List<Task> findByParam(Boolean param) {
        return this.tx(session -> {
            final Query query = session.createQuery("From Task t where t.done = :param");
            query.setParameter("param", param);
            return query.list();
        });
    }

    public Task findById(int id) {
        return this.tx(session -> {
            final Query query = session.createQuery("From Task t where t.id = :id");
            query.setParameter("id", id);
            return (Task) query.uniqueResult();
        });
    }

    public Task add(Task task) {
        return this.tx(session -> {
            int id = (int) session.save(task);
            task.setId(id);
            return task;
        });
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
        });
    }

    public boolean delete(int id) {
        return this.tx(session -> {
            int rsl = session.createQuery("delete from Task t where t.id = :fId")
                .setParameter("fId", id)
                .executeUpdate();
            return rsl > 0;
        });
    }

    public boolean changeStatus(int id, boolean done) {
        return this.tx(session -> {
            int rsl = session.createQuery("update Task t set t.done = :newDone where t.id = :fId")
                        .setParameter("newDone", !done)
                        .setParameter("fId", id)
                        .executeUpdate();
            return rsl > 0;
        });
    }
}
