package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;

@Repository
public class TaskStore {

    private final SessionFactory sf;

    public TaskStore(SessionFactory sf) {
        this.sf = sf;
    }

    public List<Task> findAll() {
        Session session = sf.openSession();
        session.beginTransaction();
        Query query = session.createQuery("From Task");
        session.getTransaction().commit();
        List<Task> rsl = query.list();
        session.close();
        return rsl;
    }

    public List<Task> findByParam(Boolean param) {
        Session session = sf.openSession();
        session.beginTransaction();
        Query query = session.createQuery("From Task t where t.done = :param");
        query.setParameter("param", param);
        session.getTransaction().commit();
        List<Task> rsl = query.list();
        session.close();
        return rsl;
    }

    public Task findById(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Query query = session.createQuery("From Task t where t.id = :id");
        query.setParameter("id", id);
        session.getTransaction().commit();
        Task rsl = (Task) query.uniqueResult();
        session.close();
        return rsl;
    }

    public Task add(Task task) {
        Session session = sf.openSession();
        session.beginTransaction();
        try {
            int id = (int) session.save(task);
            task.setId(id);
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return task;
    }

    public boolean replace(int id, Task task) {
        Session session = sf.openSession();
        session.beginTransaction();
        int rsl =
                session.createQuery("update Task i set i.description = :newDescription, i.created = :newCreated, "
                                + "i.done = :newDone where i.id = :fId")
                        .setParameter("newDescription", task.getDescription())
                        .setParameter("newCreated", task.getCreated())
                        .setParameter("newDone", task.isDone())
                        .setParameter("fId", id)
                        .executeUpdate();
        System.out.println(rsl);
        session.getTransaction().commit();
        session.close();
        return rsl > 0;
    }

    public boolean delete(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        int rsl = session.createQuery("delete from Task t where t.id = :fId")
                .setParameter("fId", id)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
        return rsl > 0;
    }

    public boolean changeStatus(int id, boolean done) {
        Session session = sf.openSession();
        session.beginTransaction();
        int rsl = session.createQuery("update Task t set t.done = :newDone where t.id = :fId")
                        .setParameter("newDone", !done)
                        .setParameter("fId", id)
                        .executeUpdate();
        session.getTransaction().commit();
        session.close();
        return rsl > 0;
    }
}
