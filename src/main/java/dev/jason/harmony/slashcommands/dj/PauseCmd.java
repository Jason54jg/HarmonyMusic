package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.PlayStatus;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.DJCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseCmd extends DJCommand {
    Logger log = LoggerFactory.getLogger("Pause");

    public PauseCmd(Bot bot) {
        super(bot);
        this.name = "pause";
        this.help = "Mettre en pause la chanson en cours";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getPlayer().isPaused()) {
            event.replyWarning("La chanson est déjà en pause. Vous pouvez la réactiver en utilisant `" + event.getClient().getPrefix() + " play`.");
            return;
        }
        handler.getPlayer().setPaused(true);
        log.info(event.getGuild().getName() + "En pause à " + handler.getPlayer().getPlayingTrack().getInfo().title + ".");
                event.replySuccess("**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** en pause. `" + event.getClient().getPrefix() + " Utilisez play` pour reprendre. ");

        Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PAUSED);
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getPlayer().isPaused()) {
            event.reply(event.getClient().getWarning() + "La chanson est déjà en pause. Vous pouvez la réactiver en utilisant `" + event.getClient().getPrefix() + "play`.").queue();
            return;
        }
        handler.getPlayer().setPaused(true);
        log.info(event.getGuild().getName() + "En pause à " + handler.getPlayer().getPlayingTrack().getInfo().title + ".");
                event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** paused. Utilisez `" + event.getClient(). getPrefix() + "play` pour reprendre.").queue();

        Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PAUSED);
    }
}
