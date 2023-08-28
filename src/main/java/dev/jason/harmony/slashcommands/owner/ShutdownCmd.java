package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;

public class ShutdownCmd extends OwnerCommand {
    private final Bot bot;

    public ShutdownCmd(Bot bot) {
        this.bot = bot;
        this.name = "shutdown";
        this.help = "fermer en toute sécurité";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply(event.getClient().getWarning() + "Arrêt en cours...\nIl se peut qu'il ne puisse pas s'arrêter normalement en raison d'un problème. Dans ce cas, veuillez arrêter le bot de force.").queue();
        bot.shutdown();
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyWarning("Arrêt en cours...\nIl se peut qu'il ne puisse pas s'arrêter normalement en raison d'un problème. Dans ce cas, veuillez arrêter le bot de force.");
        bot.shutdown();
    }
}
