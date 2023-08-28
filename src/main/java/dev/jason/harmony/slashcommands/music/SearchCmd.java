package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.QueuedTrack;
import com.jason.harmony.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.jason.harmony.slashcommands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchCmd extends MusicCommand {
    private final OrderedMenu.Builder builder;
    private final String searchingEmoji;
    protected String searchPrefix = "ytsearch:";

    public SearchCmd(Bot bot) {
        super(bot);
        this.searchingEmoji = bot.getConfig().getSearching();
        this.name = "recherche";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.arguments = "<query>";
        this.help = "Rechercher des vidéos sur YouTube en utilisant la requête spécifiée.";
        this.beListening = true;
        this.bePlaying = false;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "input", "Requête de recherche", true));
        this.options = options;

        builder = new OrderedMenu.Builder()
                .allowTextInput(true)
                .useNumbers()
                .useCancelButton(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Veuillez spécifier une requête de recherche.");
            return;
        }
        event.reply(searchingEmoji + "Recherche en cours pour `[" + event.getArgs() + "]`...",
                m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), searchPrefix + event.getArgs(), new ResultHandler(m, event)));
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        event.reply(searchingEmoji + "Recherche en cours pour `[" + event.getOption("input").getAsString() + "]`...").queue(
                m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), searchPrefix + event.getOption("input").getAsString(), new SlashResultHandler(m, event)));
    }

    private class SlashResultHandler implements AudioLoadResultHandler {
        private final InteractionHook m;
        private final SlashCommandEvent event;

        private SlashResultHandler(InteractionHook m, SlashCommandEvent event) {
            this.m = m;
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if (bot.getConfig().isTooLong(track)) {
                m.editOriginal(FormatUtil.filter(event.getClient().getWarning() + "**" + track.getInfo().title + "** est plus longue que la durée maximale autorisée."
                        + FormatUtil.formatTime(track.getDuration()) + " > " + bot.getConfig().getMaxTime())).queue();
                return;
            }
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getUser())) + 1;
            m.editOriginal(FormatUtil.filter(event.getClient().getSuccess() + "**" + track.getInfo().title
                    + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0? "Commence à jouer."
                    : " a été ajoutée à la " + pos + "ème file d'attente de lecture."))).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            builder.setColor(event.getGuild().getSelfMember().getColor())
                    .setText(FormatUtil.filter(event.getClient().getSuccess() + " Résultats de recherche pour `" + event.getOption("input").getAsString() + "` :"))
                    .setChoices()
                    .setSelection((msg, i) ->
                    {
                        AudioTrack track = playlist.getTracks().get(i - 1);
                        if (bot.getConfig().isTooLong(track)) {
                            event.reply(event.getClient().getWarning() + "**" + track.getInfo().title + "** est plus longue que la durée maximale autorisée : `"
                                    + FormatUtil.formatTime(track.getDuration()) + " > " + bot.getConfig().getMaxTime() + "`").queue();
                            return;
                        }
                        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                        int pos = handler.addTrack(new QueuedTrack(track, event.getUser())) + 1;
                        event.reply(event.getClient().getSuccess() + "**" + track.getInfo().title
                                + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0? "Commence à jouer."
                                : " a été ajoutée à la " + pos + "ème file d'attente de lecture.")).queue();
                    })
                    .setCancel((msg) -> {
                    })
                    .setUsers(event.getUser())
            ;
            for (int i = 0; i < 4 && i < playlist.getTracks().size(); i++) {
                AudioTrack track = playlist.getTracks().get(i);
                builder.addChoices("`[" + FormatUtil.formatTime(track.getDuration()) + "]` [**" + track.getInfo().title + "**](" + track.getInfo().uri + ")");
            }
            builder.build().display(event.getChannel());
        }

        @Override
        public void noMatches() {
            m.editOriginal(FormatUtil.filter(event.getClient().getWarning() + " Aucun résultat trouvé pour `" + event.getOption("input").getAsString() + "`.")).queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON)
                m.editOriginal(event.getClient().getError() + " Une erreur s'est produite lors du chargement : " + throwable.getMessage()).queue();
            else
                m.editOriginal(event.getClient().getError() + " Une erreur s'est produite lors du chargement. ").queue();
        }
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final Message m;
        private final CommandEvent event;

        private ResultHandler(Message m, CommandEvent event) {
            this.m = m;
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if (bot.getConfig().isTooLong(track)) {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning() + "Cette chanson (**" + track.getInfo().title + "**) est plus longue que la durée maximale autorisée. `"
                        + FormatUtil.formatTime(track.getDuration()) + " > " + bot.getConfig().getMaxTime() + "`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
            m.editMessage(FormatUtil.filter(event.getClient().getSuccess() + "**" + track.getInfo().title
                    + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0? "Commence à jouer."
                    : " a été ajoutée à la " + pos + "ème file d'attente de lecture."))).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            builder.setColor(event.getSelfMember().getColor())
                    .setText(FormatUtil.filter(event.getClient().getSuccess() + " Résultats de recherche pour `" + event.getArgs() + "` :"))
                    .setChoices()
                    .setSelection((msg, i) ->
                    {
                        AudioTrack track = playlist.getTracks().get(i - 1);
                        if (bot.getConfig().isTooLong(track)) {
                            event.replyWarning("Cette chanson (**" + track.getInfo().title + "**) est plus longue que la durée maximale autorisée : `"
                                    + FormatUtil.formatTime(track.getDuration()) + " > " + bot.getConfig().getMaxTime() + "`");
                            return;
                        }
                        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                        int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
                        event.replySuccess("**" + FormatUtil.filter(track.getInfo().title)
                                + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0? "Commence à jouer."
                                : " a été ajoutée à la " + pos + "ème file d'attente de lecture."));
                    })
                    .setCancel((msg) -> {
                    })
                    .setUsers(event.getAuthor())
            ;
            for (int i = 0; i < 4 && i < playlist.getTracks().size(); i++) {
                AudioTrack track = playlist.getTracks().get(i);
                builder.addChoices("`[" + FormatUtil.formatTime(track.getDuration()) + "]` [**" + track.getInfo().title + "**](" + track.getInfo().uri + ")");
            }
            builder.build().display(m);
        }

        @Override
        public void noMatches() {
            m.editMessage(FormatUtil.filter(event.getClient().getWarning() + " Aucun résultat n'a été trouvé pour `" + event.getArgs() + "`.")).queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON)
                m.editMessage(event.getClient().getError() + " Erreur lors du chargement : " + throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError() + " Échec lors du chargement de la chanson. ").queue();
        }
    }
}