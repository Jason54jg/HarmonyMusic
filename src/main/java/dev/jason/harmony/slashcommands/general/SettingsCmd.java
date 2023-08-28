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

public class SettingsCmd extends SlashCommand {
    private final static String EMOJI = "\uD83C\uDFA7"; // 🎧

    public SettingsCmd(Bot bot) {
        this.name = "settings";
        this.help = "Afficher les paramètres du bot";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent(EMOJI + " **")
                .addContent(FormatUtil.filter(event.getJDA().getSelfUser().getName()))
                .addContent("** paramètres de:");
        TextChannel tChan = s.getTextChannel(event.getGuild());
        VoiceChannel vChan = s.getVoiceChannel(event.getGuild());
        Role role = s.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setDescription("Canal pour l'exécution de la commande : " + (tChan == null ? "none" : "**#" + tChan.getName() + "**")
                + "\nCanal vocal dédié: " + (vChan == null? "none": "**" + vChan.getAsMention() + "**")
                + "\nDJ permission: " + (role == null ? "not set" : "**" + role.getName() + "**")
                + "\nRépéter: **" + (s.getRepeatMode() == RepeatMode.ALL? "Activer (répéter toutes les chansons)": (s.getRepeatMode() == RepeatMode.SINGLE? "Activer (répéter 1 chanson)" : "désactivé")) + "**"
                + "\nVolume:**" + (s.getVolume()) + "**"
                + "\nListe de lecture par défaut: " + (s.getDefaultPlaylist() == null? "none": "**" + s.getDefaultPlaylist() + "**")
                )
                .setFooter(String.format(
                                "Rejoindre les serveurs %s | Se connecter aux canaux vocaux %s",
                                event.getJDA().getGuilds().size(),
                                event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                        null);
        event.reply(builder.addEmbeds(ebuilder.build()).build()).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent(EMOJI + " **")
                .addContent(FormatUtil.filter(event.getSelfUser().getName()))
                .addContent("** paramètres de:");
        TextChannel tChan = s.getTextChannel(event.getGuild());
        VoiceChannel vChan = s.getVoiceChannel(event.getGuild());
        Role role = s.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(event.getSelfMember().getColor())
                .setDescription("Canal pour l'exécution de la commande : " + (tChan == null ? "none" : "**#" + tChan.getName() + "**")
                        + "\nCanal vocal dédié: " + (vChan == null? "none": "**" + vChan.getName() + "**")
                        + "\nDJ permission: " + (role == null ? "not set" : "**" + role.getName() + "**")
                        + "\nRépéter: **" + (s.getRepeatMode() == RepeatMode.ALL? "Activer (répéter toutes les chansons)": (s.getRepeatMode() == RepeatMode.SINGLE? "Activer (répéter 1 chanson)" : "désactivé")) + "**"
                        + "\nListe de lecture par défaut: " + (s.getDefaultPlaylist() == null? "none": "**" + s.getDefaultPlaylist() + "**")
                )
                .setFooter(String.format(
                                "Rejoindre les serveurs %s | Se connecter aux canaux vocaux %s",
                                event.getJDA().getGuilds().size(),
                                event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                        null);
        event.getChannel().sendMessage(builder.addEmbeds(ebuilder.build()).build()).queue();
    }

}
