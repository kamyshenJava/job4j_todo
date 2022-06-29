package ru.job4j.todo.store;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;

@Repository
public class UserStore implements DefaultQuery {

    private final SessionFactory sf;

    public UserStore(SessionFactory sf) {
        this.sf = sf;
    }

    public Optional<User> add(User user) {
        try {
            return tx(session -> {
                int id = (int) session.save(user);
                user.setId(id);
                return Optional.of(user);
            }, sf);
        } catch (HibernateException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findUserByNameAndPassword(String name, String password) {
        return tx(session -> {
            final Query<User> query
                    = session.createQuery("From User u where u.name = :name and u.password = :password");
            query.setParameter("name", name);
            query.setParameter("password", password);
            return query.uniqueResultOptional();
        }, sf);
    }
}
