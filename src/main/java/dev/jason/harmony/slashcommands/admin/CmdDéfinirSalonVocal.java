package dev.jason.harmony.slashcommands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.slashcommands.AdminCommand;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CmdDéfinirSalonVocal extends AdminCommand {
    public CmdDéfinirSalonVocal(Bot bot) {
        this.name = "definirsalonvocal";
        this.help = "Définir le canal audio utilisé pour la lecture.";
        this.arguments = "<canal|aucun>";
        this.aliases = bot.getConfig().getAliases(this.name);

        // Définir les sous-commandes pour la commande slash
        this.children = new SlashCommand[]{new Set(), new None()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        // Non implémenté dans cet exemple
    }

    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("SetVcCmd");
        if (event.getArgs().isEmpty()) {
            event.reply(event.getClient().getError() + "Veuillez inclure les canaux audio ou AUCUN.");
            return;
        }

        Settings s = event.getClient().getSettingsFor(event.getGuild());

        // Gérer le cas où l'utilisateur choisit "aucun"
        if (event.getArgs().toLowerCase().matches("(aucun)")) {
            s.setVoiceChannel(null);
            event.reply(event.getClient().getSuccess() + "La musique peut maintenant être jouée sur n'importe quel canal.");
        } else {
            List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getArgs(), event.getGuild());
            if (list.isEmpty())
                event.reply(event.getClient().getWarning() + "Aucun canal audio correspondant trouvé pour \"" + event.getArgs() + "\".");
            else if (list.size() > 1)
                event.reply(event.getClient().getWarning() + FormatUtil.listOfVChannels(list, event.getArgs()));
            else {
                s.setVoiceChannel(list.get(0));
                log.info("J'ai configuré un canal pour la musique.");
                event.reply(event.getClient().getSuccess() + "La musique ne peut maintenant être jouée que dans **" + list.get(0).getAsMention() + "**.");
            }
        }
    }

    // Sous-commande pour définir le canal audio via la commande slash
    private static class Set extends AdminCommand {
        public Set() {
            this.name = "set";
            this.help = "Définir le canal audio à utiliser pour la lecture";

            // Définir les options pour la sous-commande slash
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.CHANNEL, "channel", "canal pour autoriser la musique, laissez vide pour effacer le canal", true));

            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            // Vérifier les permissions d'administrateur
            if (checkAdminPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());
            Long channelId = event.getOption("channel").getAsLong();

            // Vérifier le type de canal
            if (event.getOption("channel").getChannelType() != ChannelType.VOICE) {
                event.reply(event.getClient().getError() + "Veuillez définir un canal vocal.").queue();
                return;
            }

            VoiceChannel vc = event.getGuild().getVoiceChannelById(channelId);
            s.setVoiceChannel(vc);
            event.reply(event.getClient().getSuccess() + "La musique ne peut maintenant être jouée que dans **" + vc.getAsMention() + "**.").queue();
        }
    }

    // Sous-commande pour réinitialiser le canal audio via la commande slash
    private static class None extends AdminCommand {
        public None() {
            this.name = "aucun";
            this.help = "Réinitialiser les paramètres du canal audio utilisés pour la lecture.";
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            // Vérifier les permissions d'administrateur
            if (checkAdminPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
                return;
            }

            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setVoiceChannel(null);
            event.reply(event.getClient().getSuccess() + "La musique peut maintenant être jouée sur n'importe quel canal.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setVoiceChannel(null);
            event.replySuccess("La musique peut maintenant être jouée sur n'importe quel canal audio.");
        }
    }
}
