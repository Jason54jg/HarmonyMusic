package com.jason.harmony.audio;

import com.jason.harmony.Bot;
import com.jason.harmony.entities.Pair;
import com.jason.harmony.settings.Settings;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NowplayingHandler {
    private final Bot bot;
    private final HashMap<Long, Pair<Long, Long>> lastNP; // guild -> channel,message

    public NowplayingHandler(Bot bot) {
        this.bot = bot;
        this.lastNP = new HashMap<>();
    }

    public void init() {
        if (!bot.getConfig().useNPImages())
            bot.getThreadpool().scheduleWithFixedDelay(this::updateAll, 0, 10, TimeUnit.SECONDS);
    }

    public void setLastNPMessage(Message m) {
        lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getChannel().getIdLong(), m.getIdLong()));
    }

    public void clearLastNPMessage(Guild guild) {
        lastNP.remove(guild.getIdLong());
    }

    private void updateAll() {
        Set<Long> toRemove = new HashSet<>();
        for (long guildId : lastNP.keySet()) {
            Guild guild = bot.getJDA().getGuildById(guildId);
            if (guild == null) {
                toRemove.add(guildId);
                continue;
            }
            Pair<Long, Long> pair = lastNP.get(guildId);
            TextChannel tc = guild.getTextChannelById(pair.getKey());
            if (tc == null) {
                toRemove.add(guildId);
                continue;
            }
            AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
            MessageEditData msg = null;
            try {
                msg = MessageEditData.fromCreateData(Objects.requireNonNull(handler).getNowPlaying(bot.getJDA()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (msg == null) {
                msg = MessageEditData.fromCreateData(handler.getNoMusicPlaying(bot.getJDA()));
                toRemove.add(guildId);
            }
            try {
                tc.editMessageById(pair.getValue(), msg).queue(m -> {
                }, t -> lastNP.remove(guildId));
            } catch (Exception e) {
                toRemove.add(guildId);
            }
        }
        toRemove.forEach(lastNP::remove);
    }

    public void updateTopic(long guildId, AudioHandler handler, boolean wait) {
        Guild guild = bot.getJDA().getGuildById(guildId);
        if (guild == null)
            return;
        Settings settings = bot.getSettingsManager().getSettings(guildId);
        TextChannel tchan = settings.getTextChannel(guild);
        if (tchan != null && guild.getSelfMember().hasPermission(tchan, Permission.MANAGE_CHANNEL)) {
            String otherText;
            String topic = tchan.getTopic();
            if (topic == null || topic.isEmpty())
                otherText = "\u200B";
            else if (topic.contains("\u200B"))
                otherText = topic.substring(topic.lastIndexOf("\u200B"));
            else
                otherText = "\u200B\n " + topic;
            String text = handler.getTopicFormat(bot.getJDA()) + otherText;
            if (!text.equals(tchan.getTopic())) {
                try {
                    tchan.getManager().setTopic(text).complete(wait);
                } catch (PermissionException | RateLimitedException ignore) {
                }
            }
        }
    }

    // "event"-based methods
    public void onTrackUpdate(long guildId, AudioTrack track, AudioHandler handler) {
        // Mettre à jour le statut du bot, le cas échéant
        if (bot.getConfig().getSongInStatus()) {
            if (track != null && bot.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count() <= 1)

                bot.getJDA().getPresence().setActivity(Activity.listening(track.getInfo().title));
            else
                bot.resetGame();
        }

        // Mettez à jour le sujet de la chaîne, le cas échéant
        updateTopic(guildId, handler, false);
    }

    public void onMessageDelete(Guild guild, long messageId) {
        Pair<Long, Long> pair = lastNP.get(guild.getIdLong());
        if (pair == null)
            return;
        if (pair.getValue() == messageId)
            lastNP.remove(guild.getIdLong());
    }
}
