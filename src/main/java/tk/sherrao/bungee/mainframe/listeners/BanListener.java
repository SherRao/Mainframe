package tk.sherrao.bungee.mainframe.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.RefractionsManager;
import tk.sherrao.bungee.mainframe.data.Ban;

public class BanListener implements Listener {
    
    private BungeeMainframe pl;
    private RefractionsManager refractions;

    public BanListener( BungeeMainframe pl ) {
       this.pl = pl;
       this.refractions = pl.getRefrationsManager();

    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerJoin( LoginEvent event ) {
        Ban ban = refractions.getActiveBan( event.getConnection().getUniqueId() );
        if( ban != null ) {
            event.setCancelled( true );
            event.setCancelReason( ban.reason.joinMessage );
            
        } else return;
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}
