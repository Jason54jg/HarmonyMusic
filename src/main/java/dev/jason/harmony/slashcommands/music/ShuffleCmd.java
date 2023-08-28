package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.MusicCommand;

public class ShuffleCmd extends MusicCommand {
    public ShuffleCmd(Bot bot) {
        super(bot);
        this.name = "melanger";
        this.help = "Lecture aléatoire des chansons ajoutées";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int s = handler.getQueue().shuffle(event.getAuthor().getIdLong());
        switch (s) {
            case 0:
                event.replyError("Il n'y a pas de chansons dans la file d'attente !");
                break;
            case 1:
                event.replyWarning("Il n'y a actuellement qu'une seule chanson dans la file d'attente !");
                break;
            default:
                event.replySuccess(s + " chansons mélangées.");
                break;
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int s = handler.getQueue().shuffle(event.getUser().getIdLong());
        switch (s) {
            case 0:
                event.reply(event.getClient().getError() + "Il n'y a pas de chansons dans la file d'attente !").queue();
                break;
            case 1:
                event.reply(event.getClient().getWarning() + "Il n'y a actuellement qu'une seule chanson dans la file d'attente !").queue();
                break;
            default:
                event.reply(event.getClient().getSuccess() + s + " chansons ont été mélangées.").queue();
                break;
        }
    }
}
