package com.jason.harmony.audio;

import com.jason.harmony.Bot;
import com.jason.harmony.Harmony;
import com.jason.harmony.PlayStatus;
import com.jason.harmony.playlist.PlaylistLoader.Playlist;
import com.jason.harmony.queue.FairQueue;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import dev.jason.harmony.settings.RepeatMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AudioHandler extends AudioEventAdapter implements AudioSendHandler {
    private final FairQueue<QueuedTrack> queue = new FairQueue<>();
    private final List<AudioTrack> defaultQueue = new LinkedList<>();
    private final Set<String> votes = new HashSet<>();
    private final PlayerManager manager;
    private final AudioPlayer audioPlayer;
    private final long guildId;
    private final String stringGuildId;
    private AudioFrame lastFrame;

    protected AudioHandler(PlayerManager manager, Guild guild, AudioPlayer player) {
        this.manager = manager;
        this.audioPlayer = player;
        this.guildId = guild.getIdLong();
        this.stringGuildId = guild.getId();
    }

    public int addTrackToFront(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else {
            queue.addAt(0, qtrack);
            return 0;
        }
    }

    public int addTrack(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else
            return queue.add(qtrack);
    }

    public void addTrackIfRepeat(AudioTrack track) {
        // Ajouter une piste à la fin de la file d'attente si en mode répétition
        RepeatMode mode = manager.getBot().getSettingsManager().getSettings(guildId).getRepeatMode();
        if (mode != RepeatMode.OFF) {
            queue.add(new QueuedTrack(track.makeClone(), track.getUserData(RequestMetadata.class)));
        }
    }

    public FairQueue<QueuedTrack> getQueue() {
        return queue;
    }

    public void stopAndClear() {
        queue.clear();
        defaultQueue.clear();
        audioPlayer.stopTrack();
        // current = null;

        Guild guild = guild(manager.getBot().getJDA());
        Bot.updatePlayStatus(guild, guild.getSelfMember(), PlayStatus.STOPPED);
    }

    public boolean isMusicPlaying(JDA jda) {
        return guild(jda).getSelfMember().getVoiceState().inAudioChannel() && audioPlayer.getPlayingTrack() != null;
    }

    public Set<String> getVotes() {
        return votes;
    }

    public AudioPlayer getPlayer() {
        return audioPlayer;
    }

    public RequestMetadata getRequestMetadata() {
        if (audioPlayer.getPlayingTrack() == null)
            return RequestMetadata.EMPTY;
        RequestMetadata rm = audioPlayer.getPlayingTrack().getUserData(RequestMetadata.class);
        return rm == null ? RequestMetadata.EMPTY : rm;
    }

    public boolean playFromDefault() {
        if (!defaultQueue.isEmpty()) {
            audioPlayer.playTrack(defaultQueue.remove(0));
            return true;
        }
        Settings settings = manager.getBot().getSettingsManager().getSettings(guildId);
        if (settings == null || settings.getDefaultPlaylist() == null)
            return false;

        Playlist pl = manager.getBot().getPlaylistLoader().getPlaylist(stringGuildId, settings.getDefaultPlaylist());
        if (pl == null || pl.getItems().isEmpty())
            return false;
        pl.loadTracks(manager, (at) -> {
            if (audioPlayer.getPlayingTrack() == null)
                audioPlayer.playTrack(at);
            else
                defaultQueue.add(at);
        }, () -> {
            if (pl.getTracks().isEmpty() && !manager.getBot().getConfig().getStay())
                manager.getBot().closeAudioConnection(guildId);
        });
        return true;
    }

    // Audio Events
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        RepeatMode repeatMode = manager.getBot().getSettingsManager().getSettings(guildId).getRepeatMode();

        // Si la chanson se termine normalement et que le mode de répétition est activé (!OFF), remettez en file d'attente
        if (endReason == AudioTrackEndReason.FINISHED && repeatMode != RepeatMode.OFF) {
            // in RepeatMode.ALL
            if (repeatMode == RepeatMode.ALL) {
                queue.add(new QueuedTrack(track.makeClone(), track.getUserData(RequestMetadata.class)));

                // in RepeatMode.SINGLE
            } else if (repeatMode == RepeatMode.SINGLE) {
                queue.addAt(0, new QueuedTrack(track.makeClone(), track.getUserData(RequestMetadata.class)));
            }
        }

        if (queue.isEmpty()) {
            if (!playFromDefault()) {
                manager.getBot().getNowplayingHandler().onTrackUpdate(guildId, null, this);
                if (!manager.getBot().getConfig().getStay()) manager.getBot().closeAudioConnection(guildId);

                player.setPaused(false);

                Guild guild = guild(manager.getBot().getJDA());
                Bot.updatePlayStatus(guild, guild.getSelfMember(), PlayStatus.STOPPED);
            }
        } else {
            QueuedTrack qt = queue.pull();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        votes.clear();
        manager.getBot().getNowplayingHandler().onTrackUpdate(guildId, track, this);

        Guild guild = guild(manager.getBot().getJDA());
        Bot.updatePlayStatus(guild, guild.getSelfMember(), PlayStatus.PLAYING);
    }


    // Formatting
    public MessageCreateData getNowPlaying(JDA jda) throws Exception {
        if (isMusicPlaying(jda)) {
            Guild guild = guild(jda);
            AudioTrack track = audioPlayer.getPlayingTrack();
            MessageCreateBuilder mb = new MessageCreateBuilder();
            mb.addContent(FormatUtil.filter(manager.getBot().getConfig().getSuccess() + " **" + guild.getSelfMember().getVoiceState().getChannel().getAsMention() + "**で、再生中です..."));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(guild.getSelfMember().getColor());
            RequestMetadata rm = getRequestMetadata();
            if (rm.getOwner() != 0L) {
                User u = guild.getJDA().getUserById(rm.user.id);
                if (u == null)
                    eb.setAuthor(rm.user.username + "#" + rm.user.discrim, null, rm.user.avatar);
                else
                    eb.setAuthor(u.getName() + "#" + u.getDiscriminator(), null, u.getEffectiveAvatarUrl());
            }
            try {
                eb.setTitle(track.getInfo().title, track.getInfo().uri);
            } catch (Exception e) {
                eb.setTitle(track.getInfo().title);
            }
            if (track instanceof YoutubeAudioTrack && manager.getBot().getConfig().useNPImages()) {
                eb.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/maxresdefault.jpg");
            }

            if (track.getInfo().author != null && !track.getInfo().author.isEmpty())
                eb.setFooter("source: " + track.getInfo().author, null);

            double progress = (double) audioPlayer.getPlayingTrack().getPosition() / track.getDuration();
            eb.setDescription((audioPlayer.isPaused() ? Harmony.PAUSE_EMOJI : Harmony.PLAY_EMOJI)
                    + " " + FormatUtil.progressBar(progress)
                    + " `[" + FormatUtil.formatTime(track.getPosition()) + "/" + FormatUtil.formatTime(track.getDuration()) + "]` "
                    + FormatUtil.volumeIcon(audioPlayer.getVolume()));

            return mb.addEmbeds(eb.build()).build();
        } else return null;
    }

    public MessageCreateData getNoMusicPlaying(JDA jda) {
        Guild guild = guild(jda);
        return new MessageCreateBuilder()
                .setContent(FormatUtil.filter(manager.getBot().getConfig().getSuccess() + " **Ne joue pas de musique.**"))
                .setEmbeds(new EmbedBuilder()
                        .setTitle("Ne joue pas de musique.")
                        .setDescription(Harmony.STOP_EMOJI + " " + FormatUtil.progressBar(-1) + " " + FormatUtil.volumeIcon(audioPlayer.getVolume()))
                        .setColor(guild.getSelfMember().getColor())
                        .build())
                .build();
    }

    public String getTopicFormat(JDA jda) {
        if (isMusicPlaying(jda)) {
            long userid = getRequestMetadata().getOwner();
            AudioTrack track = audioPlayer.getPlayingTrack();

            // Vérifiez si Mouv joue
            if (track.getInfo().uri.matches(".*radiofrance.fr/mouv/.*")) {
                return "**Radio Mouv** [" + (userid == 0 ? "lecture automatique" : "<@" + userid + ">") + "]"
                        + "\n" + (audioPlayer.isPaused() ? Harmony.PAUSE_EMOJI : Harmony.PLAY_EMOJI) + " "
                        + "[LIVE] "
                        + FormatUtil.volumeIcon(audioPlayer.getVolume());
            }

            String title = track.getInfo().title;
            if (title == null || title.equals("titre inconnu"))
                title = track.getInfo().uri;
            return "**" + title + "** [" + (userid == 0 ? "lecture automatique" : "<@" + userid + ">") + "]"
                    + "\n" + (audioPlayer.isPaused() ? Harmony.PAUSE_EMOJI : Harmony.PLAY_EMOJI) + " "
                    + "[" + FormatUtil.formatTime(track.getDuration()) + "] "
                    + FormatUtil.volumeIcon(audioPlayer.getVolume());
        } else return "ne joue pas de musique" + Harmony.STOP_EMOJI + " " + FormatUtil.volumeIcon(audioPlayer.getVolume());
    }

    // Audio Send Handler methods
    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }


    // Private methods
    private Guild guild(JDA jda) {
        return jda.getGuildById(guildId);
    }
}
