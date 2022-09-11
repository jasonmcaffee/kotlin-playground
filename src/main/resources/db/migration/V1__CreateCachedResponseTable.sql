-- create schema if not exists proxy;
-- set schema 'proxy';

-- drop table cached_response;
create table if not exists cached_response(
    id serial primary key,
    url varchar not null,
    response_status_code integer not null,
    response_body text not null,
    response_headers jsonb,
    request_method varchar,
    request_body text,
    request_headers jsonb
);