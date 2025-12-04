create sequence if not exists expenses_seq;

create table expenses
(
    id          bigint not null default nextval('expenses_seq'),
    amount      bigint,
    category    varchar(128),
    create_date timestamp with time zone default now() not null,
    constraint  expenses_pk primary key (id)
);

comment on table  expenses is 'Расходы';
comment on column expenses.amount is 'Сумма';
comment on column expenses.category is 'Категория';
comment on column expenses.create_date is 'Дата создания';