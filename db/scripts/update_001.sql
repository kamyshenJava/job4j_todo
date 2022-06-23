create table if not exists tasks(
    id serial primary key,
    description text,
    created timestamp,
    done boolean
);