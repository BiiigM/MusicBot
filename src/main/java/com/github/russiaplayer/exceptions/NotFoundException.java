package com.github.russiaplayer.exceptions;

public class NotFoundException extends RuntimeException {
    private String friendlyMessage;

    public NotFoundException(String message, String friendlyMessage) {
        super(message);
        this.friendlyMessage = friendlyMessage;
    }

    public NotFoundException(String friendlyMessage, String format, Object... args) {
        super(String.format(format, args));
        this.friendlyMessage = friendlyMessage;
    }

    public String getFriendlyMessage() {
        return friendlyMessage;
    }

    public void setFriendlyMessage(String friendlyMessage) {
        this.friendlyMessage = friendlyMessage;
    }
}
