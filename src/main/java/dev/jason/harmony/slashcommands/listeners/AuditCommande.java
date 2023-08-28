package dev.jason.harmony.slashcommands.listeners;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import com.jason.harmony.Harmony;
import dev.jason.harmony.util.LastSendTextChannel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditCommande implements CommandListener {
    @Override
    public void onCommand(CommandEvent event, Command command) {
        if (Harmony.COMMAND_AUDIT_ENABLED) {
            Logger logger = LoggerFactory.getLogger("AuditCommande");
            String formatTexte = event.isFromType(ChannelType.PRIVATE) ? "%s#%s (%s) a exécuté la commande %s sur %s%s" : "%s#%s (%s) a exécuté la commande %s sur #%s sur %s";

            logger.info(String.format(formatTexte,
                    event.isFromType(ChannelType.PRIVATE) ? "DM" : event.getGuild().getName(),
                    event.isFromType(ChannelType.PRIVATE) ? "" : event.getTextChannel().getName(),
                    event.getAuthor().getName(), event.getAuthor().getDiscriminator(), event.getAuthor().getId(),
                    event.getMessage().getContentDisplay()));
        }

        LastSendTextChannel.SetLastTextId(event);
    }
}
