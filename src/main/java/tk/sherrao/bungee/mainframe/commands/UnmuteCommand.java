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
import tk.sherrao.bungee.mainframe.data.Mute;

public class UnmuteCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    
    private String permMsg;
    private String usageMsg; 
    private String playerMsg;
    private String alreadyUnmutedMsg;
    
    public UnmuteCommand( BungeeMainframe pl ) {
        super( "unmute", null, "pardon-mute", "pardonmute" );
     
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        
        this.permMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" ) )
                .replace( "[usage]", "/unmute <player>" );
        this.playerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        this.alreadyUnmutedMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "player-already-unmuted" ) );

    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender ||  sender.hasPermission( "mainframe.unmute" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    Mute mute = refractions.getActiveMute( target.getId() );
                    if( mute != null ) {
                        refractions.pardonMute( sender, mute );
                        
                    } else bukkit.sendData( sender, "SendMessage", alreadyUnmutedMsg );
                    
                } else bukkit.sendData( sender, "SendMessage", playerMsg );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", permMsg );
    
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}