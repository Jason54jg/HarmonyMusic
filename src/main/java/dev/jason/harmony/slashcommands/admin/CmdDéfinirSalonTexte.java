package dev.jason.harmony.slashcommands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.slashcommands.AdminCommand;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CmdDéfinirSalonTexte extends AdminCommand {
    public CmdDéfinirSalonTexte(Bot bot) {
        this.name = "definirsalontexte";
        this.help = "Définir le canal de commande du bot";
        this.arguments = "<Canal|aucun>";
        this.aliases = bot.getConfig().getAliases(this.name);

        // Définir les sous-commandes pour la commande slash
        this.children = new SlashCommand[]{new Set(), new None()};
    }

    // Méthode d'exécution pour les commandes slash
    @Override
    protected void execute(SlashCommandEvent event) {
        // Non implémenté dans cet exemple
    }

    // Méthode d'exécution pour les commandes normales (précédente version)
    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("SettcCmd");
        if (event.getArgs().isEmpty()) {
            event.reply(event.getClient().getError() + "Veuillez inclure le canal ou AUCUN.");
            return;
        }

        Settings s = event.getClient().getSettingsFor(event.getGuild());

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (event.getArgs().toLowerCase().matches("(aucun)")) {
            s.setTextChannel(null);
            event.reply(event.getClient().getSuccess() + "Les commandes musicales peuvent maintenant être utilisées sur n'importe quel canal.");
        } else {
            List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
            if (list.isEmpty())
                event.reply(event.getClient().getWarning() + "Aucun canal correspondant trouvé pour \"" + event.getArgs() + "\".");
            else if (list.size() > 1)
                event.reply(event.getClient().getWarning() + FormatUtil.listOfTChannels(list, event.getArgs()));
            else {
                s.setTextChannel(list.get(0));
                log.info("J'ai configuré un canal pour les commandes musicales.");
                event.reply(event.getClient().getSuccess() + "Les commandes musicales ne peuvent désormais être utilisées que dans <#" + list.get(0).getId() + ">.");
            }
        }
    }

    // Sous-commande pour définir le canal de commande via la commande slash
    private static class Set extends AdminCommand {
        public Set() {
            this.name = "set";
            this.help = "Définir le canal pour les commandes musicales";

            // Définir les options pour la sous-commande slash
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.CHANNEL, "channel", "canal pour autoriser les commandes musicales, laissez vide pour effacer le canal", true));

            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            // Vérifier les permissions d'administrateur
            if (checkAdminPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());

            // Vérifier le type de canal
            if (event.getOption("channel").getChannelType() != ChannelType.TEXT) {
                event.reply(event.getClient().getError() + "Configurez un canal de texte.").queue();
                return;
            }

            Long channelId = event.getOption("channel").getAsLong();
            TextChannel tc = event.getGuild().getTextChannelById(channelId);

            s.setTextChannel(tc);
            event.reply(client.getSuccess() + "Les commandes musicales ne peuvent désormais être utilisées que dans <#" + tc.getId() + ">.").queue();
        }
    }

    // Sous-commande pour réinitialiser le canal de commande via la commande slash
    private static class None extends AdminCommand {
        public None() {
            this.name = "aucun";
            this.help = "Désactivez le paramètre de canal pour les commandes musicales.";
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            // Vérifier les permissions d'administrateur
            if (checkAdminPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setTextChannel(null);
            event.reply(event.getClient().getSuccess() + "Les commandes musicales peuvent maintenant être utilisées sur n'importe quel canal.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setTextChannel(null);
            event.replySuccess("Les commandes musicales peuvent maintenant être utilisées sur n'importe quel canal.");
        }
    }
}
