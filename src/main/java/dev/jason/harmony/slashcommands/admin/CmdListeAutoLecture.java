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

public class CmdListeAutoLecture extends AdminCommand {
    private final Bot bot;

    // Constructeur de la commande de gestion de la liste de lecture automatique
    public CmdListeAutoLecture(Bot bot) {
        this.bot = bot;
        this.guildOnly = true;
        this.name = "listeautolecture";
        this.arguments = "<nom|aucun>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.help = "Configurer la liste de lecture automatique du serveur";
        this.ownerCommand = false;

        // Définir les options pour la commande slash
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "nom", "nom de la liste de lecture", true));

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

        // Récupérer le nom de la liste de lecture à partir de l'option
        String pName = event.getOption("nom").getAsString();

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (pName.toLowerCase().matches("(aucun)")) {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(null);
            event.reply(event.getClient().getSuccess() + "**" + event.getGuild().getName() + "** Liste de lecture automatique définie sur Aucun.").queue();
            return;
        }

        // Vérifier si la liste de lecture existe
        if (bot.getPlaylistLoader().getPlaylist(event.getGuild().getId(), pName) == null) {
            event.reply(event.getClient().getError() + "Impossible de trouver `" + pName + "` !").queue();
        } else {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(pName);
            event.reply(event.getClient().getSuccess() + "**" + event.getGuild().getName() + "** a défini la liste de lecture automatique sur `" + pName + "`.\n"
                    + "Lorsqu'il n'y a pas de chansons dans la file d'attente de lecture, les chansons de la liste de lecture automatique seront lues.").queue();
        }
    }

    // Méthode d'exécution pour les commandes non-slash (version précédente)
    @Override
    public void execute(CommandEvent event) {
        // Vérifier si l'utilisateur est le propriétaire du bot
        if (!event.isOwner() || !event.getMember().isOwner()) return;

        // Récupérer l'identifiant du serveur
        String guildId = event.getGuild().getId();

        // Gérer les différents cas pour les arguments fournis
        if (event.getArgs().isEmpty()) {
            event.reply(event.getClient().getError() + " Veuillez inclure un nom de liste de lecture ou AUCUN.");
            return;
        }
        if (event.getArgs().toLowerCase().matches("(aucun)")) {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(null);
            event.reply(event.getClient().getSuccess() + "**" + event.getGuild().getName() + "** Liste de lecture automatique définie sur Aucun.");
            return;
        }
        String pName = event.getArgs().replaceAll("\\s+", "_");
        if (bot.getPlaylistLoader().getPlaylist(guildId, pName) == null) {
            event.reply(event.getClient().getError() + "Impossible de trouver `" + pName + "` !");
        } else {
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setDefaultPlaylist(pName);
            event.reply(event.getClient().getSuccess() + "**" + event.getGuild().getName() + "** a défini la liste de lecture automatique sur `" + pName + "`.\n"
                    + "Lorsqu'il n'y a pas de chansons dans la file d'attente de lecture, les chansons de la liste de lecture automatique seront lues.");
        }
    }
}
