package com.jason.harmony;

import com.jason.harmony.entities.Prompt;
import com.jason.harmony.utils.FormatUtil;
import com.jason.harmony.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class BotConfig {
    private final static String CONTEXT = "Config";
    private final static String START_TOKEN = "/// START OF HARMONY CONFIG ///";
    private final static String END_TOKEN = "/// END OF HARMONY CONFIG ///";
    private final Prompt prompt;
    private Path path = null;
    private String token;
    private String prefix;
    private String altprefix;
    private String helpWord;
    private String playlistsFolder;
    private String mylistfolder;
    private String publistFolder;
    private String successEmoji;
    private String warningEmoji;
    private String errorEmoji;
    private String loadingEmoji;
    private String searchingEmoji;
    private String ytEmail;
    private String ytPass;
    private String spClientId;
    private String spClientSecret;
    private boolean changeNickName, stayInChannel, pauseNoUsers, resumeJoined, stopNoUsers, songInGame, npImages, updatealerts, useEval, dbots, cosgyDevHost, helpToDm, autoStopQueueSave, auditCommands, officialInvite, useinvitecommand;
    private long owner, maxSeconds, aloneTimeUntilStop;
    private OnlineStatus status;
    private Activity game;
    private Config aliases, transforms;

    private boolean valid = false;

    public BotConfig(Prompt prompt) {
        this.prompt = prompt;
    }

    public void load() {
        valid = false;

        // Charger les paramètres à partir du fichier
        try {
            // Obtenir le chemin de configuration (la valeur par défaut est config.txt)
            path = OtherUtil.getPath(System.getProperty("config.file", System.getProperty("config", "config.txt")));
            if (path.toFile().exists()) {
                if (System.getProperty("config.file") == null)
                    System.setProperty("config.file", System.getProperty("config", path.toAbsolutePath().toString()));
                ConfigFactory.invalidateCaches();
            }

            // Chargé dans le fichier de configuration avec les valeurs par défaut ajoutées
            //Config config = ConfigFactory.parseFile(path.toFile()).withFallback(ConfigFactory.load());
            Config config = ConfigFactory.load();
            // valeur de consigne
            token = config.getString("token");
            prefix = config.getString("prefix");
            altprefix = config.getString("altprefix");
            helpWord = config.getString("help");
            owner = (config.getAnyRef("owner") instanceof String ? 0L : config.getLong("owner"));
            successEmoji = config.getString("success");
            warningEmoji = config.getString("warning");
            errorEmoji = config.getString("error");
            loadingEmoji = config.getString("loading");
            searchingEmoji = config.getString("searching");
            game = OtherUtil.parseGame(config.getString("game"));
            status = OtherUtil.parseStatus(config.getString("status"));
            stayInChannel = config.getBoolean("stayinchannel");
            songInGame = config.getBoolean("songinstatus");
            npImages = config.getBoolean("npimages");
            updatealerts = config.getBoolean("updatealerts");
            useEval = config.getBoolean("eval");
            maxSeconds = config.getLong("maxtime");
            aloneTimeUntilStop = config.getLong("alonetimeuntilstop");
            playlistsFolder = config.getString("playlistsfolder");
            mylistfolder = config.getString("mylistfolder");
            publistFolder = config.getString("publistfolder");
            aliases = config.getConfig("aliases");
            transforms = config.getConfig("transforms");
            dbots = owner == 334091398263341056L;


            // [Harmony]
            pauseNoUsers = config.getBoolean("pausenousers");
            resumeJoined = config.getBoolean("resumejoined");
            stopNoUsers = config.getBoolean("stopnousers");
            changeNickName = config.getBoolean("changenickname");
            helpToDm = config.getBoolean("helptodm");
            autoStopQueueSave = config.getBoolean("autostopqueuesave");
            auditCommands = config.getBoolean("auditcommands");
            officialInvite = config.getBoolean("officialinvite");
            useinvitecommand = config.getBoolean("useinvitecommand");
            ytEmail = config.getString("ytemail");
            ytPass = config.getString("ytpass");
            spClientId = config.getString("spclient");
            spClientSecret = config.getString("spsecret");


            cosgyDevHost = false;
            // [Harmony] End

            // we may need to write a new config file
            boolean write = false;

            // validate bot token
            if (token == null || token.isEmpty() || token.matches("(BOT_TOKEN_HERE|Collez votre jeton de bot ici)")) {
                token = prompt.prompt("Veuillez entrer votre jeton BOT."
                        + "\nVoici comment obtenir un jeton:"
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token."
                        + "\nJeton BOT: ");
                if (token == null) {
                    prompt.alert(Prompt.Level.WARNING, CONTEXT, "Aucun jeton n'a été saisi! Sortie.\n\nEmplacement du fichier de configuration: " + path.toAbsolutePath());
                    return;
                } else {
                    write = true;
                }
            }

            // validate bot owner
            if (owner <= 0) {
                try {
                    owner = Long.parseLong(prompt.prompt("L'ID utilisateur du propriétaire n'est pas défini ou n'est pas un ID valide."
                            + "\nVeuillez entrer l'ID utilisateur du propriétaire du bot."
                            + "\nObtenez votre ID utilisateur ici:"
                            + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID"
                            + "\nID utilisateur propriétaire: "));
                } catch (NumberFormatException | NullPointerException ex) {
                    owner = 0;
                }
                if (owner <= 0) {
                    prompt.alert(Prompt.Level.ERROR, CONTEXT, "ID utilisateur non valide! Fermeture.\n\nEmplacement du fichier de configuration:" + path.toAbsolutePath());
                    System.exit(0);
                } else {
                    write = true;
                }
            }

            if (write) {
                String original = OtherUtil.loadResource(this, "/reference.conf");
                String mod;
                if (original == null) {
                    mod = ("token = " + token + "\r\nowner = " + owner);
                } else {
                    mod = original.substring(original.indexOf(START_TOKEN) + START_TOKEN.length(), original.indexOf(END_TOKEN))
                            .replace("BOT_TOKEN_HERE", token).replace("Collez votre jeton de bot ici", token)
                            .replace("0 // OWNER ID", Long.toString(owner)).replace("Collez l'ID du propriétaire ici", Long.toString(owner))
                            .trim();
                }

                FileUtils.writeStringToFile(path.toFile(), mod, StandardCharsets.UTF_8);
            }

            // if we get through the whole config, it's good to go
            valid = true;
        } catch (ConfigException | IOException ex) {
            prompt.alert(Prompt.Level.ERROR, CONTEXT, ex + ": " + ex.getMessage() + "\n\nEmplacement du fichier de configuration: " + path.toAbsolutePath());
        }
    }

    public boolean isValid() {
        return valid;
    }

    public String getConfigLocation() {
        return path.toFile().getAbsolutePath();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getAltPrefix() {
        return "NONE".equalsIgnoreCase(altprefix) ? null : altprefix;
    }

    public String getToken() {
        return token;
    }

    public long getOwnerId() {
        return owner;
    }

    public String getSuccess() {
        return successEmoji;
    }

    public String getWarning() {
        return warningEmoji;
    }

    public String getError() {
        return errorEmoji;
    }

    public String getLoading() {
        return loadingEmoji;
    }

    public String getSearching() {
        return searchingEmoji;
    }

    public Activity getGame() {
        return game;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public String getHelp() {
        return helpWord;
    }

    public boolean getStay() {
        return stayInChannel;
    }

    public boolean getNoUserPause() {
        return pauseNoUsers;
    }

    public boolean getResumeJoined() {
        return resumeJoined;
    }

    public boolean getNoUserStop() {
        return stopNoUsers;
    }

    public long getAloneTimeUntilStop() {
        return aloneTimeUntilStop;
    }

    public boolean getChangeNickName() {
        return changeNickName;
    }

    public boolean getSongInStatus() {
        return songInGame;
    }

    public String getPlaylistsFolder() {
        return playlistsFolder;
    }

    public String getMylistfolder() {
        return mylistfolder;
    }

    public String getPublistFolder() {
        return publistFolder;
    }

    public boolean useUpdateAlerts() {
        return updatealerts;
    }

    public boolean useEval() {
        return useEval;
    }

    public boolean useNPImages() {
        return npImages;
    }

    public long getMaxSeconds() {
        return maxSeconds;
    }

    public String getMaxTime() {
        return FormatUtil.formatTime(maxSeconds * 1000);
    }

    public boolean isTooLong(AudioTrack track) {
        if (maxSeconds <= 0)
            return false;
        return Math.round(track.getDuration() / 1000.0) > maxSeconds;
    }

    public String[] getAliases(String command) {
        try {
            return aliases.getStringList(command).toArray(new String[0]);
        } catch (NullPointerException | ConfigException.Missing e) {
            return new String[0];
        }
    }

    public String getYouTubeEmailAddress() {
        return ytEmail;
    }

    public String getYouTubePassword() {
        return ytPass;
    }

    public String getSpotifyClientId(){return spClientId;}

    public String getSpotifyClientSecret(){return spClientSecret;}

    // [Harmony] End

    public boolean getHelpToDm() {
        return helpToDm;
    }

    public boolean getAutoStopQueueSave() {
        return autoStopQueueSave;
    }

    public boolean getAuditCommands() {
        return auditCommands;
    }

    public Config getTransforms() {
        return transforms;
    }

    public boolean isOfficialInvite() {
        return officialInvite;
    }

    public boolean isUseInviteCommand() {
        return useinvitecommand;
    }
}
