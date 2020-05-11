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

public class PlayerLookupCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    
    private RefractionsManager refractions;
    private String nopermsMsg; 
    private String usageMsg; 
    private String notPlayerMsg;
    
    public PlayerLookupCommand( BungeeMainframe pl ) {
        super( "playerlookup", null, "plookup", "pinfo", "playerinfo" );
        
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        
        this.refractions = pl.getRefrationsManager();
        this.nopermsMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" )
                .replace( "[usage]", "/plookup <player>" ) );
        this.notPlayerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.plookup" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                Player player = playerDb.getPlayer( args[0] );
                if( player != null ) 
                    refractions.sendPlayerDataTo( sender, player );
                     
                else bukkit.sendData( sender, "SendMessage", notPlayerMsg );
                
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
            
        } else bukkit.sendData( sender, "SendMessage", nopermsMsg );
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}