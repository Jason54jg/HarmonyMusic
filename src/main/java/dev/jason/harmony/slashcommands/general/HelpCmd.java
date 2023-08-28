package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.util.List;
import java.util.Objects;

public class HelpCmd extends SlashCommand {
    public Bot bot;

    // Constructeur de la commande d'aide, reçoit une instance de Bot
    public HelpCmd(Bot bot) {
        this.bot = bot;
        this.name = "help";
        this.help = "Affichez la liste des commandes.";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    // Méthode d'exécution pour les commandes slash
    @Override
    protected void execute(SlashCommandEvent event) {
        StringBuilder builder = new StringBuilder("**" + event.getJDA().getSelfUser().getName() + "** Liste de commandes:\n");
        Category category = null;
        List<Command> commands = event.getClient().getCommands();
        for (Command command : commands) {
            // Vérifie si la commande est cachée et si l'utilisateur a le droit de la voir
            if (!command.isHidden() && (!command.isOwnerCommand() || event.getMember().isOwner())) {
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    builder.append("\n\n __").append(category == null ? "no category" : category.getName()).append("__:\n");
                }
                builder.append("\n`").append(event.getClient().getTextualPrefix()).append(event.getClient().getPrefix() == null ? " " : "").append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ").append(command.getHelp());
            }
        }

        String fullMessage = builder.toString();

        // Diviser le message en parties
        String messagePart1 = fullMessage.substring(0, Math.min(fullMessage.length(), 2000));
        String messagePart2 = fullMessage.substring(2000, Math.min(fullMessage.length(), 4000));

        // Envoyer la première partie du message
        event.reply(messagePart1).queue();

        // Attendre un moment pour éviter les problèmes de rapidité
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Envoyer la deuxième partie du message
        event.getChannel().sendMessage(messagePart2).queue();

        if (event.getClient().getServerInvite() != null)
            builder.append("\n\nSi vous avez besoin de plus d'aide, vous pouvez également rejoindre nos serveurs officiels: ").append(event.getClient().getServerInvite());

    }

    // Méthode d'exécution pour les commandes non-slash (précédente version)
    public void execute(CommandEvent event) {
        StringBuilder builder = new StringBuilder("**" + event.getJDA().getSelfUser().getName() + "** Liste des commandes:\n");
        Category category = null;
        List<Command> commands = event.getClient().getCommands();
        for (Command command : commands) {
            // Vérifie si la commande est cachée et si l'utilisateur a le droit de la voir
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    builder.append("\n\n __").append(category == null ? "no category" : category.getName()).append("__:\n");
                }
                builder.append("\n`").append(event.getClient().getTextualPrefix()).append(event.getClient().getPrefix() == null ? " " : "").append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ").append(command.getHelp());
            }
        }
        // Ajouter le lien d'invitation au serveur si disponible
        if (event.getClient().getServerInvite() != null)
            builder.append("\n\nVous pouvez également rejoindre le serveur officiel si vous avez besoin de plus d'aide: ").append(event.getClient().getServerInvite());

        // Envoyer le message soit en DM soit dans le salon, en fonction de la configuration
        if (bot.getConfig().getHelpToDm()) {
            event.replyInDm(builder.toString(), unused ->
            {
                if (event.isFromType(ChannelType.TEXT))
                    event.reactSuccess();
            }, t -> event.replyWarning("Je ne peux pas vous envoyer d'aide, car vous avez bloqué les messages directs."));
        } else {
            event.reply(builder.toString());
        }
    }
}
