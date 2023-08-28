package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ServerInfo extends SlashCommand {
    public ServerInfo(Bot bot) {
        this.name = "serverinfo";
        this.help = "Afficher des informations sur le serveur";
        this.guildOnly = true;
        this.category = new Category("General");
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String GuildName = event.getGuild().getName();
        String GuildIconURL = event.getGuild().getIconUrl();
        String GuildId = event.getGuild().getId();
        String GuildOwner = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String GuildCreatedDate = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String GuildRolesCount = String.valueOf(event.getGuild().getRoles().size());
        String GuildMember = String.valueOf(event.getGuild().getMembers().size());
        String GuildCategoryCount = String.valueOf(event.getGuild().getCategories().size());
        String GuildTextChannelCount = String.valueOf(event.getGuild().getTextChannels().size());
        String GuildVoiceChannelCount = String.valueOf(event.getGuild().getVoiceChannels().size());
        String GuildStageChannelCount = String.valueOf(event.getGuild().getStageChannels().size());
        String GuildForumChannelCount = String.valueOf(event.getGuild().getForumChannels().size());
        String GuildLocation = event.getGuild().getLocale().getNativeName();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("server " + GuildName + " info", null, GuildIconURL);

        eb.addField("ServerId", GuildId, true);
        eb.addField("Server Primary Language", GuildLocation, true);
        eb.addField("Propriétaire du serveur", GuildOwner, true);
        eb.addField("Nombre de membres", GuildMember, true);
        eb.addField("Nombre de positions", GuildRolesCount, true);
        eb.addField("Nombre de catégories", GuildCategoryCount, true);
        eb.addField("Nombre de canaux de texte", GuildTextChannelCount, true);
        eb.addField("Nombre de canaux vocaux", GuildVoiceChannelCount, true);
        eb.addField("Nombre de canaux d'étape", GuildStageChannelCount, true);
        eb.addField("Nombre de canaux du forum", GuildForumChannelCount, true);

        eb.setFooter("Date de création du serveur : " + GuildCreatedDate, null);

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void execute(CommandEvent event) {
        String GuildName = event.getGuild().getName();
        String GuildIconURL = event.getGuild().getIconUrl();
        String GuildId = event.getGuild().getId();
        String GuildOwner = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String GuildCreatedDate = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss"));

        String GuildRolesCount = String.valueOf(event.getGuild().getRoles().size());
        String GuildMember = String.valueOf(event.getGuild().getMembers().size());
        String GuildCategoryCount = String.valueOf(event.getGuild().getCategories().size());
        String GuildTextChannelCount = String.valueOf(event.getGuild().getTextChannels().size());
        String GuildVoiceChannelCount = String.valueOf(event.getGuild().getVoiceChannels().size());
        String GuildStageChannelCount = String.valueOf(event.getGuild().getStageChannels().size());
        String GuildForumChannelCount = String.valueOf(event.getGuild().getForumChannels().size());
        String GuildLocation = event.getGuild().getLocale().getNativeName();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("server " + GuildName + " info", null, GuildIconURL);

        eb.addField("ServerId", GuildId, true);
        eb.addField("Server Primary Language", GuildLocation, true);
        eb.addField("Propriétaire du serveur", GuildOwner, true);
        eb.addField("Nombre de membres", GuildMember, true);
        eb.addField("Nombre de positions", GuildRolesCount, true);
        eb.addField("Nombre de catégories", GuildCategoryCount, true);
        eb.addField("Nombre de canaux de texte", GuildTextChannelCount, true);
        eb.addField("Nombre de canaux vocaux", GuildVoiceChannelCount, true);
        eb.addField("Nombre de canaux d'étape", GuildStageChannelCount, true);
        eb.addField("Nombre de canaux du forum", GuildForumChannelCount, true);

        eb.setFooter("Date de création du serveur : " + GuildCreatedDate, null);

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
