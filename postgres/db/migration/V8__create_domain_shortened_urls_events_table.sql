alter table shortened_url_event_store drop column artifact_id;
alter table shortened_url_event_store drop column published_at;
alter table shortened_url_event_store drop column processed_at;

update shortened_url_event_store set version = 1;

alter table shortened_url_event_store rename to shortened_url_events;
