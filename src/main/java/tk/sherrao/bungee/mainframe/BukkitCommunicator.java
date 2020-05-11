package tk.sherrao.bungee.mainframe;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BukkitCommunicator implements Listener {

    private BungeeMainframe pl;
    
    public BukkitCommunicator( BungeeMainframe pl ) {
        this.pl = pl;
        pl.getProxy().registerChannel( "Mainframe" );
        pl.getProxy().getPluginManager().registerListener( pl, this );
        
    }
    
    @EventHandler( priority = EventPriority.HIGH )
    public void getData( PluginMessageEvent event ) {
        if( event.getTag().equalsIgnoreCase( "Mainframe" ) ) {
            ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
            Server server = (Server) event.getSender();
            String subchannel = in.readUTF();
            if( subchannel.equals( "HackAlerts" ) ) {
                String message = in.readUTF()
                        .replace( "[server]", server.getInfo().getName() );
                
                pl.getProxy().getConsole().sendMessage( new TextComponent( message ) );
                for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                    if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.hacks" ) )
                        sendData( player, "SendMessage", message );
                        
                    else continue;
                    
                }
                
            } else return;
            
        } else return;
         
    }
    
    public void sendData( CommandSender to, String channel, String... data ) {
        if( to instanceof ConsoleCommandSender ) {
            for( String str : data )
                to.sendMessage( new TextComponent( str ) );
        
        } else {
            ProxiedPlayer player = (ProxiedPlayer) to;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream( stream );
            try {
                out.writeUTF( channel ); 
                out.writeUTF( player.getUniqueId().toString() );
                for( String msg : data )
                    out.writeUTF( msg );

            } catch ( IOException e ) { e.printStackTrace(); }
        
            player.getServer().sendData( "Mainframe", stream.toByteArray() );
        
        }
    }

    public void sendDataToAll( String channel, String... data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( stream );
        try {
            out.writeUTF( channel ); 
            for( String msg : data )
                out.writeUTF( msg );

        } catch ( IOException e ) { e.printStackTrace(); }
        
        for( ServerInfo server : pl.getProxy().getServers().values() ) 
            server.sendData( "Mainframe", stream.toByteArray() );

    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}