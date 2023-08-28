package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "BITRATE",
        "LISTENERS"
})

public class _2 {

    @JsonProperty("BITRATE")
    private Integer bitrate;
    @JsonProperty("LISTENERS")
    private Integer listeners;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("BITRATE")
    public Integer getBitrate() {
        return bitrate;
    }

    @JsonProperty("BITRATE")
    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    @JsonProperty("LISTENERS")
    public Integer getListeners() {
        return listeners;
    }

    @JsonProperty("LISTENERS")
    public void setListeners(Integer listeners) {
        this.listeners = listeners;
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
