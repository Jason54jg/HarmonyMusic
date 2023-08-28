package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.slashcommands.MusicCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VolumeCmd extends MusicCommand {
    Logger log = LoggerFactory.getLogger("Volume");

    public VolumeCmd(Bot bot) {
        super(bot);
        this.name = "volume";
        this.aliases = new String[]{"vol"};
        this.help = "Règle ou affiche le volume";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.arguments = "[0-150]";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "vol", "Le volume est un entier de 0 à 150", true));
        this.options = options;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        int volume = Objects.requireNonNull(handler).getPlayer().getVolume();
        if (event.getArgs().isEmpty()) {
            event.reply(FormatUtil.volumeIcon(volume) + "Le volume actuel est `" + volume + "`.");
        } else {
            int nvolume;
            try {
                nvolume = Integer.parseInt(event.getArgs());
            } catch (NumberFormatException e) {
                nvolume = -1;
            }
            if (nvolume < 0 || nvolume > 150)
                event.reply(event.getClient().getError() + " Le volume doit être un entier compris entre 0 et 150.");
            else {
                handler.getPlayer().setVolume(nvolume);
                settings.setVolume(nvolume);
                event.reply(FormatUtil.volumeIcon(nvolume) + " Le volume est passé de `" + volume + "` à `" + nvolume + "`.");
                log.info(event.getGuild().getName() + "le volume est passé de " + volume + " à " + nvolume + ".");
            }
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {

        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        int volume = handler.getPlayer().getVolume();
        int nvolume;
        try {
            nvolume = Integer.parseInt(event.getOption("vol").getAsString());
        } catch (NumberFormatException e) {
            nvolume = -1;
        }
        if (nvolume < 0 || nvolume > 150)
            event.reply(event.getClient().getError() + " Le volume doit être un entier compris entre 0 et 150.").queue();
        else {
            handler.getPlayer().setVolume(nvolume);
            settings.setVolume(nvolume);
            event.reply(FormatUtil.volumeIcon(nvolume) + " Le volume est passé de `" + volume + "` à `" + nvolume + "`.").queue();
            log.info(event.getGuild().getName() + "le volume est passé de " + volume + " à " + nvolume + ".");
        }
    }
}
