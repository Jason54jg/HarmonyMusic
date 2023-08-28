package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jason.harmony.Bot;
import com.jason.harmony.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.FileUpload;

public class DebugCmd extends OwnerCommand {
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version",
            "java.runtime.name", "java.runtime.version", "java.specification.version", "os.arch", "os.name"};

    private final Bot bot;

    public DebugCmd(Bot bot) {
        this.bot = bot;
        this.name = "debug";
        this.help = "Afficher les informations de débogage";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Propriétés du système :");
        for (String key : PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nInformations sur Harmony :")
                .append("\n  Version = ").append(OtherUtil.getCurrentVersion())
                .append("\n  Owner = ").append(bot.getConfig().getOwnerId())
                .append("\n  Préfixe = ").append(bot.getConfig().getPrefix())
                .append("\n  AltPréfixe = ").append(bot.getConfig().getAltPrefix())
                .append("\n  MaxSecondes = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  ImagesNP = ").append(bot.getConfig().useNPImages())
                .append("\n  ChansonEnStatut = ").append(bot.getConfig().getSongInStatus())
                .append("\n  ResterDansLeSalon = ").append(bot.getConfig().getStay())
                .append("\n  UtiliserEval = ").append(bot.getConfig().useEval())
                .append("\n  AlertesMisesÀJour = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\nInformations sur la dépendance :")
                .append("\n  Version de JDA = ").append(JDAInfo.VERSION)
                .append("\n  Version de JDA-Utilities = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Version de Lavaplayer = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\nInformations d'exécution :")
                .append("\n  Mémoire totale = ").append(total)
                .append("\n  Mémoire utilisée = ").append(used);
        sb.append("\n\nInformations sur Discord :")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  Guildes = ").append(event.getJDA().getGuildCache().size())
                .append("\n  Utilisateurs = ").append(event.getJDA().getUserCache().size());
        sb.append("\nSi vous souhaitez envoyer ce fichier au développeur, veuillez l'envoyer sans modification.")
                .append("\nCe fichier ne contient aucune donnée pouvant être utilisée pour identifier vos informations personnelles, prendre le contrôle de votre compte, etc.");

        if (event.isFromGuild() || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            event.reply("Informations de débogage :").queue();
            event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "informations_debogage.txt")).queue();
        } else {
            event.reply("Informations de débogage : ```\n" + sb + "\n```").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Propriétés système :");
        for (String key : PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nInformations sur Harmony :")
                .append("\n  Version = ").append(OtherUtil.getCurrentVersion())
                .append("\n  Owner = ").append(bot.getConfig().getOwnerId())
                .append("\n  Préfixe = ").append(bot.getConfig().getPrefix())
                .append("\n  AltPréfixe = ").append(bot.getConfig().getAltPrefix())
                .append("\n  MaxSecondes = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  ImagesNP = ").append(bot.getConfig().useNPImages())
                .append("\n  ChansonEnStatut = ").append(bot.getConfig().getSongInStatus())
                .append("\n  ResterDansLeSalon = ").append(bot.getConfig().getStay())
                .append("\n  UtiliserEval = ").append(bot.getConfig().useEval())
                .append("\n  AlertesMisesÀJour = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\nInformations sur la dépendance :")
                .append("\n  Version de JDA = ").append(JDAInfo.VERSION)
                .append("\n  Version de JDA-Utilities = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Version de Lavaplayer = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\nInformations d'exécution :")
                .append("\n  Mémoire totale = ").append(total)
                .append("\n  Mémoire utilisée = ").append(used);
        sb.append("\n\nInformations sur Discord :")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  Guildes = ").append(event.getJDA().getGuildCache().size())
                .append("\n  Utilisateurs = ").append(event.getJDA().getUserCache().size());
        sb.append("\nSi vous souhaitez envoyer ce fichier au développeur, veuillez l'envoyer sans modification.");

        if (event.isFromType(ChannelType.PRIVATE)
                || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
            event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "informations_debogage.txt")).queue();
        else
            event.reply("Informations de débogage : ```\n" + sb + "\n```");
    }
}
