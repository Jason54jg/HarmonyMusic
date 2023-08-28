package dev.jason.harmony.slashcommands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.slashcommands.AdminCommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SetdjCmd extends AdminCommand {
    public SetdjCmd(Bot bot) {
        this.name = "setdj";
        this.help = "Définir le rôle DJ qui peut utiliser les commandes du bot.";
        this.arguments = "<rôle|aucun>";
        this.aliases = bot.getConfig().getAliases(this.name);

        // Définir les sous-commandes pour la commande slash
        this.children = new SlashCommand[]{new SetRole(), new None()};
    }

    // Méthode d'exécution pour les commandes slash
    @Override
    protected void execute(SlashCommandEvent event) {
        // Vérifier les permissions d'administrateur
        if (checkAdminPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible de s'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }

        Settings s = event.getClient().getSettingsFor(event.getGuild());

        // Vérifier si l'option "role" a été fournie
        if (event.getOption("role") != null) {
            s.setDJRole(event.getOption("role").getAsRole());
            event.reply(event.getClient().getSuccess() + "La commande DJ est maintenant disponible pour les utilisateurs ayant le rôle **" + event.getOption("role").getAsRole().getName() + "**.").queue();
            return;
        }

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (event.getOption("aucun").getAsString().toLowerCase().matches("(aucun)")) {
            s.setDJRole(null);
            event.reply(event.getClient().getSuccess() + "Le rôle de DJ a été réinitialisé. Seuls les administrateurs peuvent utiliser les commandes DJ.").queue();
        } else {
            event.reply("Commande incorrecte.").queue();
        }
    }

    // Méthode d'exécution pour les commandes non-slash (précédente version)
    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("SetDjCmd");
        if (event.getArgs().isEmpty()) {
            event.reply(event.getClient().getError() + "Indiquez le nom du rôle, ou saisissez \"aucun\".");
            return;
        }

        Settings s = event.getClient().getSettingsFor(event.getGuild());

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (event.getArgs().toLowerCase().matches("(aucun)")) {
            s.setDJRole(null);
            event.reply(event.getClient().getSuccess() + "Le rôle de DJ a été réinitialisé. Seuls les administrateurs peuvent utiliser les commandes DJ.");
        } else {
            List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
            if (list.isEmpty())
                event.reply(event.getClient().getWarning() + "Rôle introuvable : \"" + event.getArgs() + "\".");
            else if (list.size() > 1)
                event.reply(event.getClient().getWarning() + FormatUtil.listOfRoles(list, event.getArgs()));
            else {
                s.setDJRole(list.get(0));
                log.info("Un rôle pouvant utiliser les commandes DJ a été ajouté.(" + list.get(0).getName() + ")");
                event.reply(event.getClient().getSuccess() + "La commande DJ est maintenant disponible pour les utilisateurs ayant le rôle **" + list.get(0).getName() + "**.");
            }
        }
    }

    // Sous-commande pour définir le rôle DJ via la commande slash
    private static class SetRole extends AdminCommand {
        public SetRole() {
            this.name = "set";
            this.help = "Configurez un rôle qui accorde des privilèges DJ.";

            // Définir les options pour la sous-commande slash
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.ROLE, "role", "rôle pour définir le rôle DJ du serveur, laissez vide pour effacer le rôle DJ", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            Role role = event.getOption("role").getAsRole();

            s.setDJRole(role);
            event.reply(event.getClient().getSuccess() + "La commande DJ est maintenant disponible pour les utilisateurs ayant le rôle **" + role.getName() + "**.").queue();
        }
    }

    // Sous-commande pour réinitialiser le rôle DJ via la commande slash
    private static class None extends AdminCommand {
        public None() {
            this.name = "aucun";
            this.help = "Réinitialiser le rôle DJ";
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setDJRole(null);
            event.reply(event.getClient().getSuccess() + "Le rôle de DJ a été réinitialisé. Seuls les administrateurs peuvent utiliser les commandes DJ.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setDJRole(null);
            event.replySuccess("Le rôle de DJ a été réinitialisé. Seuls les administrateurs peuvent utiliser les commandes DJ.");
        }
    }
}
