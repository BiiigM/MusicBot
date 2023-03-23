create table server
(
    guild_id         BIGINT NOT NULL,
    channel_id       BIGINT NOT NULL,
    music_message_id BIGINT NOT NULL,
    news_message_id  BIGINT,
    CONSTRAINT server_pk PRIMARY KEY (guild_id)
);