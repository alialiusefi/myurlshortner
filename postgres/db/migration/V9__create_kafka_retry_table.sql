create table if not exists kafka_retry_table (
    id bigserial primary key,
    "key" varchar(32) not null,
    event_type varchar(32) not null,
    event_date_time timestamp with time zone not null,
    "version" integer not null,
    "event" jsonb not null,
    topic varchar(64) not null,
    retry_count integer not null
);
