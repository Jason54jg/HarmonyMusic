package dev.jason.harmony.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jason.harmony.Bot;
import com.jason.harmony.audio.AudioHandler;
import dev.jason.harmony.slashcommands.DJCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ForceRemoveCmd extends DJCommand {
    public ForceRemoveCmd(Bot bot) {
        super(bot);
        this.name = "forceremove";
        this.help = "Supprime l'entrée de l'utilisateur spécifié de la file d'attente de lecture";
        this.arguments = "<utilisateur>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "utilisateur", "utilisateur", true));
        this.options = options;

    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Je dois mentionner l'utilisateur!");
            return;
        }

        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.replyError("Rien n'attend pour jouer !");
            return;
        }


        User target;
        List<Member> found = FinderUtil.findMembers(event.getArgs(), event.getGuild());

        if (found.isEmpty()) {
            event.replyError("Utilisateur non trouvé!");
            return;
        } else if (found.size() > 1) {
            OrderedMenu.Builder builder = new OrderedMenu.Builder();
            for (int i = 0; i < found.size() && i < 4; i++) {
                Member member = found.get(i);
                builder.addChoice("**" + member.getUser().getName() + "**#" + member.getUser().getDiscriminator());
            }

            builder
                    .setSelection((msg, i) -> removeAllEntries(found.get(i - 1).getUser(), event))
                    .setText("Plusieurs utilisateurs trouvés:")
                    .setColor(event.getSelfMember().getColor())
                    .useNumbers()
                    .setUsers(event.getAuthor())
                    .useCancelButton(true)
                    .setCancel((msg) -> {
                    })
                    .setEventWaiter(bot.getWaiter())
                    .setTimeout(1, TimeUnit.MINUTES)

                    .build().display(event.getChannel());

            return;
        } else {
            target = found.get(0).getUser();
        }

        removeAllEntries(target, event);

    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        if (!checkDJPermission(event.getClient(), event)) {
            event.reply(event.getClient().getWarning() + "Impossible d'exécuter en raison d'un manque de privilèges.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.reply(event.getClient().getError() + "Rien n'attend pour jouer !").queue();
            return;
        }

        User target = event.getOption("utilisateur").getAsUser();
        int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
        if (count == 0) {
            event.reply(event.getClient().getWarning() + "**" + target.getName() + "Aucune chanson en file d'attente pour **!").queue();
        } else {
            event.reply(event.getClient().getSuccess() + "**" + target.getName() + "**#" + target.getDiscriminator() + count + " chansons supprimées.").queue();
        }
    }

    private void removeAllEntries(User target, CommandEvent event) {
        int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
        if (count == 0) {
            event.replyWarning("**" + target.getName() + "Aucune chanson en file d'attente pour **!");
        } else {
            event.replySuccess("**" + target.getName() + "**#" + target.getDiscriminator() + count + " chansons supprimées.");
        }
    }
}