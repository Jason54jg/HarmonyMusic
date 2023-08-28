package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class InfosServeur extends SlashCommand {
    public InfosServeur(Bot bot) {
        this.name = "infoserveur";
        this.help = "Affiche des informations sur le serveur";
        this.guildOnly = true;
        this.category = new Category("Général");
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String nomServeur = event.getGuild().getName();
        String urlIcôneServeur = event.getGuild().getIconUrl();
        String idServeur = event.getGuild().getId();
        String propriétaireServeur = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String dateCréationServeur = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String nbRolesServeur = String.valueOf(event.getGuild().getRoles().size());
        String nbMembresServeur = String.valueOf(event.getGuild().getMembers().size());
        String nbCatégoriesServeur = String.valueOf(event.getGuild().getCategories().size());
        String nbCanauxTexteServeur = String.valueOf(event.getGuild().getTextChannels().size());
        String nbCanauxVocauxServeur = String.valueOf(event.getGuild().getVoiceChannels().size());
        String nbCanauxÉtapesServeur = String.valueOf(event.getGuild().getStageChannels().size());
        String nbCanauxForumServeur = String.valueOf(event.getGuild().getForumChannels().size());
        String localisationServeur = event.getGuild().getLocale().getNativeName();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Informations sur le serveur " + nomServeur, null, urlIcôneServeur);

        eb.addField("ID du serveur", idServeur, true);
        eb.addField("Langue principale du serveur", localisationServeur, true);
        eb.addField("Propriétaire du serveur", propriétaireServeur, true);
        eb.addField("Nombre de membres", nbMembresServeur, true);
        eb.addField("Nombre de rôles", nbRolesServeur, true);
        eb.addField("Nombre de catégories", nbCatégoriesServeur, true);
        eb.addField("Nombre de canaux de texte", nbCanauxTexteServeur, true);
        eb.addField("Nombre de canaux vocaux", nbCanauxVocauxServeur, true);
        eb.addField("Nombre de canaux d'étape", nbCanauxÉtapesServeur, true);
        eb.addField("Nombre de canaux du forum", nbCanauxForumServeur, true);

        eb.setFooter("Date de création du serveur : " + dateCréationServeur, null);

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void execute(CommandEvent event) {
        String nomServeur = event.getGuild().getName();
        String urlIcôneServeur = event.getGuild().getIconUrl();
        String idServeur = event.getGuild().getId();
        String propriétaireServeur = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String dateCréationServeur = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String nbRolesServeur = String.valueOf(event.getGuild().getRoles().size());
        String nbMembresServeur = String.valueOf(event.getGuild().getMembers().size());
        String nbCatégoriesServeur = String.valueOf(event.getGuild().getCategories().size());
        String nbCanauxTexteServeur = String.valueOf(event.getGuild().getTextChannels().size());
        String nbCanauxVocauxServeur = String.valueOf(event.getGuild().getVoiceChannels().size());
        String nbCanauxÉtapesServeur = String.valueOf(event.getGuild().getStageChannels().size());
        String nbCanauxForumServeur = String.valueOf(event.getGuild().getForumChannels().size());
        String localisationServeur = event.getGuild().getLocale().getNativeName();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Informations sur le serveur " + nomServeur, null, urlIcôneServeur);

        eb.addField("ID du serveur", idServeur, true);
        eb.addField("Langue principale du serveur", localisationServeur, true);
        eb.addField("Propriétaire du serveur", propriétaireServeur, true);
        eb.addField("Nombre de membres", nbMembresServeur, true);
        eb.addField("Nombre de rôles", nbRolesServeur, true);
        eb.addField("Nombre de catégories", nbCatégoriesServeur, true);
        eb.addField("Nombre de canaux de texte", nbCanauxTexteServeur, true);
        eb.addField("Nombre de canaux vocaux", nbCanauxVocauxServeur, true);
        eb.addField("Nombre de canaux d'étape", nbCanauxÉtapesServeur, true);
        eb.addField("Nombre de canaux du forum", nbCanauxForumServeur, true);

        eb.setFooter("Date de création du serveur : " + dateCréationServeur, null);

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
