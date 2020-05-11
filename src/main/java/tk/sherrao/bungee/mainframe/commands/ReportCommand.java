package tk.sherrao.bungee.mainframe.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;
import tk.sherrao.bungee.mainframe.ReportTimeout;
import tk.sherrao.utils.strings.StringMultiJoiner;

public class ReportCommand extends Command {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    private ReportTimeout reports;
    
    private String format;
    private String timeoutMsg;
    private String nopermsMsg;
    private String usageMsg;
    
    public ReportCommand( BungeeMainframe pl ) {
        super( "report", null, "helpop" );
        
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        this.reports = pl.getReportTimeout();
        
        this.format = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "report-format" ) );
        this.timeoutMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "timeout" ) )
                .replace( "[time]", Long.toString( reports.getTimeout() ) );
        this.nopermsMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "no-perms") );
        this.usageMsg = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "wrong-command-usage" )
                .replace( "[usage]", "/report <message>" ) );

    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        if( sender instanceof ConsoleCommandSender || sender.hasPermission( "mainframe.report" ) || sender.hasPermission( "mainframe.*" ) ) {
            if( args.length > 0 ) {
                if( !(sender instanceof ConsoleCommandSender) ) { 
                    ProxiedPlayer player = (ProxiedPlayer) sender;
                    if( reports.allowedToReport( player ) ) {
                        reports.addPlayer( player );
                        sendReportMessage( player, new StringMultiJoiner( " " )
                                .add( args )
                                .toString() );
                
                    } else bukkit.sendData( sender, "SendMessage", timeoutMsg );
            
                } else sendReportMessage( sender, new StringMultiJoiner( " " )
                        .add( args )
                        .toString() );
            
            } else bukkit.sendData( sender, "SendMessage", usageMsg );
            
        } else bukkit.sendData( sender, "SendMessage", nopermsMsg );
            
    }
    
    private void sendReportMessage( CommandSender sender, String message ) {
        String str = format.replace( "[message]", ChatColor.translateAlternateColorCodes( '&', message ) );
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
            if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.seereport" ) )
                bukkit.sendData( player, "SendMessage", str );
        
            else continue;
            
        }
        
    }
    
    public BungeeMainframe getPlugin() {
        return pl;
        
    }
    
}
