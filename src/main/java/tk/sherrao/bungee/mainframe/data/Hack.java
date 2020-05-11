package tk.sherrao.bungee.mainframe.data;

import java.time.Duration;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Hack {

    public final String name;
    public final Duration duration;
    public final TextComponent kickMessage;
    public final TextComponent joinMessage;
    public final String formattedTime;
    
    public Hack( final String name, final Duration duration, final String kickMessage, final String joinMessage, final String formattedTime ) {
        this.name = name;
        this.duration = duration;
        this.kickMessage = new TextComponent( ChatColor.translateAlternateColorCodes( '&', kickMessage ) );
        this.joinMessage = new TextComponent( ChatColor.translateAlternateColorCodes( '&', joinMessage ) );
        this.formattedTime = formattedTime;
        
    }
    
}