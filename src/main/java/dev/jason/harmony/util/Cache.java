package dev.jason.harmony.util;

public class Cache {
    private String title;
    private String author;
    private String length;
    private String identifier;
    private Boolean isStream;
    private String url;
    private String userId;

    public Cache(String title, String author, long length, String identifier, boolean isStream, String uri, long userId) {
        this.title = title;
        this.author = author;
        this.length = String.valueOf(length);
        this.identifier = identifier;
        this.isStream = isStream;
        this.url = uri;
        this.userId = String.valueOf(userId);
    }

    public Cache() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getIsStream() {
        return isStream;
    }

    public void setIsStream(Boolean isStream) {
        this.isStream = isStream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
