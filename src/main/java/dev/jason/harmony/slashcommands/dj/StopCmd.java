package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.audio.QueuedTrack;
import com.jason.harmony.queue.FairQueue;
import dev.jason.harmony.playlist.CacheLoader;
import dev.jason.harmony.slashcommands.DJCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StopCmd extends DJCommand {
    Logger log = LoggerFactory.getLogger("Stop");

    public StopCmd(Bot bot) {
        super(bot);
        this.name = "stop";
        this.help = "Arrête la chanson en cours et supprime la file d'attente.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "option", "Entrez `save` pour enregistrer la liste de lecture", false));

        this.options = options;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        CacheLoader cache = bot.getCacheLoader();
        FairQueue<QueuedTrack> queue = handler.getQueue();

        if (queue.size() > 0 && event.getArgs().matches("save")) {
            cache.Save(event.getGuild().getId(), handler.getQueue());
            event.reply(event.getClient().getSuccess() + " En attente de lecture " + queue.size() + "Chanson enregistrée et arrêtée.");
            log.info(event.getGuild().getName() + "Enregistré la file d'attente de lecture et déconnecté du canal vocal.");
        } else {
            event.reply(event.getClient().getSuccess() + "La file d'attente de lecture a été supprimée et la lecture a cessé.");
        }
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        CacheLoader cache = bot.getCacheLoader();
        FairQueue<QueuedTrack> queue = handler.getQueue();

        log.debug("再生待ちのサイズ：" + queue.size());

        if (event.getOption("option") == null) {
            event.reply(event.getClient().getSuccess() + "La file d'attente de lecture a été supprimée et la lecture a cessé.").queue();
            log.info(event.getGuild().getName() + "Suppression de la file d'attente de lecture et déconnexion du canal vocal.");
            handler.stopAndClear();
            event.getGuild().getAudioManager().closeAudioConnection();
            return;
        }

        if (queue.size() > 0 && event.getOption("option").getAsString().equals("save")) {
            cache.Save(event.getGuild().getId(), handler.getQueue());
            event.reply(event.getClient().getSuccess() + " En attente de lecture " + queue.size() + "Chanson enregistrée et arrêtée.").queue();
            log.info(event.getGuild().getName() + "Enregistré la file d'attente de lecture et déconnecté du canal vocal.");
        } else {
            event.reply(event.getClient().getSuccess() + "La file d'attente de lecture a été supprimée et la lecture a cessé.").queue();
            log.info(event.getGuild().getName() + "Suppression de la file d'attente de lecture et déconnexion du canal vocal.");
        }
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        String[] cmdOptions = {"save"};
        if (event.getName().equals("stop") && event.getFocusedOption().getName().equals("option")) {
            List<Command.Choice> options = Stream.of(cmdOptions)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
