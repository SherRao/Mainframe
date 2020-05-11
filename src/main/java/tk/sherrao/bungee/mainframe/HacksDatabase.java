package tk.sherrao.bungee.mainframe;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import tk.sherrao.bungee.mainframe.data.Hack;

public class HacksDatabase {

    private BungeeMainframe pl;
    private Configuration config;
    private List<Hack> hacks;
    
    public HacksDatabase( BungeeMainframe pl ) {
        this.pl = pl;
        this.config = pl.getHacks();
        this.hacks = new ArrayList<>();
        
    }
    
    public void load() {
        for( String hack : config.getKeys() ) {
            String name = config.getString( hack + ".name" );
            Duration duration = Duration.ofSeconds( config.getLong( hack + ".duration" ) );
            String kickMessage = config.getString( hack + ".kick-message" );
            String joinMessage = config.getString( hack + ".join-message" );
            String formattedTime = config.getString( hack + ".formatted-time" );
            
            hacks.add( new Hack( name, duration, kickMessage, joinMessage, formattedTime ) );
            
        }
    }
    
    public Hack resolve( String name ) {
        for( Hack hack : hacks ) {
            if( hack.name.equalsIgnoreCase( name ) )
                return hack;
            
            else continue;
                
        }
        
        return null;
        
    }
    
    public List<Hack> getActiveHacks() {
        return hacks;
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}
