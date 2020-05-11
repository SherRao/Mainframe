package tk.sherrao.bungee.mainframe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.sherrao.utils.TimeUtils;

public class ReportTimeout implements Runnable {

    private final Object lock = new Object();
    
    private BungeeMainframe pl;
    private Map<ProxiedPlayer, Long> reporters;
    private long timeoutSeconds;
    private long timeoutMilli;
    
    public ReportTimeout( BungeeMainframe pl ) {
        this.pl = pl;
        this.reporters = Collections.synchronizedMap( new HashMap<>() );
        this.timeoutSeconds = pl.getConfig().getLong( "report-timeout" );
        this.timeoutMilli = timeoutSeconds * 1000;
        
    }
    
    @Override
    public void run() {
        synchronized( lock ) {
            for( Iterator<Entry<ProxiedPlayer, Long>> it = reporters.entrySet().iterator(); it.hasNext(); ) {
                Entry<ProxiedPlayer, Long> entry = it.next();
                long time = entry.getValue();
                if( TimeUtils.isTimedOut( time, timeoutMilli ) )
                    it.remove();
                
                else continue;
                
            }
            
        }
    }
    
    public void addPlayer( ProxiedPlayer reporter ) {
        synchronized( lock ) {
            reporters.put( reporter, System.currentTimeMillis() );
            
        }
    }

    public boolean allowedToReport( ProxiedPlayer reporter ) {
        synchronized( lock ) {
            return !reporters.containsKey( reporter );
            
        }
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
    public long getTimeout() {
        return timeoutSeconds;
        
    }
    
}