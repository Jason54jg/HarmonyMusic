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
        this.name = "malist";
        this.arguments = "<ajouter|supprimer|crée|all>";
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
        StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Commandes de gestion de ma liste :\n");
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
            this.name = "crée";
            this.aliases = new String[]{"create", "make"};
            this.help = "Créer une nouvelle liste de lecture";
            this.arguments = "<nom>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "nom", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String nomPlaylist = event.getArgs().replaceAll("\\s+", "_");
            String userId = event.getAuthor().getId();

            if (nomPlaylist.isEmpty()) {
                event.replyError("Veuillez spécifier un nom de liste de lecture.");
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, nomPlaylist) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, nomPlaylist);
                    event.reply(event.getClient().getSuccess() + "Créé Ma Liste `" + nomPlaylist + "`");
                } catch (IOException e) {
                    if (event.isOwner() || event.getMember().isOwner()) {
                        event.replyError("Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "**Contenu de l'erreur : " + e.getLocalizedMessage() + "**");
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Impossible de créer la liste : " + e.getLocalizedMessage());
                }
            } else {
                event.reply(event.getClient().getError() + " La liste `" + nomPlaylist + "` existe déjà");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String nomPlaylist = event.getOption("nom").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (nomPlaylist.isEmpty()) {
                event.reply(event.getClient().getError() + " Veuillez spécifier un nom de liste de lecture.").queue();
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, nomPlaylist) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, nomPlaylist);
                    event.reply(event.getClient().getSuccess() + " Ma liste `" + nomPlaylist + "` créée").queue();
                } catch (IOException e) {
                    if (event.getClient().getOwnerId().equals(event.getMember().getId()) || event.getMember().isOwner()) {
                        event.reply(event.getClient().getError() + " Une erreur s'est produite lors du chargement de la chanson.\n" +
                                "**Contenu de l'erreur : " + e.getLocalizedMessage() + "**").queue();
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " Impossible de créer la liste : " + e.getLocalizedMessage()).queue();
                }
            } else {
                event.reply(event.getClient().getError() + " La liste `" + nomPlaylist + "` existe déjà").queue();
            }
        }
    }

    public static class DeletelistCmd extends MusicCommand {
        public DeletelistCmd(Bot bot) {
            super(bot);
            this.name = "supprimer";
            this.aliases = new String[]{"remove", "delete"};
            this.help = "Supprimer ma liste existante";
            this.arguments = "<nom>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "nom", "nom de la liste de lecture", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String nomPlaylist = event.getArgs().replaceAll("\\s+", "_");
            String userId = event.getAuthor().getId();
            if (!nomPlaylist.equals("")) {
                if (bot.getMylistLoader().getPlaylist(userId, nomPlaylist) == null)
                    event.reply(event.getClient().getError() + " La liste n'existe pas : `" + nomPlaylist + "`");
                else {
                    try {
                        bot.getMylistLoader().deletePlaylist(userId, nomPlaylist);
                        event.reply(event.getClient().getSuccess() + " Liste supprimée : `" + nomPlaylist + "`");
                    } catch (IOException e) {
                        event.reply(event.getClient().getError() + " Échec de la suppression de la liste : " + e.getLocalizedMessage());
                    }
                }
            } else {
                event.reply(event.getClient().getError() + " Veuillez inclure le nom de ma liste.");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String nomPlaylist = event.getOption("nom").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (bot.getMylistLoader().getPlaylist(userId, nomPlaylist) == null)
                event.reply(event.getClient().getError() + " La liste n'existe pas : `" + nomPlaylist + "`").queue();
            else {
                try {
                    bot.getMylistLoader().deletePlaylist(userId, nomPlaylist);
                    event.reply(event.getClient().getSuccess() + " Liste supprimée : `" + nomPlaylist + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Impossible de supprimer la liste : " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class AppendlistCmd extends MusicCommand {
        public AppendlistCmd(Bot bot) {
            super(bot);
            this.name = "ajouter";
            this.aliases = new String[]{"add", "append"};
            this.help = "Ajouter des chansons à ma liste existante";
            this.arguments = "<nom> <URL> | <URL> | ...";
            this.guildOnly = true;
            this.ownerCommand = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "nom", "nom de la liste de lecture", true));
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
            String nomPlaylist = parts[0];
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, nomPlaylist);
            if (playlist == null)
                event.reply(event.getClient().getError() + " La liste n'existe pas : `" + nomPlaylist + "`");
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
                    bot.getMylistLoader().writePlaylist(userId, nomPlaylist, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Article ajouté à Ma Liste : `" + nomPlaylist + "`");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Impossible d'ajouter à ma liste : " + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();
            String nomPlaylist = event.getOption("nom").getAsString();
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, nomPlaylist);
            if (playlist == null)
                event.reply(event.getClient().getError() + " La liste n'existe pas : `" + nomPlaylist + "`").queue();
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
                    bot.getMylistLoader().writePlaylist(userId, nomPlaylist, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Article ajouté à Ma liste : `" + nomPlaylist + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Échec de l'ajout à la liste : " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }public static class ListCmd extends MusicCommand {
        public ListCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.aliases = new String[]{"available", "list", "disponibles"};
            this.help = "Afficher toutes les listes disponibles";
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
                event.reply(event.getClient().getError() + " Échec du chargement des listes disponibles.");
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans mon dossier.");
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Listes disponibles :\n");
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
                event.reply(event.getClient().getError() + " Échec du chargement des listes disponibles.").queue();
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " Il n'y a pas de listes de lecture dans mon dossier.").queue();
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Listes disponibles :\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString()).queue();
            }
        }
    }
}