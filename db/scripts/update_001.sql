create table if not exists users(
    id serial primary key,
    name varchar(200) UNIQUE not null,
    password varchar(200) not null
    );

create table if not exists tasks(
    id serial primary key,
    description text,
    created timestamp,
    done boolean,
    user_id int not null references users(id)
);

