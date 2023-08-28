package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "BITRATE",
        "LISTENERS"
})

public class _4 {

    @JsonProperty("BITRATE")
    private Object bitrate;
    @JsonProperty("LISTENERS")
    private Object listeners;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("BITRATE")
    public Object getBitrate() {
        return bitrate;
    }

    @JsonProperty("BITRATE")
    public void setBitrate(Object bitrate) {
        this.bitrate = bitrate;
    }

    @JsonProperty("LISTENERS")
    public Object getListeners() {
        return listeners;
    }

    @JsonProperty("LISTENERS")
    public void setListeners(Object listeners) {
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
