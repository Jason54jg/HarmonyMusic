package dev.jason.harmony.util;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class LastSendTextChannel implements CommandListener {
    // Apportez l'identifiant du canal de texte avec l'identifiant de la guilde.
    private static final HashMap<Long, Long> textChannel = new HashMap<>();
    static Logger log = LoggerFactory.getLogger("LastSendTextChannel");

    public static void SetLastTextId(CommandEvent event) {
        textChannel.put(event.getGuild().getIdLong(), event.getTextChannel().getIdLong());
    }

    public static long GetLastTextId(long guildId) {
        long id;
        if (textChannel.containsKey(guildId)) {
            id = textChannel.get(guildId);
        } else {
            id = 0;
        }
        return id;
    }

    public static void SendMessage(Guild guild, String message) {
        log.debug("Envoyer un message.");
        long textId = GetLastTextId(guild.getIdLong());
        if (textId == 0) {
            log.debug("Le message n'a pas pu être envoyé car la chaîne n'a pas été enregistrée.");
            return;
        }
        MessageChannel channel = guild.getTextChannelById(textId);
        channel.sendMessage(message).queue();
    }
}
