
package tk.sherrao.bungee.mainframe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.sherrao.bungee.mainframe.announcements.Announcer;
import tk.sherrao.bungee.mainframe.commands.BanCommand;
import tk.sherrao.bungee.mainframe.commands.BungeeTeleportCommand;
import tk.sherrao.bungee.mainframe.commands.GlobalExecuteCommand;
import tk.sherrao.bungee.mainframe.commands.KickCommand;
import tk.sherrao.bungee.mainframe.commands.MuteCommand;
import tk.sherrao.bungee.mainframe.commands.PlayerLookupCommand;
import tk.sherrao.bungee.mainframe.commands.ReportCommand;
import tk.sherrao.bungee.mainframe.commands.StaffChatCommand;
import tk.sherrao.bungee.mainframe.commands.UnbanCommand;
import tk.sherrao.bungee.mainframe.commands.UnmuteCommand;
import tk.sherrao.bungee.mainframe.listeners.BanListener;
import tk.sherrao.bungee.mainframe.listeners.ChatBlacklistListener;
import tk.sherrao.bungee.mainframe.listeners.CommandLogger;
import tk.sherrao.bungee.mainframe.listeners.MuteListener;

public class BungeeMainframe
        extends Plugin {

    private PlayerDatabase playerDb;
    private RefractionsManager refractionsMgr;
    private HacksDatabase hacksDb;
    private BukkitCommunicator bukkit;
    private Announcer announcer;
    private ReportTimeout reports;

    private File dataFolder;
    private ConfigurationProvider yamlProvider;

    private File configFile, messagesFile, refractionsFile, hacksFile, announcementsFile;
    private Configuration config, messages, refractions, hacks, announcements;

    public BungeeMainframe() {
        super();

    }

    @Override
    public void onLoad() {
        super.onLoad();

    }

    @Override
    public void onEnable() {
        super.onEnable();
        long start = System.currentTimeMillis();

        loadPreConfigs();
        loadConfigs();
        loadInfo();

        playerDb = new PlayerDatabase( this );
        bukkit = new BukkitCommunicator( this );
        hacksDb = new HacksDatabase( this );
        hacksDb.load();
        refractionsMgr = new RefractionsManager( this );
        refractionsMgr.load();
        announcer = new Announcer( this );
        announcer.load();
        reports = new ReportTimeout( this );

        super.getProxy().getPluginManager().registerCommand( this, new BungeeTeleportCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new GlobalExecuteCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new StaffChatCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new ReportCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new PlayerLookupCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new BanCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new UnbanCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new MuteCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new UnmuteCommand( this ) );
        super.getProxy().getPluginManager().registerCommand( this, new KickCommand( this ) );

        super.getProxy().getPluginManager().registerListener( this, playerDb );
        super.getProxy().getPluginManager().registerListener( this, new CommandLogger( this ) );
        super.getProxy().getPluginManager().registerListener( this, new BanListener( this ) );
        super.getProxy().getPluginManager().registerListener( this, new MuteListener( this ) );
        super.getProxy().getPluginManager().registerListener( this, new ChatBlacklistListener( this ) );

        super.getProxy().getScheduler().schedule( this, announcer, 2L, 1L, TimeUnit.SECONDS );
        super.getProxy().getScheduler().schedule( this, reports, 3L, 1L, TimeUnit.SECONDS );
        super.getProxy().getScheduler().schedule( this, refractionsMgr, 4L, 1L, TimeUnit.SECONDS );
        super.getProxy().getConsole().sendMessage( new TextComponent( 
                ChatColor.GREEN + "Loaded Mainframe in " + ChatColor.GOLD + (System.currentTimeMillis() - start) + " &ams!" ) );

    }

    @Override
    public void onDisable() {
        super.onDisable();

    }

    private void loadPreConfigs() {
        dataFolder = super.getDataFolder();
        if ( !dataFolder.exists() )
            dataFolder.mkdir();

        yamlProvider = ConfigurationProvider.getProvider( YamlConfiguration.class );

    }

    private void loadConfigs() {
        try {
            configFile = new File( super.getDataFolder(), "config.yml" );
            if ( !configFile.exists() ) {
                configFile.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "config.yml" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( configFile ) );
                ByteStreams.copy( in, out );
                out.close();
                in.close();

            }

            config = yamlProvider.load( configFile );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            messagesFile = new File( super.getDataFolder(), "messages.yml" );
            if ( !messagesFile.exists() ) {
                messagesFile.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "messages.yml" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( messagesFile ) );
                ByteStreams.copy( in, out );
                in.close();
                out.close();

            }

            messages = yamlProvider.load( messagesFile );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            refractionsFile = new File( super.getDataFolder(), "refractions.yml" );
            if ( !refractionsFile.exists() ) {
                refractionsFile.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "refractions.yml" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( refractionsFile ) );
                ByteStreams.copy( in, out );
                in.close();
                out.close();

            }

            refractions = yamlProvider.load( refractionsFile );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            hacksFile = new File( super.getDataFolder(), "hacks.yml" );
            if ( !hacksFile.exists() ) {
                hacksFile.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "hacks.yml" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( hacksFile ) );
                ByteStreams.copy( in, out );
                in.close();
                out.close();

            }

            hacks = yamlProvider.load( hacksFile );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            announcementsFile = new File( super.getDataFolder(), "announcements.yml" );
            if ( !announcementsFile.exists() ) {
                announcementsFile.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "announcements.yml" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( announcementsFile ) );
                ByteStreams.copy( in, out );
                out.close();
                in.close();

            }

            announcements = yamlProvider.load( announcementsFile );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    private void loadInfo() {
        try {
            File file = new File( super.getDataFolder(), "info.txt" );
            if ( !file.exists() ) {
                file.createNewFile();
                BufferedInputStream in = new BufferedInputStream( super.getResourceAsStream( "info.txt" ) );
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( file ) );
                ByteStreams.copy( in, out );
                out.close();
                in.close();

            } else return;

        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    @Deprecated
    public String __fixJSONColours( String text ) {
        String lastColour = "";
        String[] split = text.split( " " );
        StringJoiner sj = new StringJoiner( " " );
        for ( String str : split ) {
            if ( str.contains( "&" ) ) {
                int index = str.indexOf( "&" );
                lastColour = str.substring( index, index + 2 );

            } else str = lastColour + str;

            sj.add( str );

        }

        return ChatColor.translateAlternateColorCodes( '&', sj.toString() );

    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDb;
        
    }
    
    public RefractionsManager getRefrationsManager() {
        return refractionsMgr;

    }


    public HacksDatabase getHacksDatabase() {
        return hacksDb;

    }


    public BukkitCommunicator getBukkitCommunicator() {
        return bukkit;

    }


    public ReportTimeout getReportTimeout() {
        return reports;

    }


    public ConfigurationProvider getConfigProvider() {
        return yamlProvider;

    }


    public Configuration getConfig() {
        return config;

    }


    public Configuration getMessages() {
        return messages;

    }


    public Configuration getRefractions() {
        return refractions;

    }


    public Configuration getHacks() {
        return hacks;

    }

   
    public Configuration getAnnouncements() {
        return announcements;

    }

    public File getConfigFile() {
        return configFile;

    }

    public File getMessagesFile() {
        return messagesFile;

    }

    public File getRefractionsFile() {
        return refractionsFile;

    }

    public File getHacksFile() {
        return hacksFile;

    }

    public File getAnnouncementsFile() {
        return announcementsFile;

    }

}