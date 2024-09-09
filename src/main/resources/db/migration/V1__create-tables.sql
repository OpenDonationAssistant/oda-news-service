create table news (
  id varchar(255),
  title varchar(255),
  description text,
  date varchar(255),
  demo_url varchar(255)
);

create table feed (
  streamer_id varchar(255),
  last_read_news_id varchar(255)
)
