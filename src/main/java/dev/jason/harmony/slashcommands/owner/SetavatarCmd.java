package dev.jason.harmony.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jason.harmony.Bot;
import com.jason.harmony.utils.OtherUtil;
import dev.jason.harmony.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SetavatarCmd extends OwnerCommand {
    public SetavatarCmd(Bot bot) {
        this.name = "setavatar";
        this.help = "Définit l'avatar du bot";
        this.arguments = "<url>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "image", "URL de l'image", true));
        this.options = options;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String url = event.getOption("image").getAsString();
        InputStream s = OtherUtil.imageFromUrl(url);
        if (s == null) {
            event.reply(event.getClient().getError() + " URL invalide ou manquante").queue();
        } else {
            try {
                event.getJDA().getSelfUser().getManager().setAvatar(Icon.from(s)).queue(
                        v -> event.reply(event.getClient().getSuccess() + "Avatar changé.").queue(),
                        t -> event.reply(event.getClient().getError() + "Échec de la définition de l'avatar.").queue());
            } catch (IOException e) {
                event.reply(event.getClient().getError() + "Échec du chargement à partir de l'URL fournie.").queue();
            }
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        String url;
        if (event.getArgs().isEmpty())
            if (!event.getMessage().getAttachments().isEmpty() && event.getMessage().getAttachments().get(0).isImage())
                url = event.getMessage().getAttachments().get(0).getUrl();
            else
                url = null;
        else
            url = event.getArgs();
        InputStream s = OtherUtil.imageFromUrl(url);
        if (s == null) {
            event.reply(event.getClient().getError() + "URL invalide ou manquante");
        } else {
            try {
                event.getSelfUser().getManager().setAvatar(Icon.from(s)).queue(
                        v -> event.reply(event.getClient().getSuccess() + "Avatar changé."),
                        t -> event.reply(event.getClient().getError() + "Échec de la définition de l'avatar."));
            } catch (IOException e) {
                event.reply(event.getClient().getError() + "Échec du chargement à partir de l'URL fournie.");
            }
        }
    }
}
