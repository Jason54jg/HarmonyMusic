package com.jason.harmony.audio;

import com.jason.harmony.queue.Queueable;
import com.jason.harmony.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.User;

public class QueuedTrack implements Queueable {
    private final AudioTrack track;

    public QueuedTrack(AudioTrack track, User owner) {
        this(track, new RequestMetadata(owner));
    }

    public QueuedTrack(AudioTrack track, RequestMetadata rm) {
        this.track = track;
        this.track.setUserData(rm);
    }

    @Override
    public long getIdentifier() {
        return track.getUserData(RequestMetadata.class).getOwner();
    }

    public AudioTrack getTrack() {
        return track;
    }

    @Override
    public String toString() {

        String entry = "`[" + FormatUtil.formatTime(track.getDuration()) + "]` ";
        AudioTrackInfo trackInfo = track.getInfo();
        entry = entry + (trackInfo.uri.startsWith("http") ? "[**" + trackInfo.title + "**](" + trackInfo.uri + ")" : "**" + trackInfo.title + "**");
        return entry + " - <@" + track.getUserData(RequestMetadata.class).getOwner() + ">";
    }
}
