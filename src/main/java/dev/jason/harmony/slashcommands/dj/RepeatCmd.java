package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import dev.jason.harmony.settings.RepeatMode;
import dev.jason.harmony.slashcommands.DJCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatCmd extends DJCommand {
    Logger log = LoggerFactory.getLogger("Repeat");

    public RepeatCmd(Bot bot) {
        super(bot);
        this.name = "repeat";
        this.help = "Ajoutez à nouveau la chanson lorsque la chanson en attente a fini de jouer";
        this.arguments = "[all|on|single|one|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;

        this.children = new SlashCommand[]{new SingleCmd(bot), new AllCmd(bot), new OffCmd(bot)};

    }

    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) {
        RepeatMode value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        String args = event.getArgs();

        if (args.isEmpty()) {
            log.info("Mode de lecture avant changement:" + settings.getRepeatMode());
            value = (settings.getRepeatMode() == RepeatMode.OFF ? RepeatMode.ALL : (settings.getRepeatMode() == RepeatMode.ALL ? RepeatMode.SINGLE : (settings.getRepeatMode() == RepeatMode.SINGLE ? RepeatMode.OFF : settings.getRepeatMode())));
        } else if (args.equalsIgnoreCase("true") || args.equalsIgnoreCase("all") || args.equalsIgnoreCase("on")) {
            value = RepeatMode.ALL;
        } else if (args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off")) {
            value = RepeatMode.OFF;
        } else if (args.equalsIgnoreCase("one") || args.equalsIgnoreCase("single")) {
            value = RepeatMode.SINGLE;
        } else {
            event.replyError("Les options valides sont\n" +
                    "```\n" +
                    "répéter tout: true, all, on\n" +
                    "Une répétition: one, single\n" +
                    "répéter désactivé: false, off" +
                    "```\n" +
                    "est\n" +
                    "(ou vous pouvez changer sans options)");
            return;
        }

        settings.setRepeatMode(value);
        log.info(event.getGuild().getName() + "Exécuté une commande de répétition sur et définissez le paramètre sur " + value  + ".");
        event.replySuccess("Repeat is set to `" + (value == RepeatMode.ALL ? "Enable (répéter toutes les chansons)" : (value == RepeatMode.SINGLE ? "Enable (répéter 1 chanson)" : "Disable")) + "` .");
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }

    @Override
    public void doCommand(SlashCommandEvent event) {
    }

    private class SingleCmd extends DJCommand {
        public SingleCmd(Bot bot) {
            super(bot);
            this.name = "single";
            this.help = "Passez en mode de répétition 1 piste.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.SINGLE);
            event.reply("La répétition est réglée sur ``activé (répéter 1 chanson)''.").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }

    private class AllCmd extends DJCommand {
        public AllCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.help = "Passez en mode de répétition de toutes les pistes.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.ALL);
            event.reply("La répétition est réglée sur `activé (répéter toutes les chansons)`.").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }

    private class OffCmd extends DJCommand {
        public OffCmd(Bot bot) {
            super(bot);
            this.name = "off";
            this.help = "Désactiver la répétition.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.OFF);
            event.reply("Répétition désactivée.").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }
}
