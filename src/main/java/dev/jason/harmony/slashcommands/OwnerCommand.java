package dev.jason.harmony.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;

public abstract class OwnerCommand extends SlashCommand {
    public OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}
