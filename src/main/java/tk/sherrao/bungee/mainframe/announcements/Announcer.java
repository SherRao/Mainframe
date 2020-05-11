package tk.sherrao.bungee.mainframe.announcements;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.RefractionsManager;
import tk.sherrao.utils.TimeUtils;

public class Announcer implements Runnable {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    private DateTimeFormatter time;

    private Configuration config;
    private List<Announcement> announcements;
    
    public Announcer( BungeeMainframe pl ) {
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        this.time = DateTimeFormatter.ofPattern( "HH:mm:ss" );

        this.config = pl.getAnnouncements();
        this.announcements = Collections.synchronizedList( new ArrayList<>() );

    }
    
    public void load() {
        for( String serverName : config.getKeys() ) {
            ServerInfo server = pl.getProxy().getServerInfo( serverName );
            if( server != null ) {
                for( String announcementName : config.getSection( serverName ).getKeys() ) {
                    String message = ChatColor.translateAlternateColorCodes( '&', config.getString( serverName + "." + announcementName + ".message" ) );
                    long offset = config.getLong( serverName + "." + announcementName + ".offset" );
                    long delay = config.getLong( serverName + "." + announcementName + ".delay" );
                    if( message != null )
                        announcements.add( new Announcement( server, message, offset, delay ) );
                
                    else continue; 
                
                }
            
            } else continue;
            
        } 
    }

    @Override
    public void run() {
        for( Announcement announcement : announcements ) {
            if( TimeUtils.isTimedOut( announcement.lastAnnouncement , announcement.delay * 1000 ) ) {
                announcement.lastAnnouncement = System.currentTimeMillis();
                String out = announcement.message
                        .replace( "[bans]", Integer.toString( refractions.getBans().size() ) )
                        .replace( "[time]", time.format( ZonedDateTime.now() ) );
                
                for( ProxiedPlayer player : announcement.server.getPlayers() )
                    bukkit.sendData( player, "SendMessage", out.replace( "[player]", player.getName() ) );
                    
            } else continue;
            
        }
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}
