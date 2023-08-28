package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.playlist.PubliclistLoader.Playlist;
import dev.jason.harmony.slashcommands.OwnerCommand;
import dev.jason.harmony.slashcommands.admin.AutoplaylistCmd;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublistCmd extends OwnerCommand {
    private final Bot bot;

    public PublistCmd(Bot bot) {
        this.bot = bot;
        this.guildOnly = false;
        this.name = "publist";
        this.arguments = "<append|delete|make|all>";
        this.help = "Gestion des listes de lecture";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new OwnerCommand[]{
                new ListCmd(),
                new AppendlistCmd(),
                new DeletelistCmd(),
                new MakelistCmd()
        };
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    @Override
    public void execute(CommandEvent event) {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning() + "commande de gestion de la liste de lecture:\n");
        for (Command cmd : this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    public static class DefaultlistCmd extends AutoplaylistCmd {
        public DefaultlistCmd(Bot bot) {
            super(bot);
            this.name = "setdefault";
            this.aliases = new String[]{"default"};
            this.arguments = "<playlistname|NONE>";
            this.guildOnly = true;
        }
    }

    public class MakelistCmd extends OwnerCommand {
        public MakelistCmd() {
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "créer une nouvelle liste de lecture";
            this.arguments = "<name>";
            this.guildOnly = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            if (bot.getPublistLoader().getPlaylist(pname) == null) {
                try {
                    bot.getPublistLoader().createPlaylist(pname);
                    event.reply(event.getClient().getSuccess() + " Une playlist a été créée avec le nom `" + pname + "`!").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + "Impossible de créer la liste de lecture:" + e.getLocalizedMessage()).queue();
                }
            } else
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` existe déjà!").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if (bot.getPublistLoader().getPlaylist(pname) == null) {
                try {
                    bot.getPublistLoader().createPlaylist(pname);
                    event.reply(event.getClient().getSuccess() + " Une playlist a été créée avec le nom `" + pname + "`!");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + "Impossible de créer la liste de lecture:" + e.getLocalizedMessage());
                }
            } else
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` existe déjà!");
        }
    }

    public class DeletelistCmd extends OwnerCommand {
        public DeletelistCmd() {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Supprimer la liste de lecture existante";
            this.arguments = "<name>";
            this.guildOnly = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            if (bot.getPublistLoader().getPlaylist(pname) == null)
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` n'existe pas!").queue();
            else {
                try {
                    bot.getPublistLoader().deletePlaylist(pname);
                    event.reply(event.getClient().getSuccess() + " Liste de lecture supprimée `"+pname+"`.!").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de la suppression de la liste de lecture : " + e.getLocalizedMessage()).queue();
                }
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if (bot.getPublistLoader().getPlaylist(pname) == null)
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` n'existe pas!");
            else {
                try {
                    bot.getPublistLoader().deletePlaylist(pname);
                    event.reply(event.getClient().getSuccess() + " Liste de lecture supprimée `" + pname + "`.!");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de la suppression de la liste de lecture: " + e.getLocalizedMessage());
                }
            }
        }
    }

    public class AppendlistCmd extends OwnerCommand {
        public AppendlistCmd() {
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "Ajouter des chansons à une liste de lecture existante";
            this.arguments = "<name> <URL> | <URL> | ...";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "nom de la liste de lecture", true));
            options.add(new OptionData(OptionType.STRING, "url", "URL", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String pname = event.getOption("name").getAsString();
            Playlist playlist = bot.getPublistLoader().getPlaylist(pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` n'existe pas!").queue();
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
                    bot.getPublistLoader().writePlaylist(pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + "Élément ajouté à la liste de lecture `" + pname + "` !").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste de lecture : " + e.getLocalizedMessage()).queue();
                }
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if (parts.length < 2) {
                event.reply(event.getClient().getError() + "Veuillez inclure le nom de la liste de lecture et l'URL à ajouter.");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPublistLoader().getPlaylist(pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + "Playlist `" + pname + "` n'existe pas !");
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
                    bot.getPublistLoader().writePlaylist(pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + "Élément ajouté à la liste de lecture `" + pname + "` !");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste de lecture : " + e.getLocalizedMessage());
                }
            }
        }
    }

    public class ListCmd extends OwnerCommand {
        public ListCmd() {
            this.name = "all";
            this.aliases = new String[]{"available", "list"};
            this.help = "Afficher toutes les listes de lecture disponibles.";
            this.guildOnly = true;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            if (!bot.getPublistLoader().folderExists())
                bot.getPublistLoader().createFolder();
            if (!bot.getPublistLoader().folderExists()) {
                event.reply(event.getClient().getWarning() + "Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.").queue();
                return;
            }
            List<String> list = bot.getPublistLoader().getPlaylistNames();
            if (list == null)
                event.reply(event.getClient().getError() + "Échec du chargement des listes de lecture disponibles.").queue();
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + "Il n'y a pas de listes de lecture dans le dossier de listes de lecture.").queue();
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Listes de lecture disponibles:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString()).queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            if (!bot.getPublistLoader().folderExists())
                bot.getPublistLoader().createFolder();
            if (!bot.getPublistLoader().folderExists()) {
                event.reply(event.getClient().getWarning() + "Le dossier de la liste de lecture n'a pas pu être créé car il n'existe pas.");
                return;
            }
            List<String> list = bot.getPublistLoader().getPlaylistNames();
            if (list == null)
                event.reply(event.getClient().getError() + "Échec du chargement des listes de lecture disponibles.");
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + "Il n'y a pas de listes de lecture dans le dossier de listes de lecture.");
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Listes de lecture disponibles :\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }
    }
}
