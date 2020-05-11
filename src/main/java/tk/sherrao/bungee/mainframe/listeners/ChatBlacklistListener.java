
package tk.sherrao.bungee.mainframe.listeners;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.sherrao.bungee.mainframe.BukkitCommunicator;
import tk.sherrao.bungee.mainframe.BungeeMainframe;

public class ChatBlacklistListener implements Listener {

    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;

    private List<String> blacklist;
    private String warning;
    private String format;

    public ChatBlacklistListener( BungeeMainframe pl ) {
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();

        this.blacklist = pl.getConfig().getStringList( "chat-blacklist" );
        this.warning = ChatColor.translateAlternateColorCodes( '&',
                pl.getMessages().getString( "chat-message-blacklisted" ) );
        this.format = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "swear-format" ) );

    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onChat( ChatEvent event ) {
        String[] message = event.getMessage().split( " " );
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if( !player.hasPermission( "mainframe.*" ) || !player.hasPermission( "mainframe.bypasschat" ) ) {
            for ( String word : message ) {
                for ( String swear : blacklist ) {
                    if ( word.equalsIgnoreCase( swear ) || word.contains( swear )
                            || event.getMessage().contains( swear ) ) {
                        event.setCancelled( true );
                        bukkit.sendData( player, "SendMessage", warning );
                        sendSwearAlert( player, swear );
                        return;

                    } else continue;

                }
            }

        } else return;
    }

    private void sendSwearAlert( ProxiedPlayer player, String swear ) {
        String out = format.replace( "[swear]", ChatColor.translateAlternateColorCodes( '&', swear ) )
                .replace( "[swearer]", player.getName() ).replace( "[server]", player.getServer().getInfo().getName() );

        for ( ProxiedPlayer p : pl.getProxy().getPlayers() ) {
            if ( p.hasPermission( "mainframe.*" ) || p.hasPermission( "mainframe.sc" ) )
                bukkit.sendData( p, "SendMessage", out );

            else continue;

        }

    }

    public BungeeMainframe getPlugin() {
        return pl;

    }

}