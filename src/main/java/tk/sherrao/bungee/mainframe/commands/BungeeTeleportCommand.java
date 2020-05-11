package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.Player;
import tk.sherrao.bungee.mainframe.PlayerDatabase;

public class BungeeTeleportCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    
    private String nopermsMsg;
    private String usageMsg; 
    private String invalidplayerMsg; 
    private String invalidServerMsg; 
    private String successMsg;
    
    public BungeeTeleportCommand( BungeeMainframe pl ) {
        super( "bungeeteleport", null, "btp", "bungeetp", "bteleport", "bungeecordtp", "bungeecordteleport" );
        
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        
        this.nopermsMsg =  ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" )
                .replace( "[usage]", "/btp <player> <server>" ) );
        this.invalidplayerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        this.invalidServerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-server" ) );
        this.successMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "command-success.btp" ) ); 
        
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.btp" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 1 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    ServerInfo server = pl.getProxy().getServerInfo( args[1] );
                    if( server != null ) {
                        target.getProxy().connect( server, Reason.COMMAND );
                        bukkit.sendData( sender, "SendMessage", successMsg
                                .replace( "[player]", target.getUsername() )
                                .replace( "[server]", server.getName() ) );
                
                    } else bukkit.sendData( sender, "SendMessage", invalidServerMsg );
                    
                } else bukkit.sendData( sender, "SendMessage", invalidplayerMsg );
                
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", nopermsMsg );
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}
