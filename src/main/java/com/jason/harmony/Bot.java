package com.jason.harmony;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jason.harmony.audio.AloneInVoiceHandler;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.NowplayingHandler;
import com.jason.harmony.audio.PlayerManager;
import com.jason.harmony.gui.GUI;
import com.jason.harmony.playlist.PlaylistLoader;
import com.jason.harmony.settings.SettingsManager;
import dev.jason.harmony.playlist.CacheLoader;
import dev.jason.harmony.playlist.MylistLoader;
import dev.jason.harmony.playlist.PubliclistLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    public static Bot INSTANCE;
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final BotConfig config;
    private final SettingsManager settings;
    private final PlayerManager players;
    private final PlaylistLoader playlists;
    private final MylistLoader mylists;
    private final PubliclistLoader publist;
    private final CacheLoader cache;
    private final NowplayingHandler nowplaying;
    private final AloneInVoiceHandler aloneInVoiceHandler;

    private boolean shuttingDown = false;
    private JDA jda;
    private GUI gui;

    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings) {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.playlists = new PlaylistLoader(config);
        this.mylists = new MylistLoader(config);
        this.publist = new PubliclistLoader(config);
        this.cache = new CacheLoader(config);
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
        this.players = new PlayerManager(this);
        this.players.init();
        this.nowplaying = new NowplayingHandler(this);
        this.nowplaying.init();
        this.aloneInVoiceHandler = new AloneInVoiceHandler(this);
        this.aloneInVoiceHandler.init();
    }

    public static void updatePlayStatus(Guild guild, Member selfMember, PlayStatus status) {
        if (!INSTANCE.getConfig().getChangeNickName()) return;
        if (!selfMember.hasPermission(Permission.NICKNAME_CHANGE)) {
            LoggerFactory.getLogger("UpdName").error("Échec de la modification du pseudo: privilèges insuffisants。");
            return;
        }

        String name = selfMember.getEffectiveName().replaceAll("[⏯⏸⏹] ", "");
        switch (status) {
            case PLAYING:
                name = "⏯ " + name;
                break;
            case PAUSED:
                name = "⏸ " + name;
                break;
            case STOPPED:
                name = "⏹ " + name;
                break;
            default:
        }

        guild.modifyNickname(selfMember, name).queue();
    }

    public BotConfig getConfig() {
        return config;
    }

    public SettingsManager getSettingsManager() {
        return settings;
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public ScheduledExecutorService getThreadpool() {
        return threadpool;
    }

    public PlayerManager getPlayerManager() {
        return players;
    }

    public PlaylistLoader getPlaylistLoader() {
        return playlists;
    }

    public MylistLoader getMylistLoader() {
        return mylists;
    }

    public PubliclistLoader getPublistLoader() {
        return publist;
    }

    public CacheLoader getCacheLoader() {
        return cache;
    }

    public NowplayingHandler getNowplayingHandler() {
        return nowplaying;
    }

    public AloneInVoiceHandler getAloneInVoiceHandler() {
        return aloneInVoiceHandler;
    }

    public JDA getJDA() {
        return jda;
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void closeAudioConnection(long guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public void resetGame() {
        Activity game = config.getGame() == null || config.getGame().getName().toLowerCase().matches("(none|なし)") ? null : config.getGame();
        if (!Objects.equals(jda.getPresence().getActivity(), game))
            jda.getPresence().setActivity(game);
    }

    public void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN) {
            jda.getGuilds().forEach(g ->
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler) g.getAudioManager().getSendingHandler();
                if (ah != null) {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                    nowplaying.updateTopic(g.getIdLong(), ah, true);
                }
            });
            jda.shutdown();
        }
        if (gui != null)
            gui.dispose();
        System.exit(0);
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }
}
