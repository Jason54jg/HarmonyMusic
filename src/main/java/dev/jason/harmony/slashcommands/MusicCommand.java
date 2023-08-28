package dev.jason.harmony.slashcommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import com.jason.harmony.settings.Settings;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MusicCommand extends SlashCommand {
    protected final Bot bot;
    protected boolean bePlaying;
    protected boolean beListening;
    Logger log = LoggerFactory.getLogger("MusicCommand");

    public MusicCommand(Bot bot) {
        this.bot = bot;
        this.guildOnly = true;
        this.category = new Category("Musique");
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel channel = settings.getTextChannel(event.getGuild());
        bot.getPlayerManager().setUpHandler(event.getGuild());
        if (bePlaying && !((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).isMusicPlaying(event.getJDA())) {
            event.reply(event.getClient().getError() + "Vous devez être en train de jouer pour utiliser les commandes.").queue();
            return;
        }
        if (beListening) {
            AudioChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();

            if (current == null)
                current = settings.getVoiceChannel(event.getGuild());
            GuildVoiceState userState = event.getMember().getVoiceState();

            if (!userState.inAudioChannel() || userState.isDeafened() || (current != null && !userState.getChannel().equals(current))) {
                event.reply(event.getClient().getError() + String.format("Vous devez être dans %s pour utiliser cette commande !", (current == null ? "un canal audio" : "**" + current.getName() + "**"))).queue();
                return;
            }
            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                try {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                } catch (PermissionException ex) {
                    event.reply(event.getClient().getError() + String.format("Impossible de se connecter à **%s** !", userState.getChannel().getName())).queue();
                    return;
                }
                if (userState.getChannel().getType() == ChannelType.STAGE) {
                    event.getTextChannel().sendMessage(event.getClient().getWarning() + String.format("Vous avez rejoint le canal de scène. Vous devez inviter manuellement l'orateur à utiliser %s dans le canal de scène.", event.getGuild().getSelfMember().getNickname())).queue();
                }
            }
        }

        doCommand(event);
    }

    @Override
    protected void execute(CommandEvent event) {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel channel = settings.getTextChannel(event.getGuild());
        if (channel != null && !event.getTextChannel().equals(channel)) {
            try {
                event.getMessage().delete().queue();
            } catch (PermissionException ignore) {
            }
            event.replyInDm(event.getClient().getError() + String.format("Les commandes ne peuvent être exécutées que sur %s.", channel.getAsMention()));
            return;
        }
        bot.getPlayerManager().setUpHandler(event.getGuild()); // inutile de vérifier constamment cela par la suite

        if (bePlaying && !((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).isMusicPlaying(event.getJDA())) {
            event.reply(event.getClient().getError() + "La musique doit être en train de jouer pour utiliser les commandes.");
            return;
        }
        if (beListening) {
            AudioChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();

            if (current == null)
                current = settings.getVoiceChannel(event.getGuild());
            GuildVoiceState userState = event.getMember().getVoiceState();
            if (!userState.inAudioChannel() || userState.isDeafened() || (current != null && !userState.getChannel().equals(current))) {
                event.replyError(String.format("Vous devez être dans %s pour utiliser cette commande !", (current == null ? "un canal audio" : "**" + current.getName() + "**")));
                return;
            }
            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                try {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                } catch (PermissionException ex) {
                    event.reply(event.getClient().getError() + String.format("Impossible de se connecter à **%s** !", userState.getChannel().getName()));
                    return;
                }
                if (userState.getChannel().getType() == ChannelType.STAGE) {
                    event.getTextChannel().sendMessage(event.getClient().getWarning() + String.format("Vous avez rejoint le canal de scène. Vous devez inviter manuellement l'orateur à utiliser %s dans le canal de scène.", event.getGuild().getSelfMember().getNickname())).queue();
                }
            }
        }

        doCommand(event);
    }

    public abstract void doCommand(CommandEvent event);

    public abstract void doCommand(SlashCommandEvent event);
}
