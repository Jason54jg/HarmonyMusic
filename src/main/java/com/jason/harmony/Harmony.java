package com.jason.harmony;

import com.github.lalyos.jfiglet.FigletFont;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jason.harmony.entities.Prompt;
import com.jason.harmony.gui.GUI;
import com.jason.harmony.settings.SettingsManager;
import com.jason.harmony.utils.OtherUtil;
import dev.jason.harmony.slashcommands.admin.*;
import dev.jason.harmony.slashcommands.dj.*;
import dev.jason.harmony.slashcommands.general.*;
import dev.jason.harmony.slashcommands.listeners.AuditCommande;
import dev.jason.harmony.slashcommands.music.*;
import dev.jason.harmony.slashcommands.owner.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Harmony {
    public final static String PLAY_EMOJI = "▶"; // ▶
    public final static String PAUSE_EMOJI = "⏸"; // ⏸
    public final static String STOP_EMOJI = "⏹"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.MESSAGE_CONTENT}; // , GatewayIntent.MESSAGE_CONTENT
    public static boolean CHECK_UPDATE = true;
    public static boolean COMMAND_AUDIT_ENABLED = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // startup log
        Logger log = getLogger("Startup");

        try {
            System.out.println(FigletFont.convertOneLine("Harmony v" + OtherUtil.getCurrentVersion()) + "\n" + "by Jason54");
        } catch (IOException e) {
            System.out.println("Harmony v" + OtherUtil.getCurrentVersion() + "\nby Jason54");
        }


        // create prompt to handle startup
        Prompt prompt = new Prompt("Harmony", "Passez en mode nogui. Vous pouvez lancer manuellement en mode nogui en incluant le drapeau -Dnogui=true.");

        // check deprecated nogui mode (new way of setting it is -Dnogui=true)
        for (String arg : args)
            if ("-nogui".equalsIgnoreCase(arg)) {
                prompt.alert(Prompt.Level.WARNING, "GUI", "Le drapeau -nogui est obsolète."
                        + "Utilisez le drapeau -Dnogui=true avant le nom du jar. Exemple: java -jar -Dnogui=true Harmony.jar");
            } else if ("-nocheckupdates".equalsIgnoreCase(arg)) {
                CHECK_UPDATE = false;
                log.info("Vérification des mises à jour désactivée");
            } else if ("-auditcommands".equalsIgnoreCase(arg)) {
                COMMAND_AUDIT_ENABLED = true;
                log.info("Enregistrement activé des commandes exécutées.");
            }

        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);

        if (!System.getProperty("java.vm.name").contains("64"))
            prompt.alert(Prompt.Level.WARNING, "Java Version", "Vous utilisez une version Java non prise en charge. Veuillez utiliser la version 64 bits de Java.");

        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();

        if (!config.isValid())
            return;


        if (config.getAuditCommands()) {
            COMMAND_AUDIT_ENABLED = true;
            log.info("Enregistrement activé des commandes exécutées.");
        }

        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        Bot.INSTANCE = bot;

        CommandeAPropos aboutCommand = new CommandeAPropos(Color.BLUE.brighter(),
                "[Harmony(v" + version + ")](https://github.com/Jason54jg/Harmony)",
                new String[]{"Lecture de musique de haute qualité", "Technologie FairQueue™"},
                RECOMMENDED_PERMS);
        aboutCommand.setEstAuteur(false);
        aboutCommand.setCaractereRemplacement("\uD83C\uDFB6"); // 🎶

        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .useHelpBuilder(false)
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .setListener(new AuditCommande());

        if (config.isOfficialInvite()) {
            cb.setServerInvite("https://discord.gg/Fpm9qvKbbV");
        }

        // Implémentation des commandes slash
        List<SlashCommand> slashCommandList = new ArrayList<>() {{
            add(new CommandeAide(bot));
            add(aboutCommand);
            if (config.isUseInviteCommand()) {
                add(new CommandeInvitation());
            }
            add(new CommandePing());
            add(new ParametresCmd(bot));
            // General
            add(new InfosServeur(bot));
            add(new InfosUtilisateur());
            add(new CommandeCache(bot));
            // Music
            add(new ParolesCmd(bot));
            add(new CmdLectureEnCours(bot));
            add(new PlayCmd(bot));
            add(new SpotifyCmd(bot));
            add(new PlaylistsCmd(bot));
            add(new MylistCmd(bot));
            add(new QueueCmd(bot));
            add(new RemoveCmd(bot));
            add(new SearchCmd(bot));
            add(new SCSearchCmd(bot));
            add(new ShuffleCmd(bot));
            add(new SkipCmd(bot));
            add(new VolumeCmd(bot));
            // DJ
            add(new ForceRemoveCmd(bot));
            add(new ForceskipCmd(bot));
            add(new NextCmd(bot));
            add(new MoveTrackCmd(bot));
            add(new PauseCmd(bot));
            add(new PlaynextCmd(bot));
            add(new RepeatCmd(bot));
            add(new SkipToCmd(bot));
            add(new PlaylistCmd(bot));
            add(new StopCmd(bot));
            // Admin
            add(new CmdPréfixe(bot));
            add(new CmdDéfinirDJ(bot));
            add(new SkipratioCmd(bot));
            add(new CmdDéfinirSalonTexte(bot));
            add(new CmdDéfinirSalonVocal(bot));
            add(new CmdListeAutoLecture(bot));
            add(new CommandeListeServeurs(bot));
            // Owner
            add(new DebugCmd(bot));
            add(new CommandeDefinirAvatar(bot));
            add(new CommandeDefinirJeu(bot));
            add(new CommandeDefinirNom(bot));
            add(new CommandeDefinirStatut(bot));
            add(new PublistCmd(bot));
            add(new CommandeArret(bot));
            add(new LeaveCmd(bot));
        }};

        cb.addCommands(slashCommandList.toArray(new Command[0]));
        cb.addSlashCommands(slashCommandList.toArray(new SlashCommand[0]));

        if (config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if (config.getStatus() != OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if (config.getGame() == null)
            cb.setActivity(Activity.playing(config.getPrefix() + config.getHelp() + "Obtenez de l'aide avec"));
        else if (config.getGame().getName().toLowerCase().matches("(none|なし)")) {
            cb.setActivity(null);
            nogame = true;
        } else
            cb.setActivity(config.getGame());
        if (!prompt.isNoGUI()) {
            try {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } catch (Exception e) {
                log.error("Échec de l'ouverture de l'interface graphique. Causes possibles:\n"
                        + "en cours d'exécution sur le serveur\n"
                        + "fonctionnement dans un environnement sans écran\n"
                        + "Pour masquer cette erreur, exécutez en mode sans interface graphique avec l'indicateur -Dnogui=true.");
            }
        }

        log.info(config.getConfigLocation() + " Configuration chargée à partir de");

        // attempt to log in and start
        try {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.ONLINE_STATUS)
                    .setActivity(nogame ? null : Activity.playing("Chargement..."))
                    .setStatus(config.getStatus() == OnlineStatus.INVISIBLE || config.getStatus() == OnlineStatus.OFFLINE
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        } catch (InvalidTokenException ex) {
            prompt.alert(Prompt.Level.ERROR, "Harmony", ex + "\n" +
                    "Assurez-vous que vous modifiez le bon fichier de configuration. Échec de la connexion avec le jeton du bot." +
                    "Veuillez saisir un jeton de bot valide (PAS DE SECRET CLIENT!)\n" +
                    "Emplacement du fichier de configuration: " + config.getConfigLocation());
            System.exit(1);
        } catch (IllegalArgumentException ex) {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", "Certains paramètres ne sont pas valides:" + ex + "\n" +
                    "Emplacement du fichier de configuration: " + config.getConfigLocation());
            System.exit(1);
        }
    }
}
