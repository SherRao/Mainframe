package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.HacksDatabase;
import tk.sherrao.bungee.mainframe.Player;
import tk.sherrao.bungee.mainframe.PlayerDatabase;
import tk.sherrao.bungee.mainframe.RefractionsManager;
import tk.sherrao.bungee.mainframe.data.Ban;
import tk.sherrao.bungee.mainframe.data.Hack;
import tk.sherrao.utils.strings.StringMultiJoiner;

public class BanCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    private HacksDatabase hacks;
    
    private String permMsg;
    private String usageMsg; 
    private String playerMsg;
    private String hackMsg;
    private String availableHacksMsg;
    private String alreadyBannedMsg;
    
    public BanCommand( BungeeMainframe pl ) {
        super( "ban" );
     
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        this.hacks = pl.getHacksDatabase();
        
        this.permMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" ) )
                .replace( "[usage]", "/ban <player> <refraction>" );
        this.playerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        this.hackMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-hack" ) );
        this.alreadyBannedMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "player-already-banned" ) );
        this.availableHacksMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "available-hacks" )
                .replace( "[hacks]", new StringMultiJoiner( ", ", "[ ", " ]" ) 
                        .add( hacks.getActiveHacks(), (hack) -> { return hack.name; } )
                        .toString() ) );
        
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender ||  sender.hasPermission( "mainframe.ban" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    if( target.getProxy() == null || !target.getProxy().hasPermission( "mainframe.*" ) || !target.getProxy().hasPermission( "mainframe.bypassban" ) ) {
                        if( args.length > 1 ) {
                            Hack hack = hacks.resolve( args[1] );
                            if( hack != null ) {
                                Ban ban = refractions.getActiveBan( target.getId() );
                                if( ban == null ) {
                                    refractions.ban( sender, target, hack );
                        
                                } else bukkit.sendData( sender, "SendMessage", alreadyBannedMsg );
                    
                            } else bukkit.sendData( sender, "SendMessage", hackMsg );
                    
                        } else bukkit.sendData( sender, "SendMessage", availableHacksMsg );
                    
                    } else bukkit.sendData( sender, "SendMessage", permMsg );
                        
                } else bukkit.sendData( sender, "SendMessage", playerMsg );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", permMsg );
    
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}