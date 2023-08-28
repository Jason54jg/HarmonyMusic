package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class CommandeListeServeurs extends OwnerCommand {
    protected Bot bot;

    public CommandeListeServeurs(Bot bot) {
        this.name = "listeserveurs";
        this.help = "Affiche la liste des serveurs où le bot est présent.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        List<Guild> guilds = event.getJDA().getGuilds();

        StringBuilder stringBuilder = new StringBuilder();
        for (Guild guild : guilds) {
            stringBuilder.append(guild.getName()).append(" - ").append(guild.getId()).append("\n");
        }

        event.reply(stringBuilder.toString()).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Guild> guilds = event.getJDA().getGuilds();

        StringBuilder stringBuilder = new StringBuilder();
        for (Guild guild : guilds) {
            stringBuilder.append(guild.getName()).append(" - ").append(guild.getId()).append("\n");
        }

        event.reply(stringBuilder.toString());
    }
}
