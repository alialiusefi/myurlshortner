create table shortened_urls_indexed (
	unique_identifier varchar(10) not null unique,
	title tsvector
);

create index shortened_urls_indexed_title_index on shortened_urls_indexed using gin (title);

merge into shortened_urls_indexed using shortened_urls
	on shortened_urls_indexed.unique_identifier = shortened_urls.unique_identifier
when matched then
	update set title = to_tsvector(shortened_urls.title)
when not matched then
	insert (unique_identifier, title) values (shortened_urls.unique_identifier, to_tsvector(shortened_urls.title));
