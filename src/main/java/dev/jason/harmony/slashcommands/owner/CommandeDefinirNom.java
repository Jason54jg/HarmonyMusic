package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandeDefinirNom extends OwnerCommand {
    protected Bot bot;

    public CommandeDefinirNom(Bot bot) {
        this.bot = bot;
        this.name = "definirnom";
        this.help = "Définit le nom du bot.";
        this.arguments = "<nom>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "nom", "nouveau nom du bot", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            String ancienNom = event.getJDA().getSelfUser().getName();
            event.getJDA().getSelfUser().getManager().setName(event.getOption("nom").getAsString()).complete(false);
            event.reply(event.getClient().getSuccess() + "Le bot a été renommé de `" + ancienNom + "` à `" + event.getOption("nom").getAsString() + "`.").queue();
        } catch (RateLimitedException e) {
            event.reply(event.getClient().getError() + "Vous ne pouvez changer de nom que deux fois par heure.").queue();
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Ce nom n'est pas autorisé.").queue();
        }
    }
}
