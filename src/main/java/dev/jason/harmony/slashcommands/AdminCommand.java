package dev.jason.harmony.slashcommands;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;

public abstract class AdminCommand extends SlashCommand {
    public AdminCommand() {
        this.category = new Category("Admin", event ->
        {
            if (event.isOwner() || event.getMember().isOwner())
                return true;
            if (event.getGuild() == null)
                return true;
            return event.getMember().hasPermission(Permission.MANAGE_SERVER);
        });
        this.guildOnly = true;
    }

    public static boolean checkAdminPermission(CommandClient client, SlashCommandEvent event) {
        if (event.getUser().getId().equals(client.getOwnerId()) || event.getMember().isOwner())
            return false;
        if (event.getGuild() == null)
            return false;
        return !event.getMember().hasPermission(Permission.MANAGE_SERVER);
    }
}
