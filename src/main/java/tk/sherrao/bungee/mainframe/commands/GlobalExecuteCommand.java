package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.utils.strings.StringMultiJoiner;

public class GlobalExecuteCommand extends Command {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    
    private String nopermsMsg;
    private String usageMsg;
    private String successMsg;
    
    public GlobalExecuteCommand( BungeeMainframe pl ) {
        super( "globalexe", null, "gexe", "gcommand", "sall" );
        
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        
        this.nopermsMsg = ChatColor.translateAlternateColorCodes( '&',  pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" )
                .replace( "[usage]", "/gexe <command> [args]" ) );
        this.successMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "command-success.global-execute" ) );
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.gexe" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                bukkit.sendDataToAll( "GlobalExecute", sender.getName(), new StringMultiJoiner( " " )
                        .add( args )
                        .toString() );
                bukkit.sendData( sender, "SendMessage", successMsg );
                
            } else bukkit.sendData( sender, "SendMessage", usageMsg );

        } else bukkit.sendData( sender, "SendMessage", nopermsMsg );
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}