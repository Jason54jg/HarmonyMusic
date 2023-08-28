package com.jason.harmony.audio;

import com.jason.harmony.Bot;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerManager extends DefaultAudioPlayerManager {
    private final Bot bot;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlayerManager(Bot bot) {
        this.bot = bot;
    }

    public void init() {

        registerSourceManager(new YoutubeAudioSourceManager(
                true,
                bot.getConfig().getYouTubeEmailAddress(),
                bot.getConfig().getYouTubePassword()
        ));

        TransformativeAudioSourceManager.createTransforms(bot.getConfig().getTransforms()).forEach(this::registerSourceManager);
        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
        source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);

        if (getConfiguration().getOpusEncodingQuality() != 10) {
            logger.debug("OpusEncodingQuality est " + getConfiguration().getOpusEncodingQuality() + "(< 10)" + ", définit la qualité sur 10.");
            getConfiguration().setOpusEncodingQuality(10);
        }

        if (getConfiguration().getResamplingQuality() != AudioConfiguration.ResamplingQuality.HIGH) {
            logger.debug("ResamplingQuality est " + getConfiguration().getResamplingQuality().name() + "(pas HIGH), définissez la qualité sur HIGH.");
            getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        }
    }

    public Bot getBot() {
        return bot;
    }

    public boolean hasHandler(Guild guild) {
        return guild.getAudioManager().getSendingHandler() != null;
    }

    public AudioHandler setUpHandler(Guild guild) {
        AudioHandler handler;
        if (guild.getAudioManager().getSendingHandler() == null) {
            AudioPlayer player = createPlayer();
            player.setVolume(bot.getSettingsManager().getSettings(guild).getVolume());
            handler = new AudioHandler(this, guild, player);
            player.addListener(handler);
            guild.getAudioManager().setSendingHandler(handler);
        } else
            handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
        return handler;
    }
}
