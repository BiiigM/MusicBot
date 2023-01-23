package com.github.russiaplayer.Entity;

public class Server {
    private Long guildId;
    private Long channelId;
    private Long musicMessageId;
    private Long newsMessageId;

    public Server() {
        this.guildId = 0L;
        this.channelId = 0L;
        this.musicMessageId = 0L;
        this.newsMessageId = 0L;
    }

    public Server(Long guildId, Long channelId, Long musicMessageId, Long newsMessageId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.musicMessageId = musicMessageId;
        this.newsMessageId = newsMessageId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getMusicMessageId() {
        return musicMessageId;
    }

    public void setMusicMessageId(Long musicMessageId) {
        this.musicMessageId = musicMessageId;
    }

    public Long getNewsMessageId() {
        return newsMessageId;
    }

    public void setNewsMessageId(Long newsMessageId) {
        this.newsMessageId = newsMessageId;
    }
}
