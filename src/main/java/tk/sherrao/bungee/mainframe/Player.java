package tk.sherrao.bungee.mainframe;

import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Player {

    private ProxiedPlayer player;
    private final String username;
    private final UUID uuid;
    
    public Player( final ProxiedPlayer player ) {
        this.player = player;
        this.username = player.getName();
        this.uuid = player.getUniqueId();
        
    } 
    
    public Player( final String username, final UUID uuid ) {
        this.player = null;
        this.username = username;
        this.uuid = uuid;
        
    }
        
    public void updatePlayer( ProxiedPlayer player ) {
        this.player = player;
        
    }
    
    public ProxiedPlayer getProxy() {
        return player;

    }
    
    public String getUsername() {
        return username;
        
    }
    
    public UUID getId() {
        return uuid;
        
    }
    
}