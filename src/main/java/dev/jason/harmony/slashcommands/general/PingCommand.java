package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;

import java.time.temporal.ChronoUnit;

@CommandInfo(
        name = {"Ping", "Pong"},
        description = "Vérifier la latence du bot"
)
public class PingCommand extends SlashCommand {

    public PingCommand() {
        this.name = "ping";
        this.help = "Vérifier la latence du bot";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply("Ping: ...").queue(m -> {
            m.editOriginal("Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

}
