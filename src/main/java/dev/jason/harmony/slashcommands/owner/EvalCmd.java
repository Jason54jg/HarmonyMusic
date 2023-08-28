package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.List;

public class EvalCmd extends OwnerCommand {
    private final Bot bot;

    public EvalCmd(Bot bot) {
        this.bot = bot;
        this.name = "eval";
        this.help = "Exécuter le code Nashorn";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "code", "Code à exécuter", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("bot", bot);
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("channel", event.getChannel());
        try {
            event.reply(event.getClient().getSuccess() + " Exécution réussie:\n```\n" + se.eval(event.getOption("code").getAsString()) + " ```").queue();
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Une exception s'est produite\n```\n" + e + " ```").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("bot", bot);
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("channel", event.getChannel());
        try {
            event.reply(event.getClient().getSuccess() + " Exécution réussie:\n```\n" + se.eval(event.getArgs()) + " ```");
        } catch (Exception e) {
            event.reply(event.getClient().getError() + " Une exception s'est produite\n```\n" + e + " ```");
        }
    }
}
