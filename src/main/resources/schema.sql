create table if not exists users
(
    id int generated by default as identity primary key,
    email varchar(64) not null,
    login varchar(64),
    name varchar(64),
    birthday timestamp
);

create table if not exists mpa
(
    mpa_id int primary key,
    name varchar(64)
);

create table if not exists films
(
    id int generated by default as identity primary key,
    name varchar(64) not null,
    description varchar(120),
    release_date timestamp,
    duration int,
    mpa_id int,
    foreign key (mpa_id) references mpa (mpa_id)
);

create table if not exists likes
(
    film_id int not null,
    user_id int not null,
    primary key (user_id, film_id),
    foreign key (user_id) references users (id),
    foreign key (film_id) references films (id)
);

create table if not exists friends
(
    user_id int not null,
    friend_id int not null,
    is_confirm boolean default false,
    primary key (user_id, friend_id),
    foreign key (user_id) references users (id),
    foreign key (friend_id) references users (id)
);

create table if not exists genres
(
    id int primary key,
    name varchar(64) not null
);

create table if not exists film_genres
(
    film_id int not null,
    genre_id int not null,
    primary key (film_id, genre_id),
    foreign key (film_id) references films (id),
    foreign key (genre_id) references genres (id)
);