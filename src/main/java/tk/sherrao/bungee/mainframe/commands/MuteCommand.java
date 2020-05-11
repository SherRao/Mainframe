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

public class MuteCommand extends Command {

    private BungeeMainframe pl;
    private PlayerDatabase playerDb;
    private BukkitCommunicator bukkit;
    private RefractionsManager refractions;
    
    private String permMsg;
    private String usageMsg; 
    private String playerMsg;
    private String durationMsg;
    private String alreadyMutedMsg;
    
    public MuteCommand( BungeeMainframe pl ) {
        super( "mute" );
     
        this.pl = pl;
        this.playerDb = pl.getPlayerDatabase();
        this.bukkit = pl.getBukkitCommunicator();
        this.refractions = pl.getRefrationsManager();
        
        this.permMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms" ) );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" ) )
                .replace( "[usage]", "/mute <player> <duration seconds> <reason>" );
        this.playerMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-player" ) );
        this.durationMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "invalid-target-duration" ) );
        this.alreadyMutedMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "player-already-muted" ) );

    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.mute" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 2 ) {
                Player target = playerDb.getPlayer( args[0] );
                if( target != null ) {
                    if( target.getProxy() == null || !target.getProxy().hasPermission( "mainframe.*" ) || !target.getProxy().hasPermission( "mainframe.bypassmute" ) ) {
                        try {
                            long duration = Long.parseLong( args[1] );
                            String reason = "";
                            for( int i = 2; i < args.length; i++ )
                                reason += args[i] + " ";
                            
                            Mute mute = refractions.getActiveMute( target.getId() );
                            if( mute == null ) {
                                refractions.mute( sender, target, reason, duration );
                            
                            } else bukkit.sendData( sender, "SendMessage", alreadyMutedMsg );
                    
                        } catch( NumberFormatException e ) { bukkit.sendData( sender, "SendMessage", durationMsg ); }
                
                    } else bukkit.sendData( sender, "SendMessage", permMsg );
                        
                } else bukkit.sendData( sender, "SendMessage", playerMsg );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
        
        } else bukkit.sendData( sender, "SendMessage", permMsg );
    
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}