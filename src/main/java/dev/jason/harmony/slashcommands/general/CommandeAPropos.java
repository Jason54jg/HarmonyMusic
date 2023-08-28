package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
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
        name = "ÀPropos",
        description = "Affiche des informations sur le bot"
)

public class CommandeAPropos extends SlashCommand {
    private final Color couleur;
    private final String description;
    private final Permission[] perms;
    private final String[] fonctionnalites;
    private boolean EST_AUTEUR = true;
    private String ICONE_REMPLACEMENT = "+";
    private String lienOAuth;

    public CommandeAPropos(Color couleur, String description, String[] fonctionnalites, Permission... perms) {
        this.couleur = couleur;
        this.description = description;
        this.fonctionnalites = fonctionnalites;
        this.name = "apropos";
        this.help = "Affiche des informations sur le bot";
        this.aliases = new String[]{"about", "info", "botinfo"};
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    public void setEstAuteur(boolean value) {
        this.EST_AUTEUR = value;
    }

    public void setCaractereRemplacement(String value) {
        this.ICONE_REMPLACEMENT = value;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (lienOAuth == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                lienOAuth = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Échec de la génération du lien d'invitation ", e);
                lienOAuth = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.getGuild() == null ? couleur : event.getGuild().getSelfMember().getColor());
        builder.setAuthor(event.getJDA().getSelfUser().getName() + " Informations sur Harmony", null, event.getJDA().getSelfUser().getAvatarUrl());
        String proprietaire = "Développé par Jason54.";
        String auteur = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Bonjour ! Je suis **").append(event.getJDA().getSelfUser().getName()).append("**, ")
                .append(description).append(" J'utilise la [bibliothèque JDA](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") et appartient à ").append((EST_AUTEUR ? proprietaire : auteur + "."))
                .append(" Pour toute question concernant le bot, rendez-vous sur [le canal officiel](https://discord.gg/Fpm9qvKbbV).")
                .append("\nPour savoir comment utiliser ce bot, tapez `").append("/help")
                .append("` pour obtenir de l'aide. \n\nFonctionnalités : ```css");
        for (String fonctionnalite : fonctionnalites)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? ICONE_REMPLACEMENT : event.getClient().getSuccess()).append(" ").append(fonctionnalite);
        descr.append(" ```");
        builder.setDescription(descr);

        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Statut", event.getJDA().getGuilds().size() + " Serveur\n1 Shard", true);
            builder.addField("Utilisateurs", event.getJDA().getUsers().size() + " Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum() + " total", true);
            builder.addField("Canaux", event.getJDA().getTextChannels().size() + " Texte\n" + event.getJDA().getVoiceChannels().size() + " Voix", true);
        } else {
            builder.addField("Statut", (event.getClient()).getTotalGuilds() + " Serveur\n Shard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + " Fragment de l'utilisateur\n" + event.getJDA().getGuilds().size() + " Serveur", true);
            builder.addField("", event.getJDA().getTextChannels().size() + " canaux de texte\n" + event.getJDA().getVoiceChannels().size() + " canaux vocaux", true);
        }
        builder.setFooter("Dernier redémarrage", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        if (lienOAuth == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                lienOAuth = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Échec de la génération du lien d'invitation ", e);
                lienOAuth = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : couleur);
        builder.setAuthor(event.getSelfUser().getName() + " Informations sur Harmony", null, event.getSelfUser().getAvatarUrl());
        String proprietaire = "Développé par Jason54.";
        String auteur = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Bonjour ! Je suis **").append(event.getJDA().getSelfUser().getName()).append("**, ")
                .append(description).append(" J'utilise la [bibliothèque JDA](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") et appartient à ").append((EST_AUTEUR ? proprietaire : auteur + "."))
                .append(" Pour toute question concernant le bot, rendez-vous sur [le canal officiel](https://discord.gg/Fpm9qvKbbV).")
                .append("\nPour savoir comment utiliser ce bot, tapez `").append("/help")
                .append("` pour obtenir de l'aide. \n\nFonctionnalités : ```css");
        for (String fonctionnalite : fonctionnalites)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? ICONE_REMPLACEMENT : event.getClient().getSuccess()).append(" ").append(fonctionnalite);
        descr.append(" ```");
        builder.setDescription(descr);

        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Statut", event.getJDA().getGuilds().size() + " Serveur\n1 Shard", true);
            builder.addField("Utilisateurs", event.getJDA().getUsers().size() + " Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum() + " total", true);
            builder.addField("Canaux", event.getJDA().getTextChannels().size() + " Texte\n" + event.getJDA().getVoiceChannels().size() + " Voix", true);
        } else {
            builder.addField("Statut", (event.getClient()).getTotalGuilds() + " Serveur\n Shard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + " Fragment de l'utilisateur\n" + event.getJDA().getGuilds().size() + " Serveur", true);
            builder.addField("", event.getJDA().getTextChannels().size() + " canaux de texte\n" + event.getJDA().getVoiceChannels().size() + " canaux vocaux", true);
        }
        builder.setFooter("Dernier redémarrage", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
