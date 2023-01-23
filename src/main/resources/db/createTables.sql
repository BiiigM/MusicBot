create table server
(
    id               INT NOT NULL AUTO_INCREMENT,
    guild_id         INT NOT NULL,
    channel_id       INT NOT NULL,
    music_message_id INT NOT NULL,
    news_message_id  INT,
    CONSTRAINT server_pk PRIMARY KEY (id)
);