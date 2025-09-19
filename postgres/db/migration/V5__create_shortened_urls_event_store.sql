create table if not exists shortened_url_event_store (
    event_id uuid primary key,
    artifact_id text not null,
    record_name text not null,
    version integer not null,
    unique_identifier varchar(10) not null,
    event jsonb not null,
    event_date_time timestamp with time zone not null, -- this is the events business logic date time
    published_at timestamp with time zone not null,
    processed_at timestamp with time zone not null
);

create index if not exists shortened_url_event_store_event_id_idx on shortened_url_event_store using hash (event_id);
create index if not exists shortened_url_event_store_unique_identifier_idx on shortened_url_event_store using hash (unique_identifier);
create index if not exists shortened_url_event_store_event_date_time_idx on shortened_url_event_store (event_date_time);

-- drop index if exists shortened_url_event_store_event_id_idx;
-- drop index if exists shortened_url_event_store_unique_identifier_idx;
-- drop index if exists shortened_url_event_store_event_date_time_idx;
-- drop table if exists shortened_url_event_store;
