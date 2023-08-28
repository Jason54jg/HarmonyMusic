package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetgameCmd extends OwnerCommand {
    public SetgameCmd(Bot bot) {
        this.name = "setgame";
        this.help = "Définit le jeu auquel le bot joue";
        this.arguments = "[action] [game]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
                new PlayingCmd(),
                new SetlistenCmd(),
                new SetstreamCmd(),
                new SetwatchCmd(),
                new SetCompetingCmd(),
                new NoneCmd()
        };
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
    }

    @Override
    protected void execute(CommandEvent event) {
        String title = event.getArgs().toLowerCase().startsWith("playing") ? event.getArgs().substring(7).trim() : event.getArgs();
        try {
            event.getJDA().getPresence().setActivity(title.isEmpty() ? null : Activity.playing(title));
            event.reply(event.getClient().getSuccess() + " **" + event.getSelfUser().getName()
                    + "** is " + (title.isEmpty()? "Nothing.": "Playing now `" + title + "`."));
        } catch (Exception e) {
            event.reply(event.getClient().getError() + "Impossible de définir le statut.");
        }
    }

    private class NoneCmd extends OwnerCommand {
        private NoneCmd() {
            this.name = "none";
            this.aliases = new String[]{"none"};
            this.help = "Statut de réinitialisation.";
            this.guildOnly = false;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Statut de réinitialisation.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Statut de réinitialisation.");
        }
    }

    private class PlayingCmd extends OwnerCommand {
        private PlayingCmd() {
            this.name = "playing";
            this.aliases = new String[]{"twitch", "streaming"};
            this.help = "Définit le jeu auquel le bot joue.";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String title = event.getOption("title").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.playing(title));
                event.reply(event.getClient().getSuccess() + " **" + event.getJDA().getSelfUser().getName()
                        + "** est " + "en cours de lecture `" + title + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de définir le statut.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
        }
    }

    private class SetstreamCmd extends OwnerCommand {
        private SetstreamCmd() {
            this.name = "stream";
            this.aliases = new String[]{"twitch", "streaming"};
            this.help = "Définissez le jeu auquel le bot joue sur le flux.";
            this.arguments = "<username> <game>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "user", "nom d'utilisateur", true));
            options.add(new OptionData(OptionType.STRING, "game", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(event.getOption("game").getAsString(), "https://twitch.tv/" + event.getOption("user").getAsString()));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName()
                        + "** diffuse actuellement `" + event.getOption("game").getAsString() + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if (parts.length < 2) {
                event.replyError("Entrez votre nom d'utilisateur et le nom du 'jeu en streaming'");
                return;
            }
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(parts[1], "https://twitch.tv/" + parts[0]));
                event.replySuccess("**" + event.getSelfUser().getName()
                        + "** diffuse actuellement `" + parts[1] + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.");
            }
        }
    }

    private class SetlistenCmd extends OwnerCommand {
        private SetlistenCmd() {
            this.name = "listen";
            this.aliases = new String[]{"listening"};
            this.help = "Définit le jeu que le bot écoute";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "titre", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String title = event.getOption("title").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.listening(title));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** écoute actuellement `" + title + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Incluez le titre que vous écoutez!");
                return;
            }
            String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.listening(title));
                event.replySuccess("**" + event.getSelfUser().getName() + "** écoute actuellement `" + title + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }

    private class SetwatchCmd extends OwnerCommand {
        private SetwatchCmd() {
            this.name = "watch";
            this.aliases = new String[]{"watching"};
            this.help = "Définit le jeu que le bot regarde";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "titre", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String title = event.getOption("title").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(title));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** regarde actuellement `" + title + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Veuillez entrer le titre que vous regardez.");
                return;
            }
            String title = event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(title));
                event.replySuccess("**" + event.getSelfUser().getName() + "** regarde actuellement `" + title + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }

    private class SetCompetingCmd extends OwnerCommand {
        private SetCompetingCmd() {
            this.name = "competing";
            this.help = "Définit le jeu auquel le bot participe";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String title = event.getOption("title").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.competing(title));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** sont actuellement en compétition pour `" + title + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Veuillez saisir le titre auquel vous participez.");
                return;
            }
            String title = event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(title));
                event.replySuccess("**" + event.getSelfUser().getName() + "** participe actuellement à `" + title + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }
}
