package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "LASTUPDATE",
        "SERVERS",
        "STATUS",
        "LISTENERS",
        "STREAMS",
        "MODE"
})

public class Serverinfo {

    @JsonProperty("LASTUPDATE")
    private Integer lastupdate;
    @JsonProperty("SERVERS")
    private Integer servers;
    @JsonProperty("STATUS")
    private String status;
    @JsonProperty("LISTENERS")
    private Integer listeners;
    @JsonProperty("STREAMS")
    private Streams streams;
    @JsonProperty("MODE")
    private String mode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("LASTUPDATE")
    public Integer getLastupdate() {
        return lastupdate;
    }

    @JsonProperty("LASTUPDATE")
    public void setLastupdate(Integer lastupdate) {
        this.lastupdate = lastupdate;
    }

    @JsonProperty("SERVERS")
    public Integer getServers() {
        return servers;
    }

    @JsonProperty("SERVERS")
    public void setServers(Integer servers) {
        this.servers = servers;
    }

    @JsonProperty("STATUS")
    public String getStatus() {
        return status;
    }

    @JsonProperty("STATUS")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("LISTENERS")
    public Integer getListeners() {
        return listeners;
    }

    @JsonProperty("LISTENERS")
    public void setListeners(Integer listeners) {
        this.listeners = listeners;
    }

    @JsonProperty("STREAMS")
    public Streams getStreams() {
        return streams;
    }

    @JsonProperty("STREAMS")
    public void setStreams(Streams streams) {
        this.streams = streams;
    }

    @JsonProperty("MODE")
    public String getMode() {
        return mode;
    }

    @JsonProperty("MODE")
    public void setMode(String mode) {
        this.mode = mode;
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
