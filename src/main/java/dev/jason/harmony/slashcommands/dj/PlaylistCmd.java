package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.playlist.PlaylistLoader.Playlist;
import dev.jason.harmony.slashcommands.DJCommand;
import dev.jason.harmony.util.StackTraceUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistCmd extends DJCommand {

    public PlaylistCmd(Bot bot) {
        super(bot);
        this.guildOnly = true;
        this.name = "playlist";
        this.arguments = "<append|delete|make>";
        this.help = "Gestion des listes de lecture";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new DJCommand[]{
                new ListCmd(bot),
                new AppendlistCmd(bot),
                new DeletelistCmd(bot),
                new MakelistCmd(bot)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {

        StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Commandes de gestion des listes de lecture:\n");
        for (Command cmd : this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    @Override
    public void doCommand(SlashCommandEvent slashCommandEvent) {
        // pas exécuté ici.
    }

    public class MakelistCmd extends DJCommand {
        public MakelistCmd(Bot bot) {
            super(bot);
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "Créer une nouvelle liste de lecture";
            this.arguments = "<name>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String pName = event.getArgs().replaceAll("\\s+", "_");
            String guildId = event.getGuild().getId();

            if (pName == null || pName.isEmpty()) {
                event.replyError("Veuillez saisir un nom de liste de lecture.");
            } else if (bot.getPlaylistLoader().getPlaylist(guildId, pName) == null) {
                try {
                    bot.getPlaylistLoader().createPlaylist(guildId, pName);
                    event.reply(event.getClient().getSuccess() + "liste de lecture créée `" + pName + "`");
                } catch (IOException e) {
                    if (event.isOwner() || event.getMember().isOwner()) {
                        event.replyError("Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "**Contenu de l'erreur: " + e.getLocalizedMessage() + "**");
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Échec de la création de la liste de lecture. :" + e.getLocalizedMessage());
                }
            } else {
                event.reply(event.getClient().getError() + "Playlist `" + pName + "` existe déjà");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            String pname = event.getOption("name").getAsString();
            String guildId = event.getGuild().getId();
            if (pname == null || pname.isEmpty()) {
                event.reply(event.getClient().getError() + "Veuillez saisir un nom de liste de lecture.").queue();
            } else if (bot.getPlaylistLoader().getPlaylist(guildId, pname) == null) {
                try {
                    bot.getPlaylistLoader().createPlaylist(guildId, pname);
                    event.reply(event.getClient().getSuccess() + "liste de lecture créée `" + pname + "`").queue();
                } catch (IOException e) {
                    if (event.getClient().getOwnerId() == event.getMember().getId() || event.getMember().isOwner()) {
                        event.reply(event.getClient().getError() + "Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "** Contenu de l'erreur:" + e.getLocalizedMessage() + "**").queue();
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Échec de la création de la liste de lecture.:" + e.getLocalizedMessage()).queue();
                }
            } else {
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` existe déjà").queue();
            }
        }
    }

    public class DeletelistCmd extends DJCommand {
        public DeletelistCmd(Bot bot) {
            super(bot);
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Supprimer la liste de lecture existante";
            this.arguments = "<name>";
            this.guildOnly = true;
            this.ownerCommand = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String pname = event.getArgs().replaceAll("\\s+", "_");
            String guildid = event.getGuild().getId();
            if (!pname.equals("")) {
                if (bot.getPlaylistLoader().getPlaylist(guildid, pname) == null)
                    event.reply(event.getClient().getError() + " la liste de lecture n'existe pas:`" + pname + "`");
                else {
                    try {
                        bot.getPlaylistLoader().deletePlaylist(guildid, pname);
                        event.reply(event.getClient().getSuccess() + " Liste de lecture supprimée :`" + pname + "`");
                    } catch (IOException e) {
                        event.reply(event.getClient().getError() + " Échec de la suppression de la liste de lecture: " + e.getLocalizedMessage());
                    }
                }
            } else {
                event.reply(event.getClient().getError() + "Inclure le nom de la liste de lecture");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            String pname = event.getOption("name").getAsString();
            String guildid = event.getGuild().getId();
            if (bot.getPlaylistLoader().getPlaylist(guildid, pname) == null)
                event.reply(event.getClient().getError() + " la liste de lecture n'existe pas:`" + pname + "`").queue();
            else {
                try {
                    bot.getPlaylistLoader().deletePlaylist(guildid, pname);
                    event.reply(event.getClient().getSuccess() + " Liste de lecture supprimée:`" + pname + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de la suppression de la liste de lecture: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public class AppendlistCmd extends DJCommand {
        public AppendlistCmd(Bot bot) {
            super(bot);
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "Ajouter des chansons à la liste de lecture existante";
            this.arguments = "<name> <URL>| <URL> | ...";
            this.guildOnly = true;
            this.ownerCommand = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            options.add(new OptionData(OptionType.STRING, "url", "URL", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String[] parts = event.getArgs().split("\\s+", 2);
            String guildid = event.getGuild().getId();
            if (parts.length < 2) {
                event.reply(event.getClient().getError() + " Incluez le nom de la liste de lecture et l'URL à ajouter.");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(guildid, pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + " la liste de lecture n'existe pas:`" + pname + "`");
            else {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = parts[1].split("\\|");
                for (String url : urls) {
                    String u = url.trim();
                    if (u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length() - 1);
                    builder.append("\r\n").append(u);
                }
                try {
                    bot.getPlaylistLoader().writePlaylist(guildid, pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Élément ajouté à la liste de lecture :`" + pname + "`");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste de lecture: " + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }

            String guildid = event.getGuild().getId();
            String pname = event.getOption("name").getAsString();
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(guildid, pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + " la liste de lecture n'existe pas :`" + pname + "`").queue();
            else {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = event.getOption("url").getAsString().split("\\|");
                for (String url : urls) {
                    String u = url.trim();
                    if (u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length() - 1);
                    builder.append("\r\n").append(u);
                }
                try {
                    bot.getPlaylistLoader().writePlaylist(guildid, pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Élément ajouté à la liste de lecture:`" + pname + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste de lecture: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public class ListCmd extends DJCommand {
        public ListCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.aliases = new String[]{"available", "list"};
            this.help = "Afficher toutes les listes de lecture disponibles";
            this.guildOnly = true;
            this.ownerCommand = false;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String guildId = event.getGuild().getId();

            if (!bot.getPlaylistLoader().folderGuildExists(guildId))
                bot.getPlaylistLoader().createGuildFolder(guildId);
            if (!bot.getPlaylistLoader().folderGuildExists(guildId)) {
                event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildId);
            if (list == null)
                event.reply(event.getClient().getError() + " Échec du chargement des listes de lecture disponibles.");
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans le dossier de listes de lecture.");
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " liste de lecture disponible:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }
            String guildId = event.getGuild().getId();
            if (!bot.getPlaylistLoader().folderGuildExists(guildId))
                bot.getPlaylistLoader().createGuildFolder(guildId);
            if (!bot.getPlaylistLoader().folderGuildExists(guildId)) {
                event.reply(event.getClient().getWarning() + " Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.").queue();
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildId);
            if (list == null)
                event.reply(event.getClient().getError() + " Échec du chargement des listes de lecture disponibles.").queue();
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans le dossier de listes de lecture.").queue();
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " liste de lecture disponible:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString()).queue();
            }
        }
    }
}