package dev.jason.agent.objects;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "1",
        "2",
        "3",
        "4"
})
public class Streams {

    @JsonProperty("1")
    private dev.jason.agent.objects._1 _1;
    @JsonProperty("2")
    private dev.jason.agent.objects._2 _2;
    @JsonProperty("3")
    private dev.jason.agent.objects._3 _3;
    @JsonProperty("4")
    private dev.jason.agent.objects._4 _4;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("1")
    public dev.jason.agent.objects._1 get1() {
        return _1;
    }

    @JsonProperty("1")
    public void set1(dev.jason.agent.objects._1 _1) {
        this._1 = _1;
    }

    @JsonProperty("2")
    public dev.jason.agent.objects._2 get2() {
        return _2;
    }

    @JsonProperty("2")
    public void set2(dev.jason.agent.objects._2 _2) {
        this._2 = _2;
    }

    @JsonProperty("3")
    public dev.jason.agent.objects._3 get3() {
        return _3;
    }

    @JsonProperty("3")
    public void set3(dev.jason.agent.objects._3 _3) {
        this._3 = _3;
    }

    @JsonProperty("4")
    public dev.jason.agent.objects._4 get4() {
        return _4;
    }

    @JsonProperty("4")
    public void set4(dev.jason.agent.objects._4 _4) {
        this._4 = _4;
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
