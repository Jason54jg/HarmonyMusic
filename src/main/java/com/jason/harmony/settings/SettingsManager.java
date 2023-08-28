package com.jason.harmony.settings;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.jason.harmony.utils.OtherUtil;
import dev.jason.harmony.settings.RepeatMode;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class SettingsManager implements GuildSettingsManager {
    private final static double SKIP_RATIO = .55;
    private final HashMap<Long, Settings> settings;

    public SettingsManager() {
        this.settings = new HashMap<>();
        try {
            JSONObject loadedSettings = new JSONObject(new String(Files.readAllBytes(OtherUtil.getPath("serversettings.json"))));
            loadedSettings.keySet().forEach((id) -> {
                JSONObject o = loadedSettings.getJSONObject(id);

                // pour prendre en charge les versions précédentes (booléennes)
                try {
                    if (o.getBoolean("repeat")) {
                        o.put("repeat", RepeatMode.ALL);
                    } else {
                        o.put("repeat", RepeatMode.OFF);
                    }
                    // Afin de changer la valeur numérique pour la bonne car une valeur incorrecte a été saisie en raison d'un bogue
                    if (o.getInt("announce") == 50) {
                        o.put("announce", 0);
                    }
                } catch (JSONException ignored) { /* ignored */ }

                settings.put(Long.parseLong(id), new Settings(this,
                        o.has("text_channel_id") ? o.getString("text_channel_id") : null,
                        o.has("voice_channel_id") ? o.getString("voice_channel_id") : null,
                        o.has("dj_role_id") ? o.getString("dj_role_id") : null,
                        o.has("volume") ? o.getInt("volume") : 10,
                        o.has("default_playlist") ? o.getString("default_playlist") : null,
                        o.has("repeat") ? o.getEnum(RepeatMode.class, "repeat") : RepeatMode.OFF,
                        o.has("prefix") ? o.getString("prefix") : null,
                        o.has("bitrate_warnings_readied") && o.getBoolean("bitrate_warnings_readied"),
                        o.has("announce") ? o.getInt("announce") : 0,
                        o.has("skip_ratio") ? o.getDouble("skip_ratio") : SKIP_RATIO));
            });
        } catch (IOException | JSONException e) {
            LoggerFactory.getLogger("Settings").warn("Impossible de charger les paramètres du serveur (normal s'il n'y a pas encore de paramètres): " + e);
        }
    }

    @Override
    public Settings getSettings(Guild guild) {
        return getSettings(guild.getIdLong());
    }

    public Settings getSettings(long guildId) {
        return settings.computeIfAbsent(guildId, id -> createDefaultSettings());
    }

    private Settings createDefaultSettings() {
        return new Settings(this, 0, 0, 0, 10, null, RepeatMode.OFF, null, false, 0, SKIP_RATIO);
    }

    protected void writeSettings() {
        var obj = new JSONObject();
        for (Long key : settings.keySet()) {
            var o = new JSONObject();
            Settings s = settings.get(key);
            if (s.textId != 0)
                o.put("text_channel_id", Long.toString(s.textId));
            if (s.voiceId != 0)
                o.put("voice_channel_id", Long.toString(s.voiceId));
            if (s.roleId != 0)
                o.put("dj_role_id", Long.toString(s.roleId));
            if (s.getVolume() != 50)
                o.put("volume", s.getVolume());
            if (s.getDefaultPlaylist() != null)
                o.put("default_playlist", s.getDefaultPlaylist());
            if (s.getRepeatMode() != RepeatMode.OFF)
                o.put("repeat", s.getRepeatMode());
            if (s.getPrefix() != null)
                o.put("prefix", s.getPrefix());
            if (s.getAnnounce() != 0)
                o.put("announce", s.getAnnounce());
            if (s.getSkipRatio() != SKIP_RATIO)
                o.put("skip_ratio", s.getSkipRatio());
            obj.put(Long.toString(key), o);
        }
        try {
            Files.write(OtherUtil.getPath("serversettings.json"), obj.toString(4).getBytes());
        } catch (IOException ex) {
            LoggerFactory.getLogger("Settings").warn("Impossible d'écrire dans le fichier： " + ex);
        }
    }
}
