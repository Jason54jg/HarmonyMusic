package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "CIRCLELINK",
        "ALBUMART",
        "CIRCLEART",
        "OFFSET",
        "OFFSETTIME"
})

public class Misc {

    @JsonProperty("CIRCLELINK")
    private String circlelink;
    @JsonProperty("ALBUMART")
    private String albumart;
    @JsonProperty("CIRCLEART")
    private String circleart;
    @JsonProperty("OFFSET")
    private String offset;
    @JsonProperty("OFFSETTIME")
    private Integer offsettime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CIRCLELINK")
    public String getCirclelink() {
        return circlelink;
    }

    @JsonProperty("CIRCLELINK")
    public void setCirclelink(String circlelink) {
        this.circlelink = circlelink;
    }

    @JsonProperty("ALBUMART")
    public String getAlbumart() {
        return albumart;
    }

    @JsonProperty("ALBUMART")
    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }

    @JsonProperty("CIRCLEART")
    public String getCircleart() {
        return circleart;
    }

    @JsonProperty("CIRCLEART")
    public void setCircleart(String circleart) {
        this.circleart = circleart;
    }

    @JsonProperty("OFFSET")
    public String getOffset() {
        return offset;
    }

    @JsonProperty("OFFSET")
    public void setOffset(String offset) {
        this.offset = offset;
    }

    @JsonProperty("OFFSETTIME")
    public Integer getOffsettime() {
        return offsettime;
    }

    @JsonProperty("OFFSETTIME")
    public void setOffsettime(Integer offsettime) {
        this.offsettime = offsettime;
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
