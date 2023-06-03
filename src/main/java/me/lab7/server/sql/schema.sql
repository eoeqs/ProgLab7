begin;

create table if not exists users (
    u_id bigserial primary key,
    u_name varchar(64) unique not null,
    u_password_digest char(96) not null,
    u_salt varchar(64) not null
);

create table if not exists coordinates (
    c_id bigserial primary key,
    c_x double precision not null,
    c_y double precision not null,
    c_creator_id bigint not null references users(u_id) on delete cascade
);

create table if not exists addresses (
    a_id bigserial primary key,
    a_street text,
    a_zip_code text not null,
    a_creator_id bigint not null references users(u_id) on delete cascade
);

create table if not exists organizations (
    o_id bigserial primary key,
    o_full_name text not null,
    o_annual_turnover int not null constraint positive_annual_turnover check (o_annual_turnover > 0),
    o_employees_count bigint not null constraint positive_employees_count check (o_employees_count > 0),
    o_address_id bigint references addresses(a_id) on delete cascade,
    o_creator_id bigint not null references users(u_id) on delete cascade

);

create table if not exists workers (
    w_id bigserial primary key,
    w_name text not null,
    w_coordinates_id bigint references coordinates(c_id) on delete cascade,
    w_creation_date timestamp default now() not null,
    w_salary int constraint positive_salary check (w_salary > 0),
    w_start_date timestamp not null constraint start_after_creation check (workers.w_start_date >= w_creation_date),
    w_pos text,
    w_status text,
    w_organization_id bigint references organizations(o_id) on delete cascade,
    w_creator_id bigint not null references users(u_id) on delete cascade
);

end;