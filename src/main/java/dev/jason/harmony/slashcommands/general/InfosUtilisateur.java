package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfosUtilisateur extends SlashCommand {
    Logger log = LoggerFactory.getLogger("InfosUtilisateur");

    public InfosUtilisateur() {
        this.name = "infosutilisateur";
        this.help = "Afficher les informations sur l'utilisateur spécifié";
        this.arguments = "<utilisateur>";
        this.guildOnly = true;
        this.category = new Category("Général");
        this.aliases = new String[]{"userinfo"};

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "utilisateur", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member memb = event.getOption("user").getAsMember();

        EmbedBuilder eb = new EmbedBuilder().setColor(memb.getColor());
        String NOM = memb.getEffectiveName();
        String DATE_REJOINDRE_SERVEUR = memb.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String DATE_REJOINDRE_DISCORD = memb.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String ID = memb.getUser().getId();
        String STATUT = memb.getOnlineStatus().getKey().replace("offline", ":x: hors ligne").replace("dnd", ":red_circle: ne pas déranger").replace("idle", "absent").replace("online", ":white_check_mark: en ligne");
        String ROLES;
        String JEU;
        String AVATAR = memb.getUser().getAvatarUrl();

        log.debug("\nusername:" + memb.getEffectiveName() + "\n" +
                "Date et heure de la guilde:"
                + memb.getUser().getTimeCreated()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
                "ID utilisateur:" + memb.getUser().getId() + "\n" +
                "Statut en ligne :" + memb.getOnlineStatus());

        try {
            JEU = memb.getActivities().toString();
        } catch (Exception e) {
            JEU = "-/-";
        }

        StringBuilder constructeurRoles = new StringBuilder();
        for (Role r : memb.getRoles()) {
            constructeurRoles.append(r.getName()).append(", ");
        }
        ROLES = constructeurRoles.toString();
        if (!ROLES.isEmpty())
            ROLES = ROLES.substring(0, ROLES.length() - 2);
        else
            ROLES = "Aucun rôle sur ce serveur";

        if (AVATAR == null) {
            AVATAR = "pas d'icône";
        }

        eb.setAuthor(memb.getUser().getName() + " Informations utilisateur pour", null, null)
                .addField(":pencil2: Nom/Surnom", "**" + NOM + "**", true)
                .addField(":1234: ID", "**" + ID + "**", true)
                .addBlankField(false)
                .addField(":signal_strength: Statut actuel", "**" + STATUT + "**", true)
                .addField(":video_game: Jeu en cours", "**" + JEU + "**", true)
                .addField(":tools: Rôles", "**" + ROLES + "**", true)
                .addBlankField(false)
                .addField(":inbox_tray: Date de connexion au serveur", "**" + DATE_REJOINDRE_SERVEUR + "**", true)
                .addField(":beginner: Date de création du compte", "**" + DATE_REJOINDRE_DISCORD + "**", true)
                .addBlankField(false)
                .addField(":frame_photo: URL de l'icône", AVATAR, false);

        if (!AVATAR.equals("pas d'icône")) {
            eb.setAuthor(memb.getUser().getName() + " Informations utilisateur pour", null, AVATAR);
        }

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void execute(CommandEvent event) {
        Member memb;

        if (!event.getArgs().isEmpty()) {
            try {
                if (!Objects.requireNonNull(event.getMessage().getReferencedMessage()).getMentions().getMembers().isEmpty()) {
                    memb = event.getMessage().getReferencedMessage().getMentions().getMembers().get(0);
                } else {
                    memb = FinderUtil.findMembers(event.getArgs(), event.getGuild()).get(0);
                }
            } catch (Exception e) {
                event.reply("Utilisateur \"" + event.getArgs() + "\" introuvable.");
                return;
            }
        } else {
            memb = event.getMember();
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(memb.getColor());
        String NOM = memb.getEffectiveName();
        String DATE_REJOINDRE_SERVEUR = memb.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String DATE_REJOINDRE_DISCORD = memb.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String ID = memb.getUser().getId();
        String STATUT = memb.getOnlineStatus().getKey().replace("offline", ":x: hors ligne").replace("dnd", ":red_circle: ne pas déranger").replace("idle", "absent").replace("online", ":white_check_mark: en ligne");
        String ROLES;
        String JEU;
        String AVATAR = memb.getUser().getAvatarUrl();

        log.debug("\nusername:" + memb.getEffectiveName() + "\n" +
                "Date et heure de la guilde:"
                + memb.getUser().getTimeCreated()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
                "ID utilisateur:" + memb.getUser().getId() + "\n" +
                "Statut en ligne :" + memb.getOnlineStatus());

        try {
            JEU = memb.getActivities().toString();
        } catch (Exception e) {
            JEU = "-/-";
        }

        StringBuilder constructeurRoles = new StringBuilder();
        for (Role r : memb.getRoles()) {
            constructeurRoles.append(r.getName()).append(", ");
        }
        ROLES = constructeurRoles.toString();
        if (!ROLES.isEmpty())
            ROLES = ROLES.substring(0, ROLES.length() - 2);
        else
            ROLES = "Aucun rôle sur ce serveur";

        if (AVATAR == null) {
            AVATAR = "pas d'icône";
        }

        eb.setAuthor(memb.getUser().getName() + " Informations utilisateur pour", null, null)
                .addField(":pencil2: Nom/Surnom", "**" + NOM + "**", true)
                .addField(":1234: ID", "**" + ID + "**", true)
                .addBlankField(false)
                .addField(":signal_strength: Statut actuel", "**" + STATUT + "**", true)
                .addField(":video_game: Jeu en cours", "**" + JEU + "**", true)
                .addField(":tools: Rôles", "**" + ROLES + "**", true)
                .addBlankField(false)
                .addField(":inbox_tray: Date de connexion au serveur", "**" + DATE_REJOINDRE_SERVEUR + "**", true)
                .addField(":beginner: Date de création du compte", "**" + DATE_REJOINDRE_DISCORD + "**", true)
                .addBlankField(false)
                .addField(":frame_photo: URL de l'icône", AVATAR, false);

        if (!AVATAR.equals("pas d'icône")) {
            eb.setAuthor(memb.getUser().getName() + " Informations utilisateur pour", null, AVATAR);
        }

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
