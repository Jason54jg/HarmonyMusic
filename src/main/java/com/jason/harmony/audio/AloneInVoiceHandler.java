package com.jason.harmony.audio;

import com.jason.harmony.Bot;
import dev.jason.harmony.playlist.CacheLoader;
import dev.jason.harmony.util.LastSendTextChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
public class AloneInVoiceHandler {
    private final Bot bot;
    private final HashMap<Long, Instant> aloneSince = new HashMap<>();
    Logger log = LoggerFactory.getLogger("AloneInVoiceHandler");
    private long aloneTimeUntilStop = 0;

    public AloneInVoiceHandler(Bot bot) {
        this.bot = bot;
    }

    public void init() {
        aloneTimeUntilStop = bot.getConfig().getAloneTimeUntilStop();
        if (aloneTimeUntilStop > 0)
            bot.getThreadpool().scheduleWithFixedDelay(this::check, 0, 5, TimeUnit.SECONDS);
    }

    private void check() {
        Set<Long> toRemove = new HashSet<>();
        for (Map.Entry<Long, Instant> entrySet : aloneSince.entrySet()) {
            if (entrySet.getValue().getEpochSecond() > Instant.now().getEpochSecond() - aloneTimeUntilStop) continue;

            Guild guild = bot.getJDA().getGuildById(entrySet.getKey());

            if (guild == null) {
                toRemove.add(entrySet.getKey());
                continue;
            }
            AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();

            if (bot.getConfig().getAutoStopQueueSave()) {
                // Processus de sauvegarde du cache
                CacheLoader cache = bot.getCacheLoader();
                cache.Save(guild.getId(), handler.getQueue());
                log.info("Enregistrez la file d'attente de lecture et quittez le canal vocal.");
                LastSendTextChannel.SendMessage(guild, ":notes: Vous avez enregistré la file d'attente de lecture et quitté le canal vocal.");
            } else {
                // Traitement en quittant sans enregistrer le cache
                log.info("Supprimez la file d'attente et quittez le canal vocal.");
                LastSendTextChannel.SendMessage(guild, ":notes: J'ai supprimé la file d'attente de lecture et quitté le canal vocal.");
            }

            handler.stopAndClear();
            guild.getAudioManager().closeAudioConnection();

            toRemove.add(entrySet.getKey());
        }
        toRemove.forEach(aloneSince::remove);
    }

    public void onVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (aloneTimeUntilStop <= 0) return;

        Guild guild = event.getEntity().getGuild();
        if (!bot.getPlayerManager().hasHandler(guild)) return;
        // Ne partez pas si vous êtes dans le canal de scène.
        if (guild.getAudioManager().getConnectedChannel() != null) {
            if (guild.getAudioManager().getConnectedChannel().getType() == ChannelType.STAGE) return;
        }

        boolean alone = isAlone(guild);
        boolean inList = aloneSince.containsKey(guild.getIdLong());

        if (!alone && inList)
            aloneSince.remove(guild.getIdLong());
        else if (alone && !inList)
            aloneSince.put(guild.getIdLong(), Instant.now());
    }

    private boolean isAlone(Guild guild) {
        if (guild.getAudioManager().getConnectedChannel() == null) return false;
        return guild.getAudioManager().getConnectedChannel().getMembers().stream()
                .noneMatch(x ->
                        !x.getVoiceState().isDeafened()
                                && !x.getUser().isBot());
    }
}