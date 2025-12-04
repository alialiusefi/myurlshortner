create table notifications (
    id bigserial primary key,
    unique_identifier varchar(10) not null,
    user_id bigint not null,
    "type" varchar(64) not null,
    params jsonb not null,
    created_at timestamp with time zone not null,
    read_at timestamp with time zone
);

create index if not exists notifications_user_id_idx on notifications (user_id);
