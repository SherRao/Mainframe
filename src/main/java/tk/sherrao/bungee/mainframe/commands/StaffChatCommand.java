package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.utils.strings.StringMultiJoiner;

public class StaffChatCommand extends Command {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    
    private String format;
    private String nopermsMsg;
    private String usageMsg;
    
    public StaffChatCommand( BungeeMainframe pl ) {
        super( "staffchat", null, "sc", "staffc", "schat" );
        
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        
        this.format = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "staff-chat-format" ) );
        this.nopermsMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" )
                .replace( "[usage]", "/staffchat <message>" ) );

    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.sc" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) 
                sendStaffChatMessage( sender, new StringMultiJoiner( " " )
                        .add( args )
                        .toString() );
                
            else bukkit.sendData( sender, "SendMessage", usageMsg );
            
        } else bukkit.sendData( sender, "SendMessage", nopermsMsg );
    
    }
    
    private void sendStaffChatMessage( CommandSender sender, String message ) {
        String str = ChatColor.translateAlternateColorCodes( '&', format.replace( "[message]", message ) );
        if( !(sender instanceof ProxiedPlayer) ) {
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            str = str.replace( "[sender]", console.getName() )
                    .replace( "[server]", "" );
            
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            str = str.replace( "[sender]", player.getDisplayName() )
                    .replace( "[server]", player.getServer().getInfo().getName() );
        
        }

        pl.getProxy().getConsole().sendMessage( new TextComponent( str ) );
        for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
            if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                bukkit.sendData( player, "SendMessage", str );
                
            else continue;
            
        }
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}