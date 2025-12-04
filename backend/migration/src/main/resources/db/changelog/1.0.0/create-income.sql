create sequence if not exists income_seq;

create table income
(
    id          bigint not null default nextval('income_seq'),
    amount      bigint,
    category    varchar(128),
    create_date timestamp with time zone default now() not null,
    constraint income_pk primary key (id)
);

comment on table  income is 'Доходы';
comment on column income.amount is 'Сумма';
comment on column income.category is 'Категория';
comment on column income.create_date is 'Дата создания';
