-- Add primary-key integrity constraints and a rating range check.
-- Tables were created without keys; duplicates and NULL keys are removed first so the
-- primary keys can be added safely. No foreign keys are added (integrity remains logical).

-- feed: one row per streamer
delete from feed a using feed b
  where a.ctid < b.ctid and a.streamer_id = b.streamer_id;
delete from feed where streamer_id is null;
alter table feed add constraint feed_pkey primary key (streamer_id);

-- guides: one row per recipient
delete from guides a using guides b
  where a.ctid < b.ctid and a.recipient_id = b.recipient_id;
delete from guides where recipient_id is null;
alter table guides add constraint guides_pkey primary key (recipient_id);

-- news
delete from news a using news b
  where a.ctid < b.ctid and a.id = b.id;
delete from news where id is null;
alter table news add constraint news_pkey primary key (id);

-- advices
delete from advices a using advices b
  where a.ctid < b.ctid and a.id = b.id;
delete from advices where id is null;
alter table advices add constraint advices_pkey primary key (id);

-- feedback
delete from feedback a using feedback b
  where a.ctid < b.ctid and a.id = b.id;
delete from feedback where id is null;
alter table feedback add constraint feedback_pkey primary key (id);

alter table feedback
  add constraint feedback_rating_check check (rating between -1 and 10);
