package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.QueuedTrack;
import com.jason.harmony.settings.Settings;
import dev.jason.harmony.slashcommands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class RemoveCmd extends MusicCommand {
    public RemoveCmd(Bot bot) {
        super(bot);
        this.name = "remove";
        this.help = "Supprimer la chanson de la file d'attente de lecture";
        this.arguments = "<numéro|Tous>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "input", "Numéro|Tous", true));
        this.options = options;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.replyError("Rien n'attend de jouer.");
            return;
        }
        if (event.getArgs().toLowerCase().matches("(tous)")) {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
            if (count == 0)
                event.replyWarning("Il n'y a pas de chansons dans la file d'attente de lecture.");
            else
                event.replySuccess(count + "Chanson supprimée.");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException e) {
            pos = 0;
        }
        if (pos < 1 || pos > handler.getQueue().size()) {
            event.replyError(String.format("Veuillez entrer un nombre valide entre 1 et %s!", handler.getQueue().size()));
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
        QueuedTrack qt = handler.getQueue().get(pos - 1);
        if (qt.getIdentifier() == event.getAuthor().getIdLong()) {
            handler.getQueue().remove(pos - 1);
            event.replySuccess("**" + qt.getTrack().getInfo().title + "** retiré de la file d'attente.");
        } else if (isDJ) {
            handler.getQueue().remove(pos - 1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch (Exception e) {
                u = null;
            }
            event.replySuccess("**" + qt.getTrack().getInfo().title
                    + "** supprimé de la file d'attente.\n(Cette chanson est " + (u == null ? "Quelqu'un l'a demandée." : "**" + u.getName() + "** a demandé que je l'ai fait.") + " )");
        } else {
            event.replySuccess("Suppression de **" + qt.getTrack().getInfo().title + "** de la file d'attente.");
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.reply(event.getClient().getError() + "Rien n'attend de jouer.").queue();
            return;
        }

        if (event.getOption("input").getAsString().toLowerCase().matches("(tous)")) {
            int count = handler.getQueue().removeAll(event.getUser().getIdLong());
            if (count == 0)
                event.reply(event.getClient().getWarning() + "Il n'y a pas de chansons dans la file d'attente de lecture.").queue();
            else
                event.reply(event.getClient().getSuccess() + count + "Chanson supprimée.").queue();
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getOption("input").getAsString());
        } catch (NumberFormatException e) {
            pos = 0;
        }
        if (pos < 1 || pos > handler.getQueue().size()) {
            event.reply(event.getClient().getError() + String.format("Veuillez entrer un nombre valide entre 1 et %s !", handler.getQueue().size())).queue();
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
        QueuedTrack qt = handler.getQueue().get(pos - 1);
        if (qt.getIdentifier() == event.getUser().getIdLong()) {
            handler.getQueue().remove(pos - 1);
            event.reply(event.getClient().getSuccess() + "**" + qt.getTrack().getInfo().title + "** retiré de la file d'attente.").queue();
        } else if (isDJ) {
            handler.getQueue().remove(pos - 1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch (Exception e) {
                u = null;
            }
            event.reply(event.getClient().getSuccess() + "**" + qt.getTrack().getInfo().title
                    + "** supprimé de la file d'attente.\n(Cette chanson est " + (u == null ? "Quelqu'un l'a demandée." : "**" + u.getName() + "** a demandé que je l'ai fait.") + " )").queue();
        } else {
            event.reply(event.getClient().getError() + "**" + qt.getTrack().getInfo().title + "** n'a pas pu être supprimé. Raison: avez-vous des droits de DJ? Vous ne pouvez supprimer que vos propres demandes.").queue();
        }
    }
}
