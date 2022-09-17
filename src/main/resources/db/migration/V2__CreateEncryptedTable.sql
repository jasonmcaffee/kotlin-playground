
create table if not exists encrypted_column(
    id serial primary key,
    access_key varchar not null,
    description varchar not null
);