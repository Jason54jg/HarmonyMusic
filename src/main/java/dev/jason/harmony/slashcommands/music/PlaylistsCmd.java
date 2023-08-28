package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.MusicCommand;

import java.util.List;

public class PlaylistsCmd extends MusicCommand {
    public PlaylistsCmd(Bot bot) {
        super(bot);
        this.name = "playlists";
        this.help = "Afficher les listes de lecture disponibles";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
        this.beListening = false;
    }

    @Override
    public void doCommand(CommandEvent event) {
        String guildID = event.getGuild().getId();
        if (!bot.getPlaylistLoader().folderExists())
            bot.getPlaylistLoader().createFolder();
        if (!bot.getPlaylistLoader().folderGuildExists(guildID))
            bot.getPlaylistLoader().createGuildFolder(guildID);
        if (!bot.getPlaylistLoader().folderExists()) {
            event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.");
            return;
        }
        if (!bot.getPlaylistLoader().folderGuildExists(guildID)) {
            event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture pour ce serveur n'existe pas et n'a pas pu être créé.");
            return;
        }
        List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildID);
        if (list == null)
            event.reply(event.getClient().getError() + " Échec du chargement des listes de lecture disponibles.");
        else if (list.isEmpty())
            event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans le dossier de listes de lecture.");
        else {
            StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " liste de lecture disponible:\n");
            list.forEach(str -> builder.append("`").append(str).append("` "));
            builder.append("\n`").append(event.getClient().getTextualPrefix()).append("play playlist <name>` Vous pouvez lire la liste de lecture en tapant");
            event.reply(builder.toString());
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        String guildID = event.getGuild().getId();
        if (!bot.getPlaylistLoader().folderExists())
            bot.getPlaylistLoader().createFolder();
        if (!bot.getPlaylistLoader().folderGuildExists(guildID))
            bot.getPlaylistLoader().createGuildFolder(guildID);
        if (!bot.getPlaylistLoader().folderExists()) {
            event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.").queue();
            return;
        }
        if (!bot.getPlaylistLoader().folderGuildExists(guildID)) {
            event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture pour ce serveur n'existe pas et n'a pas pu être créé.").queue();
            return;
        }
        List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildID);
        if (list == null)
            event.reply(event.getClient().getError() + " Échec du chargement des listes de lecture disponibles.").queue();
        else if (list.isEmpty())
            event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans le dossier de listes de lecture.").queue();
        else {
            StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " liste de lecture disponible:\n");
            list.forEach(str -> builder.append("`").append(str).append("` "));
            builder.append("\n`").append(event.getClient().getTextualPrefix()).append("play playlist <name>` Vous pouvez lire la liste de lecture en tapant");
            event.reply(builder.toString()).queue();
        }
    }
}
