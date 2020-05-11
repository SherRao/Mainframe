package tk.sherrao.bungee.mainframe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerDatabase implements Listener {

    private final Object lock = new Object();
    
    private BungeeMainframe pl;
    private List<Player> players;
    private String authURL;
    
    public PlayerDatabase( BungeeMainframe pl ) {
        this.pl = pl;
        this.players = Collections.synchronizedList( new ArrayList<>() );
        this.authURL = "https://api.mojang.com/users/profiles/minecraft/";

    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerJoin( LoginEvent event ) {
        synchronized( lock ) {
            ProxiedPlayer proxy = (ProxiedPlayer) event.getConnection();
            UUID id = proxy.getUniqueId();
            for( Player p : players ) {
                if( p.getId().equals( id ) ) {
                    p.updatePlayer( proxy );
                    return;
                    
                } else continue;
                
            }
            
            players.add( new Player( proxy ) );

        }
    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerLeave( PlayerDisconnectEvent event ) {
        synchronized( lock ) {
            ProxiedPlayer proxy = event.getPlayer();
            for( Iterator<Player> it = players.iterator(); it.hasNext(); ) {
                Player p = it.next();
                if( p.getId().equals( proxy.getUniqueId() ) )
                    it.remove(); 
                
                else continue;
                
            }
            
        }
    }
    
    public Player getPlayer( ProxiedPlayer proxy ) {
        Player player = getPlayer( proxy.getUniqueId() );
        if( player != null && player.getProxy() != null )
            return player;
        
        else return null;
        
    }
    
    public Player getPlayer( String name ) {
        synchronized( lock ) {
            for( Player p : players ) {
                if( p.getUsername().equalsIgnoreCase( name ) )
                    return p;
                
                else continue;
                
            }
            
            UUID id = resolveUsername( name );
            if( id != null ) {
                Player player = getPlayer( id );
                if( player == null ) {
                    player = new Player( name, id );
                    players.add( player );
                    return player;
                    
                } else return player;
                
            } else 
                return null;
            
        }
    }

    public Player getPlayer( UUID id ) {
        synchronized( lock ) {
            for( Player p : players ) {
                if( p.getId().equals( id ) )
                    return p;
                
                else continue;
                
            }
            
            return null;
                        
        }
    }
    
    public UUID resolveUsername( String username ) {
        ProxiedPlayer player = pl.getProxy().getPlayer( username );
        if( player == null ) {
            try {
                URL url = new URL( authURL + username );
                BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
                String line;
                while( (line = in.readLine()) != null ) {
                    String uuid = line.substring( 7, 39 );
                    in.close();
                    
                    return UUID.fromString( uuid.substring( 0, 8 ) + "-" + 
                            uuid.substring( 8, 12 ) + "-" + 
                            uuid.substring( 12, 16 ) + "-" + 
                            uuid.substring( 16, 20 ) + "-" +
                            uuid.substring( 20, 32 ) );

                }
                
            } catch( IOException e ) { 
                pl.getLogger().warning( "Error while trying to establish a connection with the Mojang authentication servers!" );
                pl.getLogger().warning( "They could be down, or the local network on this machine could not be working!" );
                e.printStackTrace();
                
            }
                
        } else 
            return player.getUniqueId();
        
        return null;
        
    }
    
}