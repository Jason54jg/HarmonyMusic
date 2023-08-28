package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Objects;

@CommandInfo(
        name = "About",
        description = "Afficher des informations sur les bots"
)

public class AboutCommand extends SlashCommand {
    private final Color color;
    private final String description;
    private final Permission[] perms;
    private final String[] features;
    private boolean IS_AUTHOR = true;
    private String REPLACEMENT_ICON = "+";
    private String oauthLink;

    public AboutCommand(Color color, String description, String[] features, Permission... perms) {
        this.color = color;
        this.description = description;
        this.features = features;
        this.name = "about";
        this.help = "Afficher des informations sur les bots";
        this.aliases = new String[]{"botinfo", "info"};
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    public void setIsAuthor(boolean value) {
        this.IS_AUTHOR = value;
    }

    public void setReplacementCharacter(String value) {
        this.REPLACEMENT_ICON = value;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Échec de la génération du lien d'invitation ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.getGuild() == null ? color : event.getGuild().getSelfMember().getColor());
        builder.setAuthor(event.getJDA().getSelfUser().getName() + " des informations sur Harmony", null, event.getJDA().getSelfUser().getAvatarUrl());
        String CosgyOwner = "Développé par Jason54.";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Bonjour! **").append(event.getJDA().getSelfUser().getName()).append(" C'est **")
                .append(description).append(") Il utilise la [bibliothèque JDA](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") et appartient à ").append((IS_AUTHOR ? CosgyOwner : author + "."))
                .append("Pour toute question concernant le bot direction [Official Channel](https://discord.gg/Fpm9qvKbbV).")
                .append("\nComment utiliser ce bot`").append("/help")
                .append("` pour vérifier.").append("\n\nFonctionnalités: ```css");
        for (String feature : features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
        descr.append(" ```");
        builder.setDescription(descr);

        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Status", event.getJDA().getGuilds().size() + " Server\n1 Shard", true);
            builder.addField("Utilisateurs", event.getJDA().getUsers().size() + " Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers ().size()).sum() + " total", true);
            builder.addField("Canaux", event.getJDA().getTextChannels().size() + " Texte\n" + event.getJDA().getVoiceChannels().size() + " Voix", true);
        } else  {
            builder.addField("Status", (event.getClient()).getTotalGuilds() + " Server\n Shard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + " Fragment de l'utilisateur\n" + event.getJDA().getGuilds().size() + " Serveur", true);
            builder.addField("", event.getJDA().getTextChannels().size() + " canaux de texte\n" + event.getJDA().getVoiceChannels().size() + " canaux vocaux", true);
        }
        builder.setFooter("Last restart", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Échec de la génération du lien d'invitation ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);
        builder.setAuthor(event.getSelfUser().getName() + " des informations sur Harmony", null, event.getSelfUser().getAvatarUrl());
        String CosgyOwner = "Détenu et développé par Cosgy Dev.";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Bonjour! **").append(event.getJDA().getSelfUser().getName()).append(" C'est **")
                .append(description).append(" j'utilise la [bibliothèque JDA](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") et appartient à ").append((IS_AUTHOR ? CosgyOwner : author + "."))
                .append("Pour toute question concernant le bot direction [Official Channel](https://discord.gg/Fpm9qvKbbV).")
                .append("\nComment utiliser ce bot`").append("/help")
                .append("` pour vérifier.").append("\n\nFonctionnalités: ```css");
        for (String feature: features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
        descr.append(" ```");
        builder.setDescription(descr);

        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Status", event.getJDA().getGuilds().size() + " Server\n1 Shard", true);
            builder.addField("Utilisateurs", event.getJDA().getUsers().size() + " Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers ().size()).sum() + " total", true);
            builder.addField("Canaux", event.getJDA().getTextChannels().size() + " Texte\n" + event.getJDA().getVoiceChannels().size() + " Voix", true);
        } else {
            builder.addField("Status", (event.getClient()).getTotalGuilds() + " Server\n Shard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + " Fragment de l'utilisateur\n" + event.getJDA().getGuilds().size() + " Serveur", true);
            builder.addField("", event.getJDA().getTextChannels().size() + " canaux de texte\n" + event.getJDA().getVoiceChannels().size() + " canaux vocaux", true);
        }
        builder.setFooter("Last restart", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
