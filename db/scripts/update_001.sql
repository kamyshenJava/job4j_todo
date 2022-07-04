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

create table if not exists categories(
    id serial primary key,
    name varchar(200) UNIQUE not null
);

create table if not exists tasks_categories(
    id serial primary key,
    task_id int not null references tasks(id),
    categories_id int not null references categories(id)
);

insert into categories(name)
values ('home'), ('work'), ('hobbies'), ('family'), ('friends'), ('other')


