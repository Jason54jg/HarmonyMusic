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
        this.name = "skip";
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
            // Nombre de personnes dans Voicha (Bot, hors haut-parleur muet)
            int listeners = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // message à envoyer
            String msg;

            // Obtenez le vote actuel et si l'expéditeur du message est inclus
            if (handler.getVotes().contains(event.getAuthor().getId())) {
                msg = event.getClient().getWarning() + "La chanson en cours de lecture a été demandée pour être ignorée. `[";
            } else {
                msg = event.getClient().getSuccess() + "Vous avez demandé à ignorer la chanson en cours.`[";
                handler.getVotes().add(event.getAuthor().getId());
            }

            // Obtenez le nombre de personnes en boicha qui votent pour sauter
            int skippers = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            int required = (int) Math.ceil(listeners * bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio());
            msg += skippers + " votes, " + required + "/" + listeners + " requis]`";

            // Si le nombre de votes requis est différent du nombre de personnes sur Boicia
            if (required != listeners) {
                // Ajouter un message
                msg += "Le nombre de demandes de saut est de " + skippers + ". Pour sauter, " + required + "/" + listeners + " required.]`";
            } else {
                msg = "";
            }

            // Si le nombre actuel d'électeurs a atteint le nombre de votes requis
            if (skippers >= required) {
                msg += "\n" + event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                        + "** ignoré. " + (rm.getOwner() == 0L ? "(lecture automatique)": "(**" + rm.user.username + "** demandé)");
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
            // Nombre de personnes dans Voicha (Bot, hors haut-parleur muet)
            int listeners = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // message à envoyer
            String msg;

            // Obtenez le vote actuel et si l'expéditeur du message est inclus
            if (handler.getVotes().contains(event.getUser().getId())) {
                msg = event.getClient().getWarning() + "La chanson en cours de lecture a été demandée pour être ignorée. `[";
            } else {
                msg = event.getClient().getSuccess() + "Vous avez demandé à ignorer la chanson en cours.`[";
                handler.getVotes().add(event.getUser().getId());
            }

            // Obtenez le nombre de personnes en boicha qui votent pour sauter
            int skippers = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            // Nombre de votes nécessaires (nombre de personnes en Boitia × 0,55)
            int required = (int) Math.ceil(listeners * .55);

            // Si le nombre de votes requis est différent du nombre de personnes sur Boicia
            if (required != listeners) {
                // Ajouter un message
                msg += "Le nombre de requêtes de saut est de " + skippers + ". Pour sauter, " + required + "/" + listeners + " required.]`";
            } else {
                msg = "";
            }

            // Si le nombre actuel d'électeurs a atteint le nombre de votes requis
            if (skippers >= required) {
                msg += "\n" + client.getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                        + "** ignoré." + (rm.getOwner() == 0L ? "(lecture automatique)" : "(**" + rm.user.username + "** demandé)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg).queue();
        }
    }
}
