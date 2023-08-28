package com.jason.harmony.utils;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class FormatUtil {
    public static String getStacktraceByString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static String formatTime(long duration) {
        if (duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration / 1000.0);
        long hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        long minutes = seconds / 60;
        seconds %= 60;
        return (hours > 0 ? hours + ":" : "") + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static String formatTime(int duration) {
        if (duration == Integer.MAX_VALUE)
            return "LIVE";
        int seconds = duration;
        int hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        int minutes = seconds / 60;
        seconds %= 60;
        return (hours > 0 ? hours + ":" : "") + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static String progressBar(double percent) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 12; i++)
            if (i == (int) (percent * 12))
                str.append("\uD83D\uDD18"); // 🔘
            else
                str.append("▬");
        return str.toString();
    }

    public static String volumeIcon(int volume) {
        if (volume == 0)
            return "\uD83D\uDD07"; // 🔇
        if (volume < 30)
            return "\uD83D\uDD08"; // 🔈
        if (volume < 70)
            return "\uD83D\uDD09"; // 🔉
        return "\uD83D\uDD0A";     // 🔊
    }

    public static String listOfTChannels(List<TextChannel> list, String query) {
        StringBuilder out = new StringBuilder(" \"" + query + "\" correspondant dans plusieurs canaux de texte. :");
        for (int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (<#").append(list.get(i).getId()).append(">)");
        if (list.size() > 6)
            out.append("\n**et ").append(list.size() - 6).append(" etc...**");
        return out.toString();
    }

    public static String listOfVChannels(List<VoiceChannel> list, String query) {
        StringBuilder outBuilder = new StringBuilder(" \"" + query + "\" correspondant sur plusieurs canaux vocaux.:");
        for (int i = 0; i < 6 && i < list.size(); i++)
            outBuilder.append("\n - ").append(list.get(i).getAsMention()).append(" (ID:").append(list.get(i).getId()).append(")");
        String out = outBuilder.toString();
        if (list.size() > 6)
            out += "\n**et " + (list.size() - 6) + " etc...**";
        return out;
    }

    public static String listOfRoles(List<Role> list, String query) {
        StringBuilder outBuilder = new StringBuilder(" \"" + query + "\" correspondant dans plusieurs canaux de texte:");
        for (int i = 0; i < 6 && i < list.size(); i++)
            outBuilder.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        String out = outBuilder.toString();
        if (list.size() > 6)
            out += "\n** et " + (list.size() - 6) + " etc...**";
        return out;
    }

    public static String filter(String input) {
        return input.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim(); // cyrillic letter e
    }
}
