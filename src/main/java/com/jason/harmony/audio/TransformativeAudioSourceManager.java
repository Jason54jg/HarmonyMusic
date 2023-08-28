package com.jason.harmony.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.typesafe.config.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class TransformativeAudioSourceManager extends YoutubeAudioSourceManager {
    private final static Logger log = LoggerFactory.getLogger(TransformativeAudioSourceManager.class);
    private final String name, regex, replacement, selector, format;

    public TransformativeAudioSourceManager(String name, Config object) {
        this(name, object.getString("regex"), object.getString("replacement"), object.getString("selector"), object.getString("format"));
    }

    public TransformativeAudioSourceManager(String name, String regex, String replacement, String selector, String format) {
        this.name = name;
        this.regex = regex;
        this.replacement = replacement;
        this.selector = selector;
        this.format = format;
    }

    public static List<TransformativeAudioSourceManager> createTransforms(Config transforms) {
        try {
            return transforms.root().entrySet().stream()
                    .map(e -> new TransformativeAudioSourceManager(e.getKey(), transforms.getConfig(e.getKey())))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Invalid transform ", ex);
            return Collections.emptyList();
        }
    }

    @Override
    public String getSourceName() {
        return name;
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager apm, AudioReference ar) {
        if (ar.identifier == null || !ar.identifier.matches(regex))
            return null;
        try {
            String url = ar.identifier.replaceAll(regex, replacement);
            Document doc = Jsoup.connect(url).get();
            String value = doc.selectFirst(selector).ownText();
            String formattedValue = String.format(format, value);
            return super.loadItem(apm, new AudioReference(formattedValue, null));
        } catch (PatternSyntaxException ex) {
            log.info(String.format("Syntaxe de modèle non valide '%s' pour la source '%s'", regex, name));
        } catch (IOException ex) {
            log.warn(String.format("Impossible de résoudre l'URL de la source '%s'： ", name), ex);
        } catch (Exception ex) {
            log.warn(String.format("Exception dans la source '%s'", name), ex);
        }
        return null;
    }
}
