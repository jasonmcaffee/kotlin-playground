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

-- create indexes for columns we plan on using in our search criteria.
create index idx_cached_response_url on cached_response(url);
create index idx_cached_response_request_method on cached_response(request_method);
-- create index idx_cached_response_request_body on cached_response(request_body);  -- probably not safe anyways in postgres, but h2 blows up with:  Feature not supported: "Index on column: ""REQUEST_BODY"" CHARACTER LARGE OBJECT"; SQL statement: