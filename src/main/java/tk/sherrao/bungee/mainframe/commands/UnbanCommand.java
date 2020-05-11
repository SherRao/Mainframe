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
import tk.sherrao.bungee.mainframe.data.Ban;

public class UnbanCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    
    private String permMsg;
    private String usageMsg; 
    private String playerMsg;
    private String alreadyUnbannedMsg;
    
    public UnbanCommand( BungeeMainframe pl ) {
        super( "unban", null, "pardon-ban", "pardonban" );
     
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        
        this.permMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" ) )
                .replace( "[usage]", "/unban <player>" );
        this.playerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        this.alreadyUnbannedMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "player-already-unbanned" ) );
        
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender ||  sender.hasPermission( "mainframe.unban" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    Ban ban = refractions.getActiveBan( target.getId() );
                    if( ban != null ) {
                        refractions.pardonBan( sender, ban );
                        
                    } else bukkit.sendData( sender, "SendMessage", alreadyUnbannedMsg );
                    
                } else bukkit.sendData( sender, "SendMessage", playerMsg );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", permMsg );
    
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}