package dev.jason.harmony.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jlyrics.LyricsClient;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.MusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class ParolesCmd extends MusicCommand {
    private final LyricsClient lClient = new LyricsClient();

    public ParolesCmd(Bot bot) {
        super(bot);
        this.name = "paroles";
        this.arguments = "[titre de la chanson]";
        this.help = "Afficher les paroles des chansons";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "nom de la chanson", false));
        this.options = options;
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        event.getChannel().sendTyping().queue();
        String title;
        if (event.getOption("name").getAsString().isEmpty()) {
            AudioHandler sendingHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (sendingHandler.isMusicPlaying(event.getJDA()))
                title = sendingHandler.getPlayer().getPlayingTrack().getInfo().title;
            else {
                event.reply(event.getClient().getError() + "Impossible d'utiliser cette commande car aucune chanson n'est en cours de lecture.").queue();
                return;
            }
        } else
            title = event.getOption("name").getAsString();
        lClient.getLyrics(title).thenAccept(lyrics ->
        {
            if (lyrics == null) {
                event.reply(event.getClient().getError() + "`" + title + "` : Paroles introuvables" + (event.getOption("name").getAsString().isEmpty() ? ". Essayez d'entrer le nom de la chanson manuellement (`/lyrics [nom de la chanson]`)." : ".")).queue();
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(lyrics.getAuthor())
                    .setColor(event.getMember().getColor())
                    .setTitle(lyrics.getTitle(), lyrics.getURL());
            if (lyrics.getContent().length() > 15000) {
                event.reply(event.getClient().getWarning() + " Chanson trouvée avec des paroles pour `" + title + "`, mais elles peuvent être incorrectes : " + lyrics.getURL()).queue();
            } else if (lyrics.getContent().length() > 2000) {
                String content = lyrics.getContent().trim();
                while (content.length() > 2000) {
                    int index = content.lastIndexOf("\n\n", 2000);
                    if (index == -1)
                        index = content.lastIndexOf("\n", 2000);
                    if (index == -1)
                        index = content.lastIndexOf(" ", 2000);
                    if (index == -1)
                        index = 2000;
                    event.replyEmbeds(eb.setDescription(content.substring(0, index).trim()).build()).queue();
                    content = content.substring(index).trim();
                    eb.setAuthor(null).setTitle(null, null);
                }
                event.replyEmbeds(eb.setDescription(content).build()).queue();
            } else
                event.replyEmbeds(eb.setDescription(lyrics.getContent()).build()).queue();
        });
    }

    @Override
    public void doCommand(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        String title;
        if (event.getArgs().isEmpty()) {
            AudioHandler sendingHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (sendingHandler.isMusicPlaying(event.getJDA()))
                title = sendingHandler.getPlayer().getPlayingTrack().getInfo().title;
            else {
                event.replyError("Impossible d'utiliser cette commande car aucune chanson n'est en cours de lecture.");
                return;
            }
        } else
            title = event.getArgs();
        lClient.getLyrics(title).thenAccept(lyrics ->
        {
            if (lyrics == null) {
                event.replyError("`" + title + "` : Paroles introuvables" + (event.getArgs().isEmpty() ? ". Essayez d'entrer le titre de la chanson manuellement (`/lyrics [nom de la chanson]`)." : "."));
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(lyrics.getAuthor())
                    .setColor(event.getSelfMember().getColor())
                    .setTitle(lyrics.getTitle(), lyrics.getURL());
            if (lyrics.getContent().length() > 15000) {
                event.replyWarning("Chanson trouvée avec des paroles pour `" + title + "`, mais elles peuvent être incorrectes : " + lyrics.getURL());
            } else if (lyrics.getContent().length() > 2000) {
                String content = lyrics.getContent().trim();
                while (content.length() > 2000) {
                    int index = content.lastIndexOf("\n\n", 2000);
                    if (index == -1)
                        index = content.lastIndexOf("\n", 2000);
                    if (index == -1)
                        index = content.lastIndexOf(" ", 2000);
                    if (index == -1)
                        index = 2000;
                    event.reply(eb.setDescription(content.substring(0, index).trim()).build());
                    content = content.substring(index).trim();
                    eb.setAuthor(null).setTitle(null, null);
                }
                event.reply(eb.setDescription(content).build());
            } else
                event.reply(eb.setDescription(lyrics.getContent()).build());
        });
    }
}
