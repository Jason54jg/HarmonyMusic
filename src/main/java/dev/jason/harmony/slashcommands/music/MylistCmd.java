package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.playlist.MylistLoader;
import dev.jason.harmony.slashcommands.DJCommand;
import dev.jason.harmony.slashcommands.MusicCommand;
import dev.jason.harmony.util.StackTraceUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MylistCmd extends MusicCommand {

    public MylistCmd(Bot bot) {
        super(bot);
        this.guildOnly = false;
        this.name = "mylist";
        this.arguments = "<append|delete|make|all>";
        this.help = "Gérez votre propre liste de lecture";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new MusicCommand[]{
                new MakelistCmd(bot),
                new DeletelistCmd(bot),
                new AppendlistCmd(bot),
                new ListCmd(bot)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {

        StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Commandes de gestion de ma liste:\n");
        for (Command cmd : this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    @Override
    public void doCommand(SlashCommandEvent slashCommandEvent) {
    }

    public static class MakelistCmd extends DJCommand {
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
            String userId = event.getAuthor().getId();

            if (pName.isEmpty()) {
                event.replyError("Veuillez spécifier un nom de liste de lecture.");
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + "Créé Ma Liste `" + pName + "`");
                } catch (IOException e) {
                    if (event.isOwner() || event.getMember().isOwner()) {
                        event.replyError("Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "**Contenu de l'erreur: " + e.getLocalizedMessage() + "**");
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Ma liste n'a pas pu être créée.:" + e.getLocalizedMessage());
                }
            } else {
                event.reply(event.getClient().getError() + "liste `" + pName + "` existe déjà");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (pName.isEmpty()) {
                event.reply(event.getClient().getError() + "Veuillez spécifier un nom de liste de lecture.").queue();
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + "mylist `" + pName + "` créé").queue();
                } catch (IOException e) {
                    if (event.getClient().getOwnerId() == event.getMember().getId() || event.getMember().isOwner()) {
                        event.reply(event.getClient().getError() + "Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "**Contenu de l'erreur: " + e.getLocalizedMessage() + "**").queue();
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Ma liste n'a pas pu être créée.:" + e.getLocalizedMessage()).queue();
                }
            } else {
                event.reply(event.getClient().getError() + " liste `" + pName + "` existe déjà").queue();
            }
        }
    }

    public static class DeletelistCmd extends MusicCommand {
        public DeletelistCmd(Bot bot) {
            super(bot);
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Supprimer ma liste existante";
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
            String userId = event.getAuthor().getId();
            if (!pName.equals("")) {
                if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                    event.reply(event.getClient().getError() + " la liste n'existe pas:`" + pName + "`");
                else {
                    try {
                        bot.getMylistLoader().deletePlaylist(userId, pName);
                        event.reply(event.getClient().getSuccess() + " liste supprimée:`" + pName + "`");
                    } catch (IOException e) {
                        event.reply(event.getClient().getError() + " Échec de la suppression de la liste: " + e.getLocalizedMessage());
                    }
                }
            } else {
                event.reply(event.getClient().getError() + "Veuillez inclure le nom de ma liste");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                event.reply(event.getClient().getError() + " la liste n'existe pas:`" + pName + "`").queue();
            else {
                try {
                    bot.getMylistLoader().deletePlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + " liste supprimée:`" + pName + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " マイリストを削除できませんでした: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class AppendlistCmd extends MusicCommand {
        public AppendlistCmd(Bot bot) {
            super(bot);
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "Ajouter des chansons à ma liste existante";
            this.arguments = "<name> <URL> | <URL> | ...";
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
            String userId = event.getAuthor().getId();
            if (parts.length < 2) {
                event.reply(event.getClient().getError() + " Veuillez inclure le nom de Ma liste et l'URL à ajouter.");
                return;
            }
            String pName = parts[0];
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pName);
            if (playlist == null)
                event.reply(event.getClient().getError() + " la liste n'existe pas :`" + pName + "`");
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
                    bot.getMylistLoader().writePlaylist(userId, pName, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Article ajouté à Ma Liste:`" + pName + "`");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Impossible d'ajouter à ma liste: " + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();
            String pname = event.getOption("name").getAsString();
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + " la liste n'existe pas:`" + pname + "`").queue();
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
                    bot.getMylistLoader().writePlaylist(userId, pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Article ajouté à Ma liste:`" + pname + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class ListCmd extends MusicCommand {
        public ListCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.aliases = new String[]{"available", "list"};
            this.help = "Afficher toutes les Mes listes disponibles";
            this.guildOnly = true;
            this.ownerCommand = false;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String userId = event.getAuthor().getId();

            if (!bot.getMylistLoader().folderUserExists(userId))
                bot.getMylistLoader().createUserFolder(userId);
            if (!bot.getMylistLoader().folderUserExists(userId)) {
                event.reply(event.getClient().getWarning() + " Impossible de créer le dossier de liste car il n'existe pas.");
                return;
            }
            List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
            if (list == null)
                event.reply(event.getClient().getError() + " Échec du chargement des Mes listes disponibles.");
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans mon dossier Ma liste.");
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Disponible Ma liste:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();

            if (!bot.getMylistLoader().folderUserExists(userId))
                bot.getMylistLoader().createUserFolder(userId);
            if (!bot.getMylistLoader().folderUserExists(userId)) {
                event.reply(event.getClient().getWarning() + " Impossible de créer le dossier de liste car il n'existe pas.").queue();
                return;
            }
            List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
            if (list == null)
                event.reply(event.getClient().getError() + " Échec du chargement des Mes listes disponibles.").queue();
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans mon dossier Ma liste.").queue();
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Disponible Ma liste:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString()).queue();
            }
        }
    }
}
