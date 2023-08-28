package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jason.harmony.Bot;
import com.jason.harmony.PlayStatus;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.QueuedTrack;
import com.jason.harmony.playlist.PlaylistLoader.Playlist;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.jason.harmony.playlist.CacheLoader;
import dev.jason.harmony.playlist.MylistLoader;
import dev.jason.harmony.playlist.PubliclistLoader;
import dev.jason.harmony.slashcommands.DJCommand;
import dev.jason.harmony.slashcommands.MusicCommand;
import dev.jason.harmony.util.Cache;
import dev.jason.harmony.util.StackTraceUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayCmd extends MusicCommand {
    private final static String LOAD = "\uD83D\uDCE5"; // 📥
    private final static String CANCEL = "\uD83D\uDEAB"; // 🚫

    private final String loadingEmoji;

    public PlayCmd(Bot bot) {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "play";
        this.arguments = "<titre|URL|sous-commande>";
        this.help = "jouer la chanson spécifiée";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
        this.children = new SlashCommand[]{new PlaylistCmd(bot), new MylistCmd(bot), new PublistCmd(bot), new RequestCmd(bot)};
    }

    @Override
    public void doCommand(CommandEvent event) {



        if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (handler.getPlayer().getPlayingTrack() != null && handler.getPlayer().isPaused()) {
                if (DJCommand.checkDJPermission(event)) {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess("**" + handler.getPlayer().getPlayingTrack().getInfo().title + "Reprise de la lecture **.");

                    Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PLAYING);
                } else
                    event.replyError("Seul le DJ peut reprendre la lecture !");
                return;
            }

            // Mécanisme de chargement du cache
            if (bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
                List<Cache> data = bot.getCacheLoader().GetCache(event.getGuild().getId());

                AtomicInteger count = new AtomicInteger();
                CacheLoader.CacheResult cache = bot.getCacheLoader().ConvertCache(data);
                event.getChannel().sendMessage(":calling: Chargement des fichiers de cache... (" + cache.getItems().size() + " chansons)").queue(m -> {
                    cache.loadTracks(bot.getPlayerManager(), (at) -> {
                        handler.addTrack(new QueuedTrack(at, (User) User.fromId(data.get(count.get()).getUserId())));
                        count.getAndIncrement();
                    }, () -> {
                        StringBuilder builder = new StringBuilder(cache.getTracks().isEmpty()
                                ? event.getClient().getWarning() + " Aucune chanson chargée."
                                : event.getClient().getSuccess() + " Chargé " + "**" + cache.getTracks().size() + "** chansons depuis le fichier cache.");
                        if (!cache.getErrors().isEmpty())
                            builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                        cache.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                        String str = builder.toString();
                        if (str.length() > 2000)
                            str = str.substring(0, 1994) + " (Omis ci-dessous)";
                        m.editMessage(FormatUtil.filter(str)).queue();
                    });
                });
                try {
                    bot.getCacheLoader().deleteCache(event.getGuild().getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }


            if (handler.playFromDefault()) {
                Settings settings = event.getClient().getSettingsFor(event.getGuild());
                handler.stopAndClear();
                Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild().getId(), settings.getDefaultPlaylist());
                if (playlist == null) {
                    event.replyError("Impossible de trouver `" + event.getArgs() + ".txt` dans le dossier de la liste de lecture.");
                    return;
                }
                event.getChannel().sendMessage(loadingEmoji + " Chargement de la playlist **" + settings.getDefaultPlaylist() + " ** ... (" + playlist.getItems().size() + " chansons)").queue(m -> {
                    playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                        StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                                ? event.getClient().getWarning() + " Aucune chanson chargée !"
                                : event.getClient().getSuccess() + " ** " + playlist.getTracks().size() + " ** Chanson chargée !");
                        if (!playlist.getErrors().isEmpty())
                            builder.append("\nÉchec du chargement des chansons suivantes :");
                        playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                        String str = builder.toString();
                        if (str.length() > 2000)
                            str = str.substring(0, 1994) + " (...)";
                        m.editMessage(FormatUtil.filter(str)).queue();
                    });
                });
                return;
            }

            StringBuilder builder = new StringBuilder(event.getClient().getWarning() + "Commande de lecture :\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <nom de la chanson>` - Lire le premier résultat de YouTube");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - Lire la chanson, la liste de lecture ou le flux spécifié");
            for (Command cmd : children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">")
                ? event.getArgs().substring(1, event.getArgs().length() - 1)
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        event.reply(loadingEmoji + "Chargement `[" + args + "]`...", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m, event, false)));
    }

    @Override
    public void doCommand(SlashCommandEvent slashCommandEvent) {
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final Message m;
        private final CommandEvent event;
        private final boolean ytsearch;

        private ResultHandler(Message m, CommandEvent event, boolean ytsearch) {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }

        private void loadSingle(AudioTrack track, AudioPlaylist playlist) {
            if (bot.getConfig().isTooLong(track)) {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning() +
                        " **" + track.getInfo().title + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) dépasse la durée configurée de `" + FormatUtil.formatTime(bot.getConfig().getMaxSeconds() * 1000) + "`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;

            // Exemple de message de sortie :
            // Ajout de <titre> (<longueur>).
            // Ajout de <Titre> (<Longueur>) à la <numéro de lecture>ième file d'attente.
            String addMsg = FormatUtil.filter(event.getClient().getSuccess() + " **" + track.getInfo().title
                    + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "Ajouté." : "En attente de lecture " + pos + "Ajoutée."));
            if (playlist == null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                m.editMessage(addMsg).queue();
            else {
                new ButtonMenu.Builder()
                        .setText(addMsg + "\n" + event.getClient().getWarning() + " La playlist de cette chanson contient plus de **" + playlist.getTracks().size() + "** chansons. Sélectionnez " + LOAD + " pour charger la piste. ")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re -> {
                            if (re.getName().equals(LOAD))
                                m.editMessage(addMsg + "\n" + event.getClient().getSuccess() + "**" + loadPlaylist(playlist, track) + "** Chanson ajoutée à la file d'attente !").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m -> {
                            try {
                                m.clearReactions().queue();
                            } catch (PermissionException ignore) {
                            }
                        }).build().display(m);
            }
        }

        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude) {
            int[] count = {0};
            playlist.getTracks().forEach((track) -> {
                if (!bot.getConfig().isTooLong(track) && !track.equals(exclude)) {
                    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            if (playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
                AudioTrack single = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            } else if (playlist.getSelectedTrack() != null) {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            } else {
                int count = loadPlaylist(playlist, null);
                if (count == 0) {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning() + " Dans cette playlist" + (playlist.getName() == null ? "" : "(**" + playlist.getName()
                            + "**) ") + "est plus long que la longueur maximale autorisée (`" + bot.getConfig().getMaxTime() + "`)")).queue();
                } else {
                    m.editMessage(FormatUtil.filter(event.getClient().getSuccess()
                            + (playlist.getName() == null ? "Playlist" : "Playlist **" + playlist.getName() + "**") + "`"
                            + playlist.getTracks().size() + "` La chanson a été ajoutée à la file d'attente."
                            + (count < playlist.getTracks().size() ? "\n" + event.getClient().getWarning() + " Piste plus longue que la longueur maximale autorisée (`"
                            + bot.getConfig().getMaxTime() + "`) omise." : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches() {
            if (ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning() + " Aucun résultat de recherche pour `" + event.getArgs() + "`.")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + event.getArgs(), new ResultHandler(m, event, true));
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON) {
                m.editMessage(event.getClient().getError() + " Une erreur s'est produite lors du chargement : " + throwable.getMessage()).queue();
            } else {
                if (m.getAuthor().getIdLong() == bot.getConfig().getOwnerId() || m.getMember().isOwner()) {
                    m.editMessage(event.getClient().getError() + " Une erreur s'est produite lors du chargement de la chanson.\n" +
                            "**Contenu de l'erreur : " + throwable.getLocalizedMessage() + "**").queue();
                    StackTraceUtil.sendStackTrace(event.getTextChannel(), throwable);
                    return;
                }

                m.editMessage(event.getClient().getError() + " Une erreur s'est produite lors du chargement de la chanson.").queue();
            }
        }
    }

    public class RequestCmd extends MusicCommand {
        private final static String LOAD = "\uD83D\uDCE5"; // 📥
        private final static String CANCEL = "\uD83D\uDEAB"; // 🚫

        private final String loadingEmoji;
        private final JDA jda;

        public RequestCmd(Bot bot) {
            super(bot);
            this.jda = bot.getJDA();
            this.loadingEmoji = bot.getConfig().getLoading();
            this.name = "demande";
            this.arguments = "<titre|URL>";
            this.help = "demander une chanson";
            this.aliases = bot.getConfig().getAliases(this.name);
            this.beListening = true;
            this.bePlaying = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "input", "URL ou nom de la chanson", false));
            this.options = options;
        }


        @Override
        public void doCommand(CommandEvent event) {
        }

        @Override
        public void doCommand(SlashCommandEvent event) {

            if (event.getOption("input") == null) {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                if (handler.getPlayer().getPlayingTrack() != null && handler.getPlayer().isPaused()) {
                    if (DJCommand.checkDJPermission(event.getClient(), event)) {

                        handler.getPlayer().setPaused(false);
                        event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title + " Reprise de la lecture **.").queue();

                        Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PLAYING);
                    } else
                        event.reply(event.getClient().getError() + "Seul le DJ peut reprendre la lecture !").queue();
                    return;
                }

                // Mécanisme de chargement du cache
                if (bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
                    List<Cache> data = bot.getCacheLoader().GetCache(event.getGuild().getId());

                    AtomicInteger count = new AtomicInteger();
                    CacheLoader.CacheResult cache = bot.getCacheLoader().ConvertCache(data);
                    event.reply(":calling: Chargement des fichiers de cache... (" + cache.getItems().size() + " chansons)").queue(m -> {
                        cache.loadTracks(bot.getPlayerManager(), (at) -> {
                            // TODO: Utiliser un ID utilisateur mis en cache.
                            handler.addTrack(new QueuedTrack(at, event.getUser()));
                            count.getAndIncrement();
                        }, () -> {
                            StringBuilder builder = new StringBuilder(cache.getTracks().isEmpty()
                                    ? event.getClient().getWarning() + " La chanson n'est pas chargée."
                                    : event.getClient().getSuccess() + " du fichier cache, " + "**" + cache.getTracks().size() + "** morceau chargé.");
                            if (!cache.getErrors().isEmpty())
                                builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                            cache.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                            String str = builder.toString();
                            if (str.length() > 2000)
                                str = str.substring(0, 1994) + " (Omis ci-dessous)";
                            m.editOriginal(FormatUtil.filter(str)).queue();
                        });
                    });
                    try {
                        bot.getCacheLoader().deleteCache(event.getGuild().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (handler.playFromDefault()) {
                    Settings settings = event.getClient().getSettingsFor(event.getGuild());
                    handler.stopAndClear();
                    Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild().getId(), settings.getDefaultPlaylist());
                    if (playlist == null) {
                        event.reply("Impossible de trouver `" + event.getOption("input").getAsString() + ".txt` dans le dossier de la liste de lecture.").queue();
                        return;
                    }
                    event.reply(loadingEmoji + " Chargement de la playlist **" + settings.getDefaultPlaylist() + " ** ...( " + playlist.getItems().size() + " chansons)").queue(m ->
                    {

                        playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getUser())), () -> {
                            StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                                    ? event.getClient().getWarning() + " Aucune chanson chargée !"
                                    : event.getClient().getSuccess() + " ** " + playlist.getTracks().size() + " ** Chanson chargée !");
                            if (!playlist.getErrors().isEmpty())
                                builder.append("\nÉchec du chargement de la chanson suivante :");
                            playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                            String str = builder.toString();
                            if (str.length() > 2000)
                                str = str.substring(0, 1994) + " (...)";
                            m.editOriginal(FormatUtil.filter(str)).queue();
                        });
                    });
                    return;
                }

                StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Commande de lecture :\n");
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <nom de la chanson>` - Joue le premier résultat de YouTube.");
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - Joue la chanson, la liste de lecture ou le flux spécifié.");
                for (Command cmd : children)
                    builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
                event.reply(builder.toString()).queue();
                return;

            }
            event.reply(loadingEmoji + "`[" + event.getOption("input").getAsString() + " Chargement ]`...").queue(m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), event.getOption("input").getAsString(), new SlashResultHandler(m, event, false)));
        }

        public class SlashResultHandler implements AudioLoadResultHandler {
            private final InteractionHook m;
            private final SlashCommandEvent event;
            private final boolean ytsearch;

            SlashResultHandler(InteractionHook m, SlashCommandEvent event, boolean ytsearch) {
                this.m = m;
                this.event = event;
                this.ytsearch = ytsearch;
            }

            private void loadSingle(AudioTrack track, AudioPlaylist playlist) {
                if (bot.getConfig().isTooLong(track)) {
                    m.editOriginal(FormatUtil.filter(event.getClient().getWarning() +
                            " **" + track.getInfo().title + "**`(" + FormatUtil.formatTime(track.getDuration()) + ")` dépasse la durée configurée `(" + FormatUtil.formatTime(bot.getConfig().getMaxSeconds() * 1000) + ")`.")).queue();
                    return;
                }
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                int pos = handler.addTrack(new QueuedTrack(track, event.getUser())) + 1;

                // Message de sortie ex:
                // Ajout de <titre><(durée)>.
                // Ajout de <titre><(durée)> à la file d'attente de lecture <numéro d'attente>.
                String addMsg = FormatUtil.filter(client.getSuccess() + " **" + track.getInfo().title
                        + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) Ajout de " + pos + "Musique en attente de lecture.");
                if (playlist == null || !event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION)) {
                    m.editOriginal(addMsg).queue();
                } else {
                    new ButtonMenu.Builder()
                            .setText(addMsg + "\n" + event.getClient().getWarning() + " La playlist de cette chanson contient plus de **" + playlist.getTracks().size() + "** chansons. Sélectionnez " + LOAD + " pour charger la piste. ")
                            .setChoices(LOAD, CANCEL)
                            .setEventWaiter(bot.getWaiter())
                            .setTimeout(30, TimeUnit.SECONDS)
                            .setAction(re ->
                            {
                                if (re.getName().equals(LOAD))
                                    m.editOriginal(addMsg + "\n" + event.getClient().getSuccess() + "**" + loadPlaylist(playlist, track) + "** Chanson ajoutée à la file d'attente!").queue();
                                else
                                    m.editOriginal(addMsg).queue();
                            }).setFinalAction(m ->
                            {
                                try {
                                    m.clearReactions().queue();
                                    m.delete().queue();
                                } catch (PermissionException ignore) {
                                }
                            }).build().display(event.getChannel());
                }
            }

            private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude) {
                int[] count = {0};
                playlist.getTracks().forEach((track) -> {
                    if (!bot.getConfig().isTooLong(track) && !track.equals(exclude)) {
                        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                        handler.addTrack(new QueuedTrack(track, event.getUser()));
                        count[0]++;
                    }
                });
                return count[0];
            }

            @Override
            public void trackLoaded(AudioTrack track) {
                loadSingle(track, null);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
                    AudioTrack single = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                    loadSingle(single, null);
                } else if (playlist.getSelectedTrack() != null) {
                    AudioTrack single = playlist.getSelectedTrack();
                    loadSingle(single, playlist);
                } else {
                    int count = loadPlaylist(playlist, null);
                    if (count == 0) {
                        m.editOriginal(FormatUtil.filter(event.getClient().getWarning() + " Dans cette playlist" + (playlist.getName() == null ? "" : "(**" + playlist.getName()
                                + "**) ") + "est plus longue que la durée maximale autorisée (`" + bot.getConfig().getMaxTime() + "`).")).queue();
                    } else {
                        m.editOriginal(FormatUtil.filter(event.getClient().getSuccess()
                                + (playlist.getName() == null ? "Playlist" : "Playlist **" + playlist.getName() + "**") + "`"
                                + playlist.getTracks().size() + "` La chanson a été ajoutée à la file d'attente."
                                + (count < playlist.getTracks().size() ? "\n" + event.getClient().getWarning() + " Piste plus longue que la durée maximale autorisée (`"
                                + bot.getConfig().getMaxTime() + "`) omise.": ""))).queue();
                    }
                }
            }

            @Override
            public void noMatches() {
                if (ytsearch)
                    m.editOriginal(FormatUtil.filter(event.getClient().getWarning() + " Aucun résultat de recherche pour `" + event.getOption("input").getAsString() + "`.")).queue();
                else
                    bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + event.getOption("input").getAsString(), new SlashResultHandler(m, event, true));
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                if (throwable.severity == Severity.COMMON) {
                    m.editOriginal(event.getClient().getError() + " Une erreur est survenue lors du chargement : " + throwable.getMessage()).queue();
                } else {
                    m.editOriginal(event.getClient().getError() + " Une erreur est survenue lors du chargement de la chanson.").queue();
                }
            }
        }
    }


    public class PlaylistCmd extends MusicCommand {
        public PlaylistCmd(Bot bot) {
            super(bot);
            this.name = "playlist";
            this.aliases = new String[]{"pl"};
            this.arguments = "<nom>";
            this.help = "joue la liste de lecture fournie";
            this.beListening = true;
            this.bePlaying = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "nom", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String guildId = event.getGuild().getId();
            if (event.getArgs().isEmpty()) {
                event.reply(event.getClient().getError() + "Veuillez inclure le nom de la liste de lecture.");
                return;
            }
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(guildId, event.getArgs());
            if (playlist == null) {
                event.replyError("Impossible de trouver le fichier `" + event.getArgs() + ".txt`");
                return;
            }
            event.getChannel().sendMessage(":calling: Chargement de la playlist **" + event.getArgs() + "**... (" + playlist.getItems().size() + " chansons)").queue(m -> {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + "Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String guildId = event.getGuild().getId();

            String name = event.getOption("name").getAsString();

            Playlist playlist = bot.getPlaylistLoader().getPlaylist(guildId, name);
            if (playlist == null) {
                event.reply(event.getClient().getError() + "Impossible de trouver le fichier `" + name + ".txt`").queue();
                return;
            }
            event.reply(":calling: Chargement de la playlist **" + name + "**... (" + playlist.getItems().size() + " chansons)").queue(m -> {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getUser())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + "Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editOriginal(FormatUtil.filter(str)).queue();
                });
            });
        }
    }

    public class MylistCmd extends MusicCommand {
        public MylistCmd(Bot bot) {
            super(bot);
            this.name = "mylist";
            this.aliases = new String[]{"ml"};
            this.arguments = "<nom>";
            this.help = "jouer ma liste";
            this.beListening = true;
            this.bePlaying = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "Nom de ma liste", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String userId = event.getAuthor().getId();
            if (event.getArgs().isEmpty()) {
                event.reply(event.getClient().getError() + " Veuillez inclure le nom de ma liste.");
                return;
            }
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, event.getArgs());
            if (playlist == null) {
                event.replyError("Impossible de trouver le fichier `" + event.getArgs() + ".txt `");
                return;
            }
            event.getChannel().sendMessage(":calling: Ma liste **" + event.getArgs() + "** Chargement en cours... (" + playlist.getItems().size() + " chansons)").queue(m ->
            {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + " Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** Chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1)
                            .append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();

            String name = event.getOption("name").getAsString();

            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, name);
            if (playlist == null) {
                event.reply(event.getClient().getError() + "Impossible de trouver le fichier `" + name + ".txt").queue();
                return;
            }
            event.reply(":calling: Ma liste **" + name + "** Chargement en cours... (" + playlist.getItems().size() + " chansons)").queue(m ->
            {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getUser())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + " Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** Chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1)
                            .append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editOriginal(FormatUtil.filter(str)).queue();
                });
            });
        }
    }

    public class PublistCmd extends MusicCommand {
        public PublistCmd(Bot bot) {
            super(bot);
            this.name = "publist";
            this.aliases = new String[]{"pul"};
            this.arguments = "<nom>";
            this.help = "Lire la liste publique";
            this.beListening = true;
            this.bePlaying = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste publique", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.reply(event.getClient().getError() + " Veuillez inclure le nom de la liste de lecture.");
                return;
            }
            PubliclistLoader.Playlist playlist = bot.getPublistLoader().getPlaylist(event.getArgs());
            if (playlist == null) {
                event.replyError("Impossible de trouver le fichier `" + event.getArgs() + ".txt `");
                return;
            }
            event.getChannel().sendMessage(":calling: Playlist **" + event.getArgs() + "** Chargement en cours... (" + playlist.getItems().size() + " chansons)").queue(m -> {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + " Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** Chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1)
                            .append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String name = event.getOption("name").getAsString();
            PubliclistLoader.Playlist playlist = bot.getPublistLoader().getPlaylist(name);
            if (playlist == null) {
                event.reply(event.getClient().getError() + "Impossible de trouver le fichier `" + name + ".txt").queue();
                return;
            }
            event.reply(":calling: Playlist **" + name + "** Chargement en cours... (" + playlist.getItems().size() + " chansons)").queue(m -> {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at) -> handler.addTrack(new QueuedTrack(at, event.getUser())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + " Aucune chanson chargée."
                            : event.getClient().getSuccess() + "**" + playlist.getTracks().size() + "** Chanson(s) chargée(s).");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nLes chansons suivantes n'ont pas pu être chargées :");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1)
                            .append("]` **").append(err.getItem()).append("** : ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (Suite omise)";
                    m.editOriginal(FormatUtil.filter(str)).queue();
                });
            });
        }
    }
}
