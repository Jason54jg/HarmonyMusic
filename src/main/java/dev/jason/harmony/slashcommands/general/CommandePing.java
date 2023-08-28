package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import java.time.temporal.ChronoUnit;

@CommandInfo(
        name = {"Ping"},
        description = "Vérifie la latence du bot"
)
public class CommandePing extends SlashCommand {

    public CommandePing() {
        this.name = "ping";
        this.help = "Vérifie la latence du bot";
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply("Ping : ...").queue(m -> {
            m.editOriginal("Websocket : " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping : ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping : " + ping + "ms | Websocket : " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

}
