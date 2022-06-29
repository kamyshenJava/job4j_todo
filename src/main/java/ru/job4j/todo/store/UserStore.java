package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;
import java.util.function.Function;

@Repository
public class UserStore {

    private final SessionFactory sf;

    public UserStore(SessionFactory sf) {
        this.sf = sf;
    }

    public Optional<User> add(User user) {
        return tx(session -> {
            int id = (int) session.save(user);
            user.setId(id);
            return Optional.ofNullable(user);
        });
    }

    public Optional<User> findUserByNameAndPassword(String name, String password) {
        return tx(session -> {
            final Query<User> query
                    = session.createQuery("From User u where u.name = :name and u.password = :password");
            query.setParameter("name", name);
            query.setParameter("password", password);
            return query.uniqueResultOptional();
        });
    }

    private Optional<User> tx(final Function<Session, Optional<User>> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            Optional<User> rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            return Optional.empty();
        } finally {
            session.close();
        }
    }
}
