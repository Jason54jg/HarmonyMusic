package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.RequestMetadata;
import dev.jason.harmony.slashcommands.DJCommand;

public class ForceskipCmd extends DJCommand {
    public ForceskipCmd(Bot bot) {
        super(bot);
        this.name = "forceskip";
        this.help = "sauter la chanson en cours";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                + "** " + (rm.getOwner() == 0L ? "(autoplay)": "(demandé par **" + rm.user.username + "**)"));
        handler.getPlayer().stopTrack();
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title
                + "** " + (rm.getOwner() == 0L ? "(autoplay)": "(demandé par **" + rm.user.username + "**)")).queue();
        handler.getPlayer().stopTrack();
    }
}
