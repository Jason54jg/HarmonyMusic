package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.jason.harmony.slashcommands.DJCommand;
import net.dv8tion.jda.api.entities.User;

public class NextCmd extends DJCommand {
    public NextCmd(Bot bot) {
        super(bot);
        this.name = "next";
        this.help = "Si le mode de répétition est activé, sautez la chanson en cours sans la retirer de la file d'attente";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        User u = event.getJDA().getUserById(handler.getRequestMetadata().user.id);

        AudioTrack track = handler.getPlayer().getPlayingTrack();
        handler.addTrackIfRepeat(track);

        event.reply(event.getClient().getSuccess() + " **" + handler.getPlayer().getPlayingTrack().getInfo().title
                + "** ignoré. (" + (u == null ? "quelqu'un" : "**" + u.getName() + "**") + "demandé.)");
        handler.getPlayer().stopTrack();
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(client, event)) {
            event.reply(client.getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        User u = event.getJDA().getUserById(handler.getRequestMetadata().user.id);

        AudioTrack track = handler.getPlayer().getPlayingTrack();
        handler.addTrackIfRepeat(track);

        event.reply(client.getSuccess() + " **" + handler.getPlayer().getPlayingTrack().getInfo().title
                + "** ignoré. (" + (u == null ? "quelqu'un" : "**" + u.getName() + "**") + "demandé.)").queue();
        handler.getPlayer().stopTrack();
    }
}
