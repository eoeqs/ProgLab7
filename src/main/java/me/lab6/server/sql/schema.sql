begin;

create type status as enum (
    'FIRED', 'HIRED', 'RECOMMENDED_FOR_PROMOTION', 'REGULAR'
);

create type pos as enum (
    'HEAD_OF_DEPARTMENT', 'DEVELOPER', 'MANAGER_OF_CLEANING'
);

create table if not exists users (
    id serial primary key,
    name varchar(64) unique not null,
    password_digest varchar(64) not null,
    salt varchar(64) not null
);

create table if not exists coordinates (
    id serial primary key,
    x double precision,
    y double precision not null,
    сreator_id int not null references users(id) on delete cascade
);

create table if not exists addresses (
    id serial primary key,
    street varchar(64),
    zip_code varchar(64) not null,
    creator_id int not null references users(id) on delete cascade
);

create table if not exists organizations (
    id serial primary key,
    full_name varchar(64) not null,
    annual_turnover int not null constraint positive_annual_turnover check (annual_turnover > 0),
    employees_count bigint not null constraint positive_employees_count check (employees_count > 0),
    address_id int unique references addresses(id) on delete cascade
);

create table if not exists workers (
    id serial primary key,
    name varchar(64) not null,
    coordinates_id int unique references coordinates(id) on delete cascade,
    creation_date timestamp default now() not null,
    salary integer constraint positive_salary CHECK (salary > 0),
    start_date timestamp not null,
    pos pos,
    status status,
    creator_id int not null references users(id) on delete cascade
);

end;