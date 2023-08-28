package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.settings.Settings;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.settings.RepeatMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.Objects;

public class ParametresCmd extends SlashCommand {
    private final static String EMOJI = "\uD83C\uDFA7"; // 🎧

    public ParametresCmd(Bot bot) {
        this.name = "parametres";
        this.help = "Affiche les paramètres du bot";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Settings p = event.getClient().getSettingsFor(event.getGuild());
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent(EMOJI + " **")
                .addContent(FormatUtil.filter(event.getJDA().getSelfUser().getName()))
                .addContent("** paramètres de :");
        TextChannel tChan = p.getTextChannel(event.getGuild());
        VoiceChannel vChan = p.getVoiceChannel(event.getGuild());
        Role role = p.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setDescription("Canal pour l'exécution de la commande : " + (tChan == null ? "aucun" : "**#" + tChan.getName() + "**")
                        + "\nCanal vocal dédié : " + (vChan == null? "aucun" : "**" + vChan.getAsMention() + "**")
                        + "\nRôle DJ : " + (role == null ? "non défini" : "**" + role.getName() + "**")
                        + "\nMode Répétition : **" + (p.getRepeatMode() == RepeatMode.ALL? "Activé (répéter toutes les chansons)" : (p.getRepeatMode() == RepeatMode.SINGLE? "Activé (répéter 1 chanson)" : "désactivé")) + "**"
                        + "\nVolume : **" + (p.getVolume()) + "**"
                        + "\nPlaylist par défaut : " + (p.getDefaultPlaylist() == null? "aucune" : "**" + p.getDefaultPlaylist() + "**")
                )
                .setFooter(String.format(
                                "Rejoindre les serveurs %s | Connecté aux canaux vocaux %s",
                                event.getJDA().getGuilds().size(),
                                event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                        null);
        event.reply(builder.addEmbeds(ebuilder.build()).build()).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        Settings p = event.getClient().getSettingsFor(event.getGuild());
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent(EMOJI + " **")
                .addContent(FormatUtil.filter(event.getSelfUser().getName()))
                .addContent("** paramètres de :");
        TextChannel tChan = p.getTextChannel(event.getGuild());
        VoiceChannel vChan = p.getVoiceChannel(event.getGuild());
        Role role = p.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(event.getSelfMember().getColor())
                .setDescription("Canal pour l'exécution de la commande : " + (tChan == null ? "aucun" : "**#" + tChan.getName() + "**")
                        + "\nCanal vocal dédié : " + (vChan == null? "aucun" : "**" + vChan.getName() + "**")
                        + "\nRôle DJ : " + (role == null ? "non défini" : "**" + role.getName() + "**")
                        + "\nMode Répétition : **" + (p.getRepeatMode() == RepeatMode.ALL? "Activé (répéter toutes les chansons)" : (p.getRepeatMode() == RepeatMode.SINGLE? "Activé (répéter 1 chanson)" : "désactivé")) + "**"
                        + "\nPlaylist par défaut : " + (p.getDefaultPlaylist() == null? "aucune" : "**" + p.getDefaultPlaylist() + "**")
                )
                .setFooter(String.format(
                                "Rejoindre les serveurs %s | Connecté aux canaux vocaux %s",
                                event.getJDA().getGuilds().size(),
                                event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                        null);
        event.getChannel().sendMessage(builder.addEmbeds(ebuilder.build()).build()).queue();
    }
}
