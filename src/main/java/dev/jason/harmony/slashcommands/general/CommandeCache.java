package dev.jason.harmony.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jason.harmony.Bot;
import com.jason.harmony.utils.FormatUtil;
import dev.jason.harmony.slashcommands.DJCommand;
import dev.jason.harmony.util.Cache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandeCache extends SlashCommand {
    private final Paginator.Builder constructeurPaginator;
    public Bot bot;

    public CommandeCache(Bot bot) {
        this.bot = bot;
        this.name = "cache";
        this.help = "Affiche les chansons stockées dans le cache.";
        this.guildOnly = true;
        this.category = new Category("Général");
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new SlashCommand[]{new CommandeSupprimer(bot), new CommandeAfficher(bot)};
        this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS};
        constructeurPaginator = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ignore) {
                    }
                })
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
            event.reply("Il n'y avait pas de chansons enregistrées dans le cache.");
            return;
        }
        int pagenum = 1;
        try {
            pagenum = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException ignore) {
        }

        List<Cache> cache = bot.getCacheLoader().GetCache(event.getGuild().getId());

        String[] chansons = new String[cache.size()];
        long total = 0;
        for (int i = 0; i < cache.size(); i++) {
            total += Long.parseLong(cache.get(i).getLength());
            chansons[i] = "`[" + FormatUtil.formatTime(Long.parseLong(cache.get(i).getLength())) + "]` **" + cache.get(i).getTitle() + "** - <@" + cache.get(i).getUserId() + ">";
        }
        long finTotal = total;
        constructeurPaginator.setText((i1, i2) -> obtenirTitreQueue(event.getClient().getSuccess(), chansons.length, finTotal))
                .setItems(chansons)
                .setUsers(event.getAuthor())
                .setColor(event.getSelfMember().getColor())
        ;
        constructeurPaginator.build().paginate(event.getChannel(), pagenum);
    }

    private String obtenirTitreQueue(String success, int longueurChansons, long total) {
        StringBuilder sb = new StringBuilder();

        return FormatUtil.filter(sb.append(success).append(" Liste des chansons en cache | ").append(longueurChansons)
                .append(" Chansons | `").append(FormatUtil.formatTime(total)).append("` ")
                .toString());
    }

    public static class CommandeSupprimer extends DJCommand {
        public CommandeSupprimer(Bot bot) {
            super(bot);
            this.name = "supprimer";
            this.aliases = new String[]{"dl", "effacer"};
            this.help = "Supprimez le cache stocké.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(CommandEvent event) {
            if (!bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
                event.reply("Le cache n'existe pas.");
                return;
            }

            try {
                bot.getCacheLoader().deleteCache(event.getGuild().getId());
            } catch (IOException e) {
                event.reply("Une erreur s'est produite lors de la suppression du cache.");
                e.printStackTrace();
                return;
            }
            event.reply("J'ai vidé le cache.");
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
                event.reply("Le cache n'existe pas.").queue();
                return;
            }

            try {
                bot.getCacheLoader().deleteCache(event.getGuild().getId());
            } catch (IOException e) {
                event.reply("Une erreur s'est produite lors de la suppression du cache.").queue();
                e.printStackTrace();
                return;
            }
            event.reply("J'ai vidé le cache.").queue();
        }
    }

    public class CommandeAfficher extends SlashCommand {
        private final Paginator.Builder constructeurPaginator;

        public CommandeAfficher(Bot bot) {
            this.name = "afficher";
            this.help = "Liste des chansons en cache.";
            this.guildOnly = true;
            this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS};
            constructeurPaginator = new Paginator.Builder()
                    .setColumns(1)
                    .setFinalAction(m -> {
                        try {
                            m.clearReactions().queue();
                        } catch (PermissionException ignore) {
                        }
                    })
                    .setItemsPerPage(10)
                    .waitOnSinglePage(false)
                    .useNumberedItems(true)
                    .showPageNumbers(true)
                    .wrapPageEnds(true)
                    .setEventWaiter(bot.getWaiter())
                    .setTimeout(1, TimeUnit.MINUTES);
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            if (!bot.getCacheLoader().cacheExists(event.getGuild().getId())) {
                event.reply("Il n'y avait pas de chansons enregistrées dans le cache.").queue();
                return;
            }
            int pagenum = 1;
            event.reply("Obtenez le cache.").queue();

            List<Cache> cache = bot.getCacheLoader().GetCache(event.getGuild().getId());

            String[] chansons = new String[cache.size()];
            long total = 0;
            for (int i = 0; i < cache.size(); i++) {
                total += Long.parseLong(cache.get(i).getLength());
                chansons[i] = "`[" + FormatUtil.formatTime(Long.parseLong(cache.get(i).getLength())) + "]` **" + cache.get(i).getTitle() + "** - <@" + cache.get(i).getUserId() + ">";
            }
            long finTotal = total;
            constructeurPaginator.setText((i1, i2) -> obtenirTitreQueue(event.getClient().getSuccess(), chansons.length, finTotal))
                    .setItems(chansons)
                    .setUsers(event.getUser())
                    .setColor(event.getMember().getColor());
            constructeurPaginator.build().paginate(event.getChannel(), pagenum);
        }
    }
}
