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

public class SkipratioCmd extends AdminCommand {
    public SkipratioCmd(Bot bot) {
        this.name = "setskip";
        this.help = "Définir le taux de saut spécifique au serveur";
        this.arguments = "<0 - 100>";
        this.aliases = bot.getConfig().getAliases(this.name);

        // Définir l'option pour la commande slash
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "pourcentage", "le pourcentage d'utilisateurs d'un canal vocal nécessaires afin de sauter une chanson, de 0 à 100", true));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            int val = Integer.parseInt(event.getOption("pourcentage").getAsString());
            if (val < 0 || val > 100) {
                event.reply(event.getClient().getError() + "La valeur doit être comprise entre 0 et 100.").queue();
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);

            event.reply(event.getClient().getSuccess() + "Taux de saut pour les auditeurs de *" + event.getGuild().getName() + "* défini sur " + val + "%.").queue();
        } catch (NumberFormatException ex) {
            event.reply(event.getClient().getError() + "Veuillez entrer un nombre entier entre 0 et 100 (la valeur par défaut est 55). Ce nombre représente le pourcentage d'utilisateurs écoutant la chanson qui doivent voter pour la sauter.").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            int val = Integer.parseInt(event.getArgs().endsWith("%") ? event.getArgs().substring(0, event.getArgs().length() - 1) : event.getArgs());
            if (val < 0 || val > 100) {
                event.replyError("La valeur doit être comprise entre 0 et 100.");
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);

            event.replySuccess("Taux de saut pour les auditeurs de *" + event.getGuild().getName() + "* défini sur " + val + "%.");
        } catch (NumberFormatException ex) {
            event.replyError("Veuillez entrer un nombre entier entre 0 et 100 (la valeur par défaut est 55). Ce nombre représente le pourcentage d'utilisateurs écoutant la chanson qui doivent voter pour la sauter.");
        }
    }
}
