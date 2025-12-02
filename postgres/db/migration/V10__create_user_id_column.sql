alter table shortened_urls add column user_id bigint default 1;
alter table shortened_urls alter column user_id set not null;

update shortened_url_events set "event" = "event" || '{"user_id": 1}'::jsonb where record_name = 'USER_CREATED_SHORTENED_URL' or record_name = 'USER_UPDATED_ORIGINAL_URL';

-- update shortened_url_events set "event" = "event" - 'user_id' where record_name = 'USER_CREATED_SHORTENED_URL' or record_name = 'USER_UPDATED_ORIGINAL_URL';

