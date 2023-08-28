package com.jason.harmony.utils;

import com.jason.harmony.Harmony;
import com.jason.harmony.entities.Prompt;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OtherUtil {
    public final static String NEW_VERSION_AVAILABLE = "Une nouvelle version de Harmony est disponible!\n"
            + "Version actuelle: %s\n"
            + "Dernière version: %s\n\n"
            + "Veuillez télécharger la dernière version sur https://github.com/Jason54jg/Harmony/releases/latest";
    private final static String WINDOWS_INVALID_PATH = "c:\\windows\\system32\\";

    public static Path getPath(String path) {
        Path result = Paths.get(path);
        // special logic to prevent trying to access system32
        if (result.toAbsolutePath().toString().toLowerCase().startsWith(WINDOWS_INVALID_PATH)) {
            try {
                result = Paths.get(new File(Harmony.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + File.separator + path);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String loadResource(Object clazz, String name) {
        try {
            return readString(clazz.getClass().getResourceAsStream(name));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String readString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream into = new ByteArrayOutputStream();
        byte[] buf = new byte[32768];
        for (int n; 0 < (n = inputStream.read(buf)); ) {
            into.write(buf, 0, n);
        }
        into.close();
        return into.toString(StandardCharsets.UTF_8);
    }

    public static InputStream imageFromUrl(String url) {
        if (url == null)
            return null;
        try {
            URL u = new URL(url);
            URLConnection urlConnection = u.openConnection();
            urlConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
            return urlConnection.getInputStream();
        } catch (IOException | IllegalArgumentException ignore) {
        }
        return null;
    }

    public static Activity parseGame(String game) {
        if (game == null || game.trim().isEmpty() || game.trim().equalsIgnoreCase("default"))
            return null;
        String lower = game.toLowerCase();
        if (lower.startsWith("playing"))
            return Activity.playing(makeNonEmpty(game.substring(7).trim()));
        if (lower.startsWith("listening to"))
            return Activity.listening(makeNonEmpty(game.substring(12).trim()));
        if (lower.startsWith("listening"))
            return Activity.listening(makeNonEmpty(game.substring(9).trim()));
        if (lower.startsWith("watching"))
            return Activity.watching(makeNonEmpty(game.substring(8).trim()));
        if (lower.startsWith("streaming")) {
            String[] parts = game.substring(9).trim().split("\\s+", 2);
            if (parts.length == 2) {
                return Activity.streaming(makeNonEmpty(parts[1]), "https://twitch.tv/" + parts[0]);
            }
        }
        return Activity.playing(game);
    }

    public static String makeNonEmpty(String str) {
        return str == null || str.isEmpty() ? "\u200B" : str;
    }

    public static OnlineStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty())
            return OnlineStatus.ONLINE;
        OnlineStatus st = OnlineStatus.fromKey(status);
        return st == null ? OnlineStatus.ONLINE : st;
    }

    public static String checkVersion(Prompt prompt) {
        // Get current version number
        String version = getCurrentVersion();

        // Check for new version
        String latestVersion = getLatestVersion();

        if (latestVersion != null && !latestVersion.equals(version) && Harmony.CHECK_UPDATE) {
            prompt.alert(Prompt.Level.WARNING, "Version", String.format(NEW_VERSION_AVAILABLE, version, latestVersion));
        }

        // Return the current version
        return version;
    }

    public static String getCurrentVersion() {
        if (Harmony.class.getPackage() != null && Harmony.class.getPackage().getImplementationVersion() != null)
            return Harmony.class.getPackage().getImplementationVersion();
        else
            return "不明";
    }

    public static String getLatestVersion() {
        try {
            Response response = new OkHttpClient.Builder().build()
                    .newCall(new Request.Builder().get().url("https://api.github.com/repos/Jason54jg/Harmony/releases/latest").build())
                    .execute();
            ResponseBody body = response.body();
            if (body != null) {
                try (Reader reader = body.charStream()) {
                    JSONObject obj = new JSONObject(new JSONTokener(reader));
                    return obj.getString("tag_name");
                } finally {
                    response.close();
                }
            } else
                return null;
        } catch (IOException | JSONException | NullPointerException ex) {
            return null;
        }
    }
}