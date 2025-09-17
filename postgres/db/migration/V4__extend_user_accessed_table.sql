alter table shortened_url_user_access add column unique_identifier varchar(10);

create index shortened_url_user_access_unique_identifier_hash_idx on shortened_url_user_access using hash (unique_identifier);

update shortened_url_user_access us1 
set unique_identifier = substring(us2.shortened_url from strpos(us2.shortened_url, '/goto/') + 6 for 10) 
from shortened_url_user_access us2 where us2.shortened_url = us1.shortened_url;

alter table shortened_url_user_access alter column unique_identifier set not null;
