package tk.sherrao.bungee.mainframe.listeners;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.sherrao.bungee.mainframe.BungeeMainframe;

public class CommandLogger implements Listener {

    private BungeeMainframe pl;
    private File folder, file;
    private String format;
    private PrintWriter out;
    
    public CommandLogger( BungeeMainframe pl ) {
        this.pl = pl;
        try { 
            this.folder = new File( pl.getDataFolder(), "logs" );
            if( !folder.exists() ) 
                folder.mkdir();
            
            String timestamp = LocalDateTime.now().toString()
                    .replace( "-", "" )
                    .replace( "-", "" )
                    .replace( ".", "" )
                    .replace( ":", "" );
            this.file = new File( folder, timestamp + ".yml" );
            if( !file.exists() ) 
                file.createNewFile();
        
            this.format = this.pl.getConfig().getString( "logging-format" );
            this.out = new PrintWriter( file );
        
        } catch( IOException e ) { e.printStackTrace(); }
        
    }
    
    @EventHandler( priority = EventPriority.NORMAL )
    public void onChat( ChatEvent event ) {
        if( !event.isCommand() || event.getSender() instanceof ConsoleCommandSender )
            return;
        
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        out.println( format.replace( "[time]", LocalTime.now().toString() )
                .replace( "[server]", player.getServer().getInfo().getName() )
                .replace( "[player]", player.getName() )
                .replace( "[message]", event.getMessage() ) );
        out.flush();
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
     
}