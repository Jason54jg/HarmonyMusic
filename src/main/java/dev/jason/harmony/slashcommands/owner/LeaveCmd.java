package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class LeaveCmd extends OwnerCommand {
    private final Bot bot;

    public LeaveCmd(Bot bot) {
        this.bot = bot;
        this.name = "leave";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "serverid", "Identifiant du serveur", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String id = event.getOption("serverid").getAsString();
        event.getJDA().getGuildById(id).leave().queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.reply(event.getClient().getError() + "Donnez-lui le nom du rôle, ou quelque chose comme AUCUN.");
            return;
        }

        event.getJDA().getGuildById(event.getArgs()).leave().queue();
    }
}
