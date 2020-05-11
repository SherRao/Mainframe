package tk.sherrao.bungee.mainframe.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.RefractionsManager;
import tk.sherrao.bungee.mainframe.data.Mute;

public class MuteListener implements Listener {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    
    private RefractionsManager refractions;
    private String mutedMsg;
    
    public MuteListener( BungeeMainframe pl ) {
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        
        this.refractions = pl.getRefrationsManager();
        this.mutedMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "muted" ) );
        
    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onChat( ChatEvent event ) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Mute mute = refractions.getActiveMute( player.getUniqueId() );
        if( mute != null ) {
            event.setCancelled( true );
            bukkit.sendData( player, "SendMessage", mutedMsg );
            
        } else return;
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
     
}