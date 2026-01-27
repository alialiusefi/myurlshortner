create table gift_request (
    id bigserial primary key,
    unique_identifier varchar(10) not null,
    source_user_id bigint not null,
    target_user_id bigint not null,
    status varchar(32) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone
);

create unique index gift_request_unique_identifier_awaiting_status on gift_request (unique_identifier, status) where status = 'AWAITING';
