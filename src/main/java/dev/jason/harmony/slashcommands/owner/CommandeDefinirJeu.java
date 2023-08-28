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

public class CommandeDefinirJeu extends OwnerCommand {
    public CommandeDefinirJeu(Bot bot) {
        this.name = "definirjeu";
        this.help = "Définit le jeu auquel le bot joue";
        this.arguments = "[action] [jeu]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
                new JoueCmd(),
                new EcouteCmd(),
                new DiffuseCmd(),
                new RegardeCmd(),
                new ParticipeCmd(),
                new AucunCmd()
        };
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
    }

    @Override
    protected void execute(CommandEvent event) {
        String titre = event.getArgs().toLowerCase().startsWith("joue") ? event.getArgs().substring(5).trim() : event.getArgs();
        try {
            event.getJDA().getPresence().setActivity(titre.isEmpty() ? null : Activity.playing(titre));
            event.reply(event.getClient().getSuccess() + " **" + event.getSelfUser().getName()
                    + "** est " + (titre.isEmpty() ? "Rien." : "En train de jouer à `" + titre + "`."));
        } catch (Exception e) {
            event.reply(event.getClient().getError() + "Impossible de définir le statut.");
        }
    }

    private class AucunCmd extends OwnerCommand {
        private AucunCmd() {
            this.name = "aucun";
            this.aliases = new String[]{"aucun"};
            this.help = "Réinitialisation du statut.";
            this.guildOnly = false;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Réinitialisation du statut.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Réinitialisation du statut.");
        }
    }

    private class JoueCmd extends OwnerCommand {
        private JoueCmd() {
            this.name = "joue";
            this.aliases = new String[]{"twitch", "streaming"};
            this.help = "Définit le jeu auquel le bot joue.";
            this.arguments = "<titre>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "titre", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String titre = event.getOption("titre").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.playing(titre));
                event.reply(event.getClient().getSuccess() + " **" + event.getJDA().getSelfUser().getName()
                        + "** joue actuellement à `" + titre + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de définir le statut.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
        }
    }

    private class DiffuseCmd extends OwnerCommand {
        private DiffuseCmd() {
            this.name = "flux";
            this.aliases = new String[]{"twitch", "diffusion"};
            this.help = "Définissez le jeu auquel le bot joue sur le flux.";
            this.arguments = "<nom d'utilisateur> <jeu>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "utilisateur", "nom d'utilisateur", true));
            options.add(new OptionData(OptionType.STRING, "jeu", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(event.getOption("jeu").getAsString(), "https://twitch.tv/" + event.getOption("utilisateur").getAsString()));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName()
                        + "** diffuse actuellement `" + event.getOption("jeu").getAsString() + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parties = event.getArgs().split("\\s+", 2);
            if (parties.length < 2) {
                event.replyError("Entrez votre nom d'utilisateur et le nom du jeu en diffusion");
                return;
            }
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(parties[1], "https://twitch.tv/" + parties[0]));
                event.replySuccess("**" + event.getSelfUser().getName()
                        + "** diffuse actuellement `" + parties[1] + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.");
            }
        }
    }

    private class EcouteCmd extends OwnerCommand {
        private EcouteCmd() {
            this.name = "ecoute";
            this.aliases = new String[]{"listening"};
            this.help = "Définit le jeu que le bot écoute";
            this.arguments = "<titre>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "titre", "titre", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String titre = event.getOption("titre").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.listening(titre));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** écoute actuellement `" + titre + "`.").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Impossible de configurer le jeu.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Incluez le titre que vous écoutez !");
                return;
            }
            String titre = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.listening(titre));
                event.replySuccess("**" + event.getSelfUser().getName() + "** écoute actuellement `" + titre + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }

    private class RegardeCmd extends OwnerCommand {
        private RegardeCmd() {
            this.name = "regarder";
            this.aliases = new String[]{"regardant"};
            this.help = "Définit le jeu que le bot regarde";
            this.arguments = "<titre>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "titre", "titre", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String titre = event.getOption("titre").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(titre));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** regarde actuellement `" + titre + "`.").queue();
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
            String titre = event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(titre));
                event.replySuccess("**" + event.getSelfUser().getName() + "** regarde actuellement `" + titre + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }

    private class ParticipeCmd extends OwnerCommand {
        private ParticipeCmd() {
            this.name = "participer";
            this.help = "Définit le jeu auquel le bot participe";
            this.arguments = "<titre>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "titre", "titre du jeu", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String titre = event.getOption("titre").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.competing(titre));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** participent actuellement à `" + titre + "`.").queue();
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
            String titre = event.getArgs();
            try {
                event.getJDA().getPresence().setActivity(Activity.watching(titre));
                event.replySuccess("**" + event.getSelfUser().getName() + "** participe actuellement à `" + titre + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + " Impossible de définir le jeu.");
            }
        }
    }

}
