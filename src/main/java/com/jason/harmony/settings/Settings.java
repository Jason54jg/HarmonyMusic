package com.jason.harmony.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import dev.jason.harmony.settings.RepeatMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.Collection;
import java.util.Collections;

public class Settings implements GuildSettingsProvider {
    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    private int volume, announce;
    private String defaultPlaylist;
    private RepeatMode repeatMode;
    private String prefix;
    private boolean bitrateWarningReaded;
    private double skipRatio;


    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, boolean bitrateWarningReaded, int announce, double skipRatio) {
        this.manager = manager;
        try {
            this.textId = Long.parseLong(textId);
        } catch (NumberFormatException e) {
            this.textId = 0;
        }
        try {
            this.voiceId = Long.parseLong(voiceId);
        } catch (NumberFormatException e) {
            this.voiceId = 0;
        }
        try {
            this.roleId = Long.parseLong(roleId);
        } catch (NumberFormatException e) {
            this.roleId = 0;
        }
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.bitrateWarningReaded = bitrateWarningReaded;
        this.announce = announce;
        this.skipRatio = skipRatio;
    }

    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, boolean bitrateWarningReaded, int announce, double skipRatio) {
        this.manager = manager;
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.bitrateWarningReaded = bitrateWarningReaded;
        this.announce = announce;
        this.skipRatio = skipRatio;
    }

    // Getters
    public TextChannel getTextChannel(Guild guild) {
        return guild == null ? null : guild.getTextChannelById(textId);
    }

    public VoiceChannel getVoiceChannel(Guild guild) {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }

    public Role getRole(Guild guild) {
        return guild == null ? null : guild.getRoleById(roleId);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.manager.writeSettings();
    }

    public String getDefaultPlaylist() {
        return defaultPlaylist;
    }

    public void setDefaultPlaylist(String defaultPlaylist) {
        this.defaultPlaylist = defaultPlaylist;
        this.manager.writeSettings();
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
        this.manager.writeSettings();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.manager.writeSettings();
    }

    public double getSkipRatio() {
        return skipRatio;
    }

    public void setSkipRatio(double skipRatio) {
        this.skipRatio = skipRatio;
        this.manager.writeSettings();
    }

    public int getAnnounce() {
        return announce;
    }

    public void setAnnounce(int announce) {
        this.announce = announce;
        this.manager.writeSettings();
    }

    public boolean isBitrateWarningReaded() {
        return bitrateWarningReaded;
    }

    public void setBitrateWarning(boolean readied) {
        this.bitrateWarningReaded = readied;
    }

    @Override
    public Collection<String> getPrefixes() {
        return prefix == null ? Collections.EMPTY_SET : Collections.singleton(prefix);
    }

    // Setters
    public void setTextChannel(TextChannel tc) {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }

    public void setVoiceChannel(AudioChannel vc) {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }

    public void setDJRole(Role role) {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }
}