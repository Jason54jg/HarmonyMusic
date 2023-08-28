package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.RequestMetadata;
import dev.jason.harmony.slashcommands.MusicCommand;

public class SkipCmd extends MusicCommand {
    public SkipCmd(Bot bot) {
        super(bot);
        this.name = "passer";
        this.help = "Demande de sauter la chanson en cours";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getAuthor().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** ignoré.");
            handler.getPlayer().stopTrack();
        } else {
            // Nombre de personnes dans la chaîne vocale (à l'exception des bots et des sourds)
            int listeners = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // Message à envoyer
            String msg;

            // Obtenir le vote actuel et vérifier si l'expéditeur du message a déjà voté
            if (handler.getVotes().contains(event.getAuthor().getId())) {
                msg = event.getClient().getWarning() + "La chanson en cours de lecture a été demandée pour être ignorée. `[";
            } else {
                msg = event.getClient().getSuccess() + "Vous avez demandé à ignorer la chanson en cours. `[";
                handler.getVotes().add(event.getAuthor().getId());
            }

            // Obtenir le nombre de personnes dans la chaîne vocale qui votent pour sauter
            int skippers = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            int requis = (int) Math.ceil(listeners * bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio());
            msg += skippers + " votes, " + requis + "/" + listeners + " requis]`";

            // Si le nombre de votes requis est différent du nombre de personnes dans la chaîne vocale
            if (requis != listeners) {
                // Ajouter un message supplémentaire
                msg += "Le nombre de demandes de saut est de " + skippers + ". Pour sauter, " + requis + "/" + listeners + " requis]`";
            } else {
                msg = "";
            }

            // Si le nombre actuel de votants atteint le nombre de votes requis
            if (skippers >= requis) {
                msg += "\n" + event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                        + "** ignoré. " + (rm.getOwner() == 0L ? "(lecture automatique)" : "(**" + rm.user.username + "** a demandé)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getUser().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** ignoré.").queue();
            handler.getPlayer().stopTrack();
        } else {
            // Nombre de personnes dans la chaîne vocale (à l'exception des bots et des sourds)
            int listeners = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // Message à envoyer
            String msg;

            // Obtenir le vote actuel et vérifier si l'expéditeur du message a déjà voté
            if (handler.getVotes().contains(event.getUser().getId())) {
                msg = event.getClient().getWarning() + "La chanson en cours de lecture a été demandée pour être ignorée. `[";
            } else {
                msg = event.getClient().getSuccess() + "Vous avez demandé à ignorer la chanson en cours. `[";
                handler.getVotes().add(event.getUser().getId());
            }

            // Obtenir le nombre de personnes dans la chaîne vocale qui votent pour sauter
            int skippers = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            // Nombre de votes nécessaires (nombre de personnes dans la chaîne vocale × 0.55)
            int requis = (int) Math.ceil(listeners * .55);

            // Si le nombre de votes requis est différent du nombre de personnes dans la chaîne vocale
            if (requis != listeners) {
                // Ajouter un message supplémentaire
                msg += "Le nombre de requêtes de saut est de " + skippers + ". Pour sauter, " + requis + "/" + listeners + " requis]`";
            } else {
                msg = "";
            }

            // Si le nombre actuel de votants atteint le nombre de votes requis
            if (skippers >= requis) {
                msg += "\n" + event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                        + "** ignoré. " + (rm.getOwner() == 0L ? "(lecture automatique)" : "(**" + rm.user.username + "** a demandé)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg).queue();
        }
    }
}