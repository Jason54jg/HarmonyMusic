package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class NowplayingCmd extends MusicCommand {
    public NowplayingCmd(Bot bot) {
        super(bot);
        this.name = "nowplaying";
        this.help = "Afficher la chanson en cours de lecture";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        MessageCreateData m = null;
        try {
            m = handler.getNowPlaying(event.getJDA());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (m == null) {
            event.reply(handler.getNoMusicPlaying(event.getJDA()));
            bot.getNowplayingHandler().clearLastNPMessage(event.getGuild());
        } else {
            event.reply(m, bot.getNowplayingHandler()::setLastNPMessage);
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        MessageCreateData m = null;
        try {
            m = handler.getNowPlaying(event.getJDA());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        event.reply("Afficher la chanson en cours de lecture...").queue(h -> h.deleteOriginal().queue());

        if (m == null) {
            event.getTextChannel().sendMessage(handler.getNoMusicPlaying(event.getJDA())).queue();
            bot.getNowplayingHandler().clearLastNPMessage(event.getGuild());
        } else {
            event.getTextChannel().sendMessage(m).queue(bot.getNowplayingHandler()::setLastNPMessage);
        }
    }
}
