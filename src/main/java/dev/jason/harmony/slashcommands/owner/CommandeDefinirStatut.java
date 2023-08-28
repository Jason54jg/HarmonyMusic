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

public class CommandeDefinirStatut extends OwnerCommand {
    public CommandeDefinirStatut(Bot bot) {
        this.name = "definirstatut";
        this.help = "Définit le statut que le bot affichera";
        this.arguments = "<statut>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "statut", "l'un des statuts suivants : ONLINE, IDLE, DND, INVISIBLE", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            OnlineStatus statut = OnlineStatus.fromKey(event.getOption("statut").getAsString());
            if (statut == OnlineStatus.UNKNOWN) {
                event.reply(event.getClient().getError() + "Incluez l'un des statuts suivants : `ONLINE`, `IDLE`, `DND`, `INVISIBLE`").queue();
            } else {
                event.getJDA().getPresence().setStatus(statut);
                event.reply(event.getClient().getSuccess() + "Statut défini sur `" + statut.getKey().toUpperCase() + "`.").queue();
            }
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Échec de la définition du statut.").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            OnlineStatus statut = OnlineStatus.fromKey(event.getArgs());
            if (statut == OnlineStatus.UNKNOWN) {
                event.replyError("Incluez l'un des statuts suivants : `ONLINE`, `IDLE`, `DND`, `INVISIBLE`");
            } else {
                event.getJDA().getPresence().setStatus(statut);
                event.replySuccess("Statut défini sur `" + statut.getKey().toUpperCase() + "`.");
            }
        } catch (Exception e) {
            event.reply(event.getClient().getError() + "Impossible de définir le statut.");
        }
    }
}
