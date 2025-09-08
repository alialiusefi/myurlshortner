create table shortened_url_user_access (
    original_url text not null,
    shortened_url text not null,
    browser varchar(32) not null,
    device varchar(32) not null,
    operating_system varchar(32) not null
);
