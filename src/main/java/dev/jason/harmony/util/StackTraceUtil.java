package dev.jason.harmony.util;


import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
    public static void sendStackTrace(TextChannel channel, Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter(writer);
        throwable.printStackTrace(pWriter);
        pWriter.flush();
        channel.sendMessage("```\n" + writer.toString().substring(0, (2000 - 3)) + "...\n```").queue();
    }
}
