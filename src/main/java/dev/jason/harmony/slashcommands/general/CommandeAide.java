package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.util.List;
import java.util.Objects;

public class CommandeAide extends SlashCommand {
    public Bot bot;

    public CommandeAide(Bot bot) {
        this.bot = bot;
        this.name = "aide";
        this.help = "Affiche la liste des commandes.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        StringBuilder constructeur = new StringBuilder("**" + event.getJDA().getSelfUser().getName() + "** Liste des commandes :\n");
        Category category = null;
        List<Command> commandes = event.getClient().getCommands();
        for (Command commande : commandes) {
            if (!commande.isHidden() && (!commande.isOwnerCommand() || event.getMember().isOwner())) {
                if (!Objects.equals(category, commande.getCategory())) {
                    category = commande.getCategory();
                    constructeur.append("\n\n __").append(category == null ? "aucune catégorie" : category.getName()).append("__:\n");
                }
                constructeur.append("\n`").append(event.getClient().getTextualPrefix()).append(event.getClient().getPrefix() == null ? " " : "").append(commande.getName())
                        .append(commande.getArguments() == null ? "`" : " " + commande.getArguments() + "`")
                        .append(" - ").append(commande.getHelp());
            }
        }

        String messageComplet = constructeur.toString();

        String partieMessage1 = messageComplet.substring(0, Math.min(messageComplet.length(), 2000));
        String partieMessage2 = messageComplet.substring(2000, Math.min(messageComplet.length(), 4000));

        event.reply(partieMessage1).queue();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        event.getChannel().sendMessage(partieMessage2).queue();

        if (event.getClient().getServerInvite() != null)
            constructeur.append("\n\nSi vous avez besoin d'aide supplémentaire, vous pouvez également rejoindre nos serveurs officiels : ").append(event.getClient().getServerInvite());
    }

    public void execute(CommandEvent event) {
        StringBuilder constructeur = new StringBuilder("**" + event.getJDA().getSelfUser().getName() + "** Liste des commandes :\n");
        Category category = null;
        List<Command> commandes = event.getClient().getCommands();
        for (Command commande : commandes) {
            if (!commande.isHidden() && (!commande.isOwnerCommand() || event.isOwner())) {
                if (!Objects.equals(category, commande.getCategory())) {
                    category = commande.getCategory();
                    constructeur.append("\n\n __").append(category == null ? "aucune catégorie" : category.getName()).append("__:\n");
                }
                constructeur.append("\n`").append(event.getClient().getTextualPrefix()).append(event.getClient().getPrefix() == null ? " " : "").append(commande.getName())
                        .append(commande.getArguments() == null ? "`" : " " + commande.getArguments() + "`")
                        .append(" - ").append(commande.getHelp());
            }
        }

        if (event.getClient().getServerInvite() != null)
            constructeur.append("\n\nVous pouvez également rejoindre le serveur officiel si vous avez besoin d'aide : ").append(event.getClient().getServerInvite());

        if (bot.getConfig().getHelpToDm()) {
            event.replyInDm(constructeur.toString(), unused -> {
                if (event.isFromType(ChannelType.TEXT))
                    event.reactSuccess();
            }, t -> event.replyWarning("Je ne peux pas vous envoyer d'aide, car vous avez bloqué les messages directs."));
        } else {
            event.reply(constructeur.toString());
        }
    }
}
