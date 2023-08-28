package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.QueuedTrack;
import com.jason.harmony.queue.FairQueue;
import dev.jason.harmony.slashcommands.DJCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoveTrackCmd extends DJCommand {

    public MoveTrackCmd(Bot bot) {
        super(bot);
        this.name = "movetrack";
        this.help = "Changer l'ordre de lecture des morceaux en attente de lecture";
        this.arguments = "<de> <à>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "de", "la position actuelle dans la file d'attente", true));
        options.add(new OptionData(OptionType.INTEGER, "à", "la nouvelle position dans la file d'attente", true));
        this.options = options;
    }

    // Méthode pour vérifier si une position dans la file d'attente est invalide
    private static boolean isUnavailablePosition(FairQueue<QueuedTrack> queue, int position) {
        return (position < 1 || position > queue.size());
    }

    @Override
    public void doCommand(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("MoveTrack");
        int from;
        int to;

        String[] parts = event.getArgs().split("\\s+", 2);
        if (parts.length < 2) {
            event.replyError("Fournissez deux positions valides.");
            return;
        }

        try {
            // Valider les arguments
            from = Integer.parseInt(parts[0]);
            to = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            event.replyError("Fournissez deux positions valides.");
            return;
        }

        if (from == to) {
            event.replyError("Impossible de déplacer une piste vers la même position.");
            return;
        }

        // Valider que "de" et "à" sont disponibles
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        FairQueue<QueuedTrack> queue = handler.getQueue();
        if (isUnavailablePosition(queue, from)) {
            String reply = String.format("`%d` n'est pas une position valide dans la file d'attente!", from);
            event.replyError(reply);
            return;
        }
        if (isUnavailablePosition(queue, to)) {
            String reply = String.format("`%d` n'est pas une position valide dans la file d'attente!", to);
            event.replyError(reply);
            return;
        }

        // Déplacer la piste
        QueuedTrack track = queue.moveItem(from - 1, to - 1);
        String trackTitle = track.getTrack().getInfo().title;
        String reply = String.format("Déplacé **%s** de la position `%d` à `%d`.", trackTitle, from, to);
        log.info(event.getGuild().getName() + "Déplacé **%s** de la position `%d` à `%d`.", trackTitle, from, to);
        event.replySuccess(reply);
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        int from;
        int to;

        from = Integer.parseInt(event.getOption("de").getAsString());
        to = Integer.parseInt(event.getOption("à").getAsString());

        if (from == to) {
            event.reply(event.getClient().getError() + "Vous ne pouvez pas déplacer vers la même position.").queue();
            return;
        }

        // Valider que "de" et "à" sont disponibles
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        FairQueue<QueuedTrack> queue = handler.getQueue();
        if (isUnavailablePosition(queue, from)) {
            String reply = String.format("`%d` n'est pas une position valide dans la file d'attente!", from);
            event.reply(event.getClient().getError() + reply).queue();
            return;
        }
        if (isUnavailablePosition(queue, to)) {
            String reply = String.format("`%d` n'est pas une position valide dans la file d'attente!", to);
            event.reply(event.getClient().getError() + reply).queue();
            return;
        }

        // Déplacer la piste
        QueuedTrack track = queue.moveItem(from - 1, to - 1);
        String trackTitle = track.getTrack().getInfo().title;
        String reply = String.format("Déplacé **%s** de la position `%d` à `%d`.", trackTitle, from, to);
        event.reply(event.getClient().getSuccess() + reply).queue();
    }
}
