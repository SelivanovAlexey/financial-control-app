create sequence if not exists users_seq;

create table users
(
    id               bigint not null default nextval('users_seq'),
    username         varchar(128),
    password_hash    varchar(128),
    constraint  users_pk primary key (id)
);

comment on table  users is 'Пользователи';
comment on column users.username is 'Имя пользователя';
comment on column users.password_hash is 'Хешированный пароль';