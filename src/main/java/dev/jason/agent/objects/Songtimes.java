package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "DURATION",
        "PLAYED",
        "REMAINING",
        "SONGSTART",
        "SONGEND"
})

public class Songtimes {

    @JsonProperty("DURATION")
    private Integer duration;
    @JsonProperty("PLAYED")
    private Integer played;
    @JsonProperty("REMAINING")
    private Integer remaining;
    @JsonProperty("SONGSTART")
    private Integer songstart;
    @JsonProperty("SONGEND")
    private Integer songend;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("DURATION")
    public Integer getDuration() {
        return duration;
    }

    @JsonProperty("DURATION")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @JsonProperty("PLAYED")
    public Integer getPlayed() {
        return played;
    }

    @JsonProperty("PLAYED")
    public void setPlayed(Integer played) {
        this.played = played;
    }

    @JsonProperty("REMAINING")
    public Integer getRemaining() {
        return remaining;
    }

    @JsonProperty("REMAINING")
    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    @JsonProperty("SONGSTART")
    public Integer getSongstart() {
        return songstart;
    }

    @JsonProperty("SONGSTART")
    public void setSongstart(Integer songstart) {
        this.songstart = songstart;
    }

    @JsonProperty("SONGEND")
    public Integer getSongend() {
        return songend;
    }

    @JsonProperty("SONGEND")
    public void setSongend(Integer songend) {
        this.songend = songend;
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
