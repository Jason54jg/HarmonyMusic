package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.DJCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SkipToCmd extends DJCommand {
    Logger log = LoggerFactory.getLogger("Skip");

    public SkipToCmd(Bot bot) {
        super(bot);
        this.name = "skipto";
        this.help = "Passer à la chanson spécifiée";
        this.arguments = "<position>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "position", "position", true));
        this.options = options;

    }

    @Override
    public void doCommand(CommandEvent event) {
        int index = 0;
        try {
            index = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException e) {
            event.reply(event.getClient().getError() + " `" + event.getArgs() + "` n'est pas un entier valide.");
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (index < 1 || index > handler.getQueue().size()) {
            event.reply(event.getClient().getError() + "doit être un entier compris entre 1 et " + handler.getQueue().size() + "!");
            return;
        }
        handler.getQueue().skip(index - 1);
        event.reply(event.getClient().getSuccess() + "Sauté à **" + handler.getQueue().get(0).getTrack().getInfo().title + ".**");
        handler.getPlayer().stopTrack();
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        int index = 0;
        try {
            index = Integer.parseInt(event.getOption("position").getAsString());
        } catch (NumberFormatException e) {
            event.reply(event.getClient().getError() + " `" + event.getOption("position").getAsString() + "` n'est pas un entier valide.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (index < 1 || index > handler.getQueue().size()) {
            event.reply(event.getClient().getError() + "doit être un entier compris entre 1 et " + handler.getQueue().size() + "!").queue();
            return;
        }
        handler.getQueue().skip(index - 1);
        event.reply(event.getClient().getSuccess() + "Sauté à **" + handler.getQueue().get(0).getTrack().getInfo().title + ".**").queue();
        handler.getPlayer().stopTrack();
    }
}
