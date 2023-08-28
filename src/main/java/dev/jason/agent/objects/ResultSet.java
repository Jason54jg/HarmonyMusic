package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SERVERINFO",
        "SONGINFO",
        "SONGTIMES",
        "SONGDATA",
        "MISC"
})

public class ResultSet {

    @JsonProperty("SERVERINFO")
    private Serverinfo serverinfo;
    @JsonProperty("SONGINFO")
    private Songinfo songinfo;
    @JsonProperty("SONGTIMES")
    private Songtimes songtimes;
    @JsonProperty("SONGDATA")
    private Songdata songdata;
    @JsonProperty("MISC")
    private Misc misc;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SERVERINFO")
    public Serverinfo getServerinfo() {
        return serverinfo;
    }

    @JsonProperty("SERVERINFO")
    public void setServerinfo(Serverinfo serverinfo) {
        this.serverinfo = serverinfo;
    }

    @JsonProperty("SONGINFO")
    public Songinfo getSonginfo() {
        return songinfo;
    }

    @JsonProperty("SONGINFO")
    public void setSonginfo(Songinfo songinfo) {
        this.songinfo = songinfo;
    }

    @JsonProperty("SONGTIMES")
    public Songtimes getSongtimes() {
        return songtimes;
    }

    @JsonProperty("SONGTIMES")
    public void setSongtimes(Songtimes songtimes) {
        this.songtimes = songtimes;
    }

    @JsonProperty("SONGDATA")
    public Songdata getSongdata() {
        return songdata;
    }

    @JsonProperty("SONGDATA")
    public void setSongdata(Songdata songdata) {
        this.songdata = songdata;
    }

    @JsonProperty("MISC")
    public Misc getMisc() {
        return misc;
    }

    @JsonProperty("MISC")
    public void setMisc(Misc misc) {
        this.misc = misc;
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
