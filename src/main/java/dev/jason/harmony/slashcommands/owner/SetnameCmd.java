package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetnameCmd extends OwnerCommand {
    protected Bot bot;

    public SetnameCmd(Bot bot) {
        this.bot = bot;
        this.name = "setname";
        this.help = "Définit le nom du bot.";
        this.arguments = "<name>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "nouveau nom de robot", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            String oldname = event.getJDA().getSelfUser().getName();
            event.getJDA().getSelfUser().getManager().setName(event.getOption("name").getAsString()).complete(false);
            event.reply(event.getClient().getSuccess() + "Bot renommé de `" + oldname + "` à `" + event.getOption("name").getAsString() + "`.").queue();
        } catch (RateLimitedException e) {
            event.reply(event.getClient().getError() + "Vous ne pouvez changer de nom que deux fois par heure.").queue();
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Ce nom n'est pas autorisé.").queue();
        }
    }
}
