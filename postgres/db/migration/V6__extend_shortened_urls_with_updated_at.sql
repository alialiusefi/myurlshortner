alter table shortened_urls add column updated_at timestamp with time zone;

update shortened_urls new set updated_at = curr.created_at from shortened_urls curr where new.unique_identifier = curr.unique_identifier;

alter table shortened_urls alter column updated_at set not null;
