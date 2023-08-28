package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetstatusCmd extends OwnerCommand {
    public SetstatusCmd(Bot bot) {
        this.name = "setstatus";
        this.help = "Définit le statut que le bot affichera";
        this.arguments = "<status>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "status", "l'un des statuts suivants：ONLINE, IDLE, DND, INVISIBLE", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            OnlineStatus status = OnlineStatus.fromKey(event.getOption("status").getAsString());
            if (status == OnlineStatus.UNKNOWN) {
                event.reply(event.getClient().getError() + "Incluez l'un des statuts suivants: :`ONLINE`, `IDLE`, `DND`, `INVISIBLE`").queue();
            } else {
                event.getJDA().getPresence().setStatus(status);
                event.reply(event.getClient().getSuccess() + "Définir le statut sur `" + status.getKey().toUpperCase() + "`.").queue();
            }
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Échec de la définition de l'état.").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            OnlineStatus status = OnlineStatus.fromKey(event.getArgs());
            if (status == OnlineStatus.UNKNOWN) {
                event.replyError("Incluez l'un des statuts suivants: :`ONLINE`, `IDLE`, `DND`, `INVISIBLE`");
            } else {
                event.getJDA().getPresence().setStatus(status);
                event.replySuccess("Définir le statut sur `" + status.getKey().toUpperCase() + "`.");
            }
        } catch (Exception e) {
            event.reply(event.getClient().getError() + "Impossible de définir le statut.");
        }
    }
}
