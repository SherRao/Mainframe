package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.Player;
import tk.sherrao.bungee.mainframe.PlayerDatabase;
import tk.sherrao.bungee.mainframe.RefractionsManager;

public class KickCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    
    private String permMsg;
    private String usageMsg; 
    private String playerMsg;
    
    public KickCommand( BungeeMainframe pl ) {
        super( "kick" );
     
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        
        this.permMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" ) )
                .replace( "[usage]", "/kick <player> <reason>" );
        this.playerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );

    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.kick" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 1 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    if( target.getProxy() == null || !target.getProxy().hasPermission( "mainframe.*" ) || !target.getProxy().hasPermission( "mainframe.bypasskick" ) ) {
                        String reason = "";
                        for( int i = 1; i < args.length; i++ )
                            reason += args[i] + " ";
                            
                        refractions.kick( sender, target, reason );
                    
                    } else bukkit.sendData( sender, "SendMessage", permMsg );
                    
                } else bukkit.sendData( sender, "SendMessage", playerMsg );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", permMsg );
    
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}