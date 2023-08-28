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

public class UserInfo extends SlashCommand {
    Logger log = LoggerFactory.getLogger("UserInfo");

    public UserInfo() {
        this.name = "userinfo";
        this.help = "Afficher les informations sur l'utilisateur spécifié";
        this.arguments = "<utilisateur>";
        this.guildOnly = true;
        this.category = new Category("General");

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "utilisateur", true));
        this.options = options;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member memb = event.getOption("user").getAsMember();

        EmbedBuilder eb = new EmbedBuilder().setColor(memb.getColor());
        String NAME = memb.getEffectiveName();
        String TAG = "#" + memb.getUser().getDiscriminator();
        String GUILD_JOIN_DATE = memb.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss"));
        String DISCORD_JOINED_DATE = memb.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss"));
        String ID = memb.getUser().getId();
        String STATUS = memb.getOnlineStatus().getKey().replace("offline", ":x: hors ligne").replace("dnd", ":red_circle: ne me réveille pas").replace("idle", "loin").replace("online", ":white_check_mark: en ligne");
        String ROLES;
        String GAME;
        String AVATAR = memb.getUser().getAvatarUrl();

        log.debug("\nusername:" + memb.getEffectiveName() + "\n" +
                "balise:" + memb.getUser().getDiscriminator() + "\n" +
                "Date et heure de la guilde:"
                + memb.getUser().getTimeCreated()
                .format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss")) + "\n" +
                "ID utilisateur:" + memb.getUser().getId() + "\n" +
                "Statut en ligne :" + memb.getOnlineStatus());

        try {
            GAME = memb.getActivities().toString();
        } catch (Exception e) {
            GAME = "-/-";
        }

        StringBuilder ROLESBuilder = new StringBuilder();
        for (Role r : memb.getRoles()) {
            ROLESBuilder.append(r.getName()).append(", ");
        }
        ROLES = ROLESBuilder.toString();
        if (ROLES.length() > 0)
            ROLES = ROLES.substring(0, ROLES.length() - 2);
        else
            ROLES = "Le titre du poste n'existe pas sur ce serveur";

        if (AVATAR == null) {
            AVATAR = "pas d'icône";
        }

        eb.setAuthor(memb.getUser().getName() + TAG + "informations utilisateur pour", null, null)
                .addField(":pencil2: nom/surnom", "**" + NAME + "**", true)
                .addField(":lien: DiscordTag", "**" + TAG + "**", true)
                .addField(":1234: ID", "**" + ID + "**", true)
                .addBlankField(false)
                .addField(":signal_strength: état actuel", "**" + STATUS + "**", true)
                .addField(":video_game: Jeu en cours", "**" + GAME + "**", true)
                .addField(":tools: Rôle", "**" + ROLES + "**", true)
                .addBlankField(false)
                .addField(":inbox_tray: date de connexion au serveur", "**" + GUILD_JOIN_DATE + "**", true)
                .addField(":beginner: date de création du compte", "**" + DISCORD_JOINED_DATE + "**", true)
                .addBlankField(false)
                .addField(":frame_photo: URL de l'icône", AVATAR, false);

        if (!AVATAR.equals("pas d'icône")) {
            eb.setAuthor(memb.getUser().getName() + TAG + " Informations utilisateur pour", null, AVATAR);
        }

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void execute(CommandEvent event) {
        Member memb;

        if (event.getArgs().length() > 0) {
            try {

                if (event.getMessage().getReferencedMessage().getMentions().getMembers().size() != 0) {
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
        String NAME = memb.getEffectiveName();
        String TAG = "#" + memb.getUser().getDiscriminator();
        String GUILD_JOIN_DATE = memb.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss"));
        String DISCORD_JOINED_DATE = memb.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss"));
        String ID = memb.getUser().getId();
        String STATUS = memb.getOnlineStatus().getKey().replace("offline", ":x: hors ligne").replace("dnd", ":red_circle: ne me réveille pas").replace("idle", "absent").replace("online", " :white_check_mark : En ligne");
        String ROLES;
        String GAME;
        String AVATAR = memb.getUser().getAvatarUrl();

        log.debug("\nusername:" + memb.getEffectiveName() + "\n" +
                "balise:" + memb.getUser().getDiscriminator() + "\n" +
                "Date et heure de la guilde:"
                + memb.getUser().getTimeCreated()
                .format(DateTimeFormatter.ofPattern("dd/MM/AAAA HH:mm:ss")) + "\n" +
                "ID utilisateur:" + memb.getUser().getId() + "\n" +
                "Statut en ligne :" + memb.getOnlineStatus());

        try {
            GAME = memb.getActivities().toString();
        } catch (Exception e) {
            GAME = "-/-";
        }

        StringBuilder ROLESBuilder = new StringBuilder();
        for (Role r : memb.getRoles()) {
            ROLESBuilder.append(r.getName()).append(", ");
        }
        ROLES = ROLESBuilder.toString();
        if (ROLES.length() > 0)
            ROLES = ROLES.substring(0, ROLES.length() - 2);
        else
            ROLES = "Le titre du poste n'existe pas sur ce serveur";

        if (AVATAR == null) {
            AVATAR = "pas d'icône";
        }

        eb.setAuthor(memb.getUser().getName() + TAG + "informations utilisateur pour", null, null)
                .addField(":pencil2: nom/surnom", "**" + NAME + "**", true)
                .addField(":lien: DiscordTag", "**" + TAG + "**", true)
                .addField(":1234: ID", "**" + ID + "**", true)
                .addBlankField(false)
                .addField(":signal_strength: état actuel", "**" + STATUS + "**", true)
                .addField(":video_game: Jeu en cours", "**" + GAME + "**", true)
                .addField(":tools: Rôle", "**" + ROLES + "**", true)
                .addBlankField(false)
                .addField(":inbox_tray: date de connexion au serveur", "**" + GUILD_JOIN_DATE + "**", true)
                .addField(":beginner: date de création du compte", "**" + DISCORD_JOINED_DATE + "**", true)
                .addBlankField(false)
                .addField(":frame_photo: URL de l'icône", AVATAR, false);

        if (!AVATAR.equals("pas d'icône")) {
            eb.setAuthor(memb.getUser().getName() + TAG + "informations utilisateur pour", null, AVATAR);
        }

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
