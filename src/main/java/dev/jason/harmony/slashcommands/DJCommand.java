package dev.jason.harmony.slashcommands;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DJCommand extends MusicCommand {
    static Logger log = LoggerFactory.getLogger("DJCommand");

    public DJCommand(Bot bot) {
        super(bot);
        this.category = new Category("DJ", DJCommand::checkDJPermission);
    }

    public static boolean checkDJPermission(CommandEvent event) {
        if (event.getAuthor().getId().equals(event.getClient().getOwnerId())) {
            return true;
        }
        if (event.getGuild() == null) {
            return true;
        }
        if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            return true;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        Role dj = settings.getRole(event.getGuild());
        return dj != null && (event.getMember().getRoles().contains(dj) || dj.getIdLong() == event.getGuild().getIdLong());
    }

    public static boolean checkDJPermission(CommandClient client, SlashCommandEvent event) {
        if (event.getUser().getId().equals(client.getOwnerId())) {
            return true;
        }
        if (event.getGuild() == null) {
            return true;
        }
        if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            return true;
        }
        Settings settings = client.getSettingsFor(event.getGuild());
        Role dj = settings.getRole(event.getGuild());
        return dj != null && (event.getMember().getRoles().contains(dj) || dj.getIdLong() == event.getGuild().getIdLong());
    }
}
