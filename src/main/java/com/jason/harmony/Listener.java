package com.jason.harmony;

import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.utils.OtherUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    private final Bot bot;

    public Listener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        if (event.getJDA().getGuilds().isEmpty()) {
            Logger log = LoggerFactory.getLogger("Harmony");
            log.warn("Ce bot n'est pas dans un groupe! Utilisez le lien ci-dessous pour ajouter le bot à votre groupe.");
            log.warn(event.getJDA().getInviteUrl(Harmony.RECOMMENDED_PERMS));
        }
        event.getJDA().getGuilds().forEach((guild) ->
        {
            try {
                String defpl = Objects.requireNonNull(bot.getSettingsManager().getSettings(guild)).getDefaultPlaylist();
                VoiceChannel vc = Objects.requireNonNull(bot.getSettingsManager().getSettings(guild)).getVoiceChannel(guild);
                if (defpl != null && vc != null && bot.getPlayerManager().setUpHandler(guild).playFromDefault()) {
                    guild.getAudioManager().openAudioConnection(vc);
                }
            } catch (Exception ignore) {
            }
        });
        if (bot.getConfig().useUpdateAlerts()) {
            bot.getThreadpool().scheduleWithFixedDelay(() ->
            {
                User owner = bot.getJDA().getUserById(bot.getConfig().getOwnerId());
                if (owner != null) {
                    String currentVersion = OtherUtil.getCurrentVersion();
                    String latestVersion = OtherUtil.getLatestVersion();
                    if (latestVersion != null && !currentVersion.equalsIgnoreCase(latestVersion) && Harmony.CHECK_UPDATE) {
                        String msg = String.format(OtherUtil.NEW_VERSION_AVAILABLE, currentVersion, latestVersion);
                        owner.openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
                    }
                }
            }, 0, 24, TimeUnit.HOURS);
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        bot.getNowplayingHandler().onMessageDelete(event.getGuild(), event.getMessageIdLong());
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Logger log = LoggerFactory.getLogger("onGuildVoiceUpdate");
        bot.getAloneInVoiceHandler().onVoiceUpdate(event);

        // Événement de sortie
        log.debug("onGuildVoiceLeave Start");
        onGuildVoiceLeave(event);
        log.debug("onGuildVoiceLeave End");
        // L'événement se termine à la sortie

        // événement auquel assister
        log.debug("onGuildVoiceJoin Start");
        onGuildVoiceJoin(event);
        log.debug("onGuildVoiceJoin End");
        // L'événement se termine lorsque vous rejoignez
    }


    public void onGuildVoiceLeave(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() == null) return;

        //NUP = false -> NUS = false -> return
        //NUP = false -> NUS = true -> GO
        //NUP = true -> GO
        if (!bot.getConfig().getNoUserPause())
            if (!bot.getConfig().getNoUserStop()) return;
        Member botMember = event.getGuild().getSelfMember();
        //ボイチャにいる人数が1人、botがボイチャにいるか
        if (event.getChannelLeft().getMembers().size() == 1 && event.getChannelLeft().getMembers().contains(botMember)) {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

            // Si nouserpause dans config.txt est vrai
            if (bot.getConfig().getNoUserPause()) {
                //⏸
                // mettre le lecteur en pause
                Objects.requireNonNull(handler).getPlayer().setPaused(true);

                Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PAUSED);

                return;
            }

            if (bot.getConfig().getNoUserStop()) {
                //⏹
                if (bot.getConfig().getAutoStopQueueSave()) {
                    bot.getCacheLoader().Save(event.getGuild().toString(), handler.getQueue());
                }
                Objects.requireNonNull(handler).stopAndClear();
                event.getGuild().getAudioManager().closeAudioConnection();
            }
        }
    }


    public void onGuildVoiceJoin(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null) return;

        Logger log = LoggerFactory.getLogger("onGuildVoiceJoin");
        if (!bot.getConfig().getResumeJoined()) return;
        //▶
        Member botMember = event.getGuild().getSelfMember();
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        log.debug("Jugement pour redémarrer la lecture {}", ((event.getChannelJoined().getMembers().size() > 1 && event.getChannelJoined().getMembers().contains(botMember)) && Objects.requireNonNull(handler).getPlayer().isPaused()));
        //Au moins 1 personne dans la boicha, le bot est dans la boicha ou la lecture est en pause
        if ((event.getChannelJoined().getMembers().size() > 1 && event.getChannelJoined().getMembers().contains(botMember)) && Objects.requireNonNull(handler).getPlayer().isPaused()) {
            handler.getPlayer().setPaused(false);
            log.debug("La lecture a repris.");

            Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PLAYING);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        bot.shutdown();
    }
}
