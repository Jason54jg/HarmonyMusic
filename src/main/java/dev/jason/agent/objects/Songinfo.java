package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "TITLE",
        "ARTIST",
        "ALBUM",
        "YEAR",
        "CIRCLE"
})
public class Songinfo {

    @JsonProperty("TITLE")
    private String title;
    @JsonProperty("ARTIST")
    private String artist;
    @JsonProperty("ALBUM")
    private String album;
    @JsonProperty("YEAR")
    private String year;
    @JsonProperty("CIRCLE")
    private String circle;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("TITLE")
    public String getTitle() {
        return title;
    }

    @JsonProperty("TITLE")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("ARTIST")
    public String getArtist() {
        return artist;
    }

    @JsonProperty("ARTIST")
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @JsonProperty("ALBUM")
    public String getAlbum() {
        return album;
    }

    @JsonProperty("ALBUM")
    public void setAlbum(String album) {
        this.album = album;
    }

    @JsonProperty("YEAR")
    public String getYear() {
        return year;
    }

    @JsonProperty("YEAR")
    public void setYear(String year) {
        this.year = year;
    }

    @JsonProperty("CIRCLE")
    public String getCircle() {
        return circle;
    }

    @JsonProperty("CIRCLE")
    public void setCircle(String circle) {
        this.circle = circle;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
