package dev.jason.harmony.slashcommands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import dev.jason.harmony.slashcommands.AdminCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CmdPréfixe extends AdminCommand {
    public CmdPréfixe(Bot bot) {
        this.name = "prefix";
        this.help = "Définir un préfixe spécifique pour le serveur";
        this.arguments = "<préfixe|aucun>";
        this.aliases = bot.getConfig().getAliases(this.name);
        //this.children = new SlashCommand[]{new None()};

        // Définir les options pour la commande slash
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "préfixe", "définir le préfixe spécifique pour le serveur, **aucun** pour effacer le préfixe du serveur", true));

        this.options = options;
    }

    // Méthode d'exécution pour les commandes slash
    @Override
    protected void execute(SlashCommandEvent event) {
        // Vérifier les permissions d'administrateur
        if (checkAdminPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible de s'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }

        // Récupérer les paramètres de configuration pour le serveur
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        String prefix = event.getOption("préfixe").getAsString();

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (prefix.toLowerCase().matches("(aucun)")) {
            s.setPrefix(null);
            event.reply(event.getClient().getSuccess() + "Préfixe effacé.").queue();
        } else {
            s.setPrefix(prefix);
            event.reply(event.getClient().getSuccess() + "*" + event.getGuild().getName() + "* a défini le préfixe en `" + prefix + "`.").queue();
        }
    }

    // Méthode d'exécution pour les commandes non-slash (précédente version)
    @Override
    protected void execute(CommandEvent event) {
        // Vérifier si un préfixe a été fourni
        if (event.getArgs().isEmpty()) {
            event.replyError("Veuillez inclure un préfixe ou aucun.");
            return;
        }

        // Récupérer les paramètres de configuration pour le serveur
        Settings s = event.getClient().getSettingsFor(event.getGuild());

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (event.getArgs().toLowerCase().matches("(aucun)")) {
            s.setPrefix(null);
            event.replySuccess("Préfixe effacé.");
        } else {
            s.setPrefix(event.getArgs());
            event.replySuccess("Le préfixe pour *" + event.getGuild().getName() + "* a été défini à `" + event.getArgs() + "`.");
        }
    }
}
