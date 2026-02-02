create sequence if not exists incomes_seq;

create table incomes
(
    id          bigint not null default nextval('incomes_seq'),
    amount      numeric(19,2),
    category    varchar(128),
    create_date timestamp with time zone default now() not null,
    constraint income_pk primary key (id)
);

comment on table  incomes is 'Доходы';
comment on column incomes.amount is 'Сумма';
comment on column incomes.category is 'Категория';
comment on column incomes.create_date is 'Дата создания';
