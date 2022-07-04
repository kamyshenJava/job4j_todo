package ru.job4j.todo.store;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.List;

@Repository
public class CategoryStore implements DefaultQuery {
    private final SessionFactory sf;

    public CategoryStore(SessionFactory sf) {
        this.sf = sf;
    }

    public List<Category> getAllCategories() {
        return this.tx(session -> {
            final Query query = session.createQuery("From Category");
            return query.list();
        }, sf);
    }
}
