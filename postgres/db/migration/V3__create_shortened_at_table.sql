create table shortened_urls (
    unique_identifier varchar(10) primary key,
    original_url text not null,
    created_at timestamp with time zone
);
