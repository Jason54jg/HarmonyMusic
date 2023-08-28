package dev.jason.harmony.slashcommands.music;

import com.jason.harmony.Bot;

public class SCSearchCmd extends SearchCmd {
    public SCSearchCmd(Bot bot) {
        super(bot);
        this.searchPrefix = "scsearch:";
        this.name = "scsearch";
        this.help = "Rechercher Soundcloud en utilisant la chaîne spécifiée";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
}