package tk.sherrao.bungee.mainframe.announcements;

import net.md_5.bungee.api.config.ServerInfo;

public class Announcement {

    public final ServerInfo server;
    public final String message;
    public final long offset;
    public final long delay;
    public long lastAnnouncement;
    
    protected Announcement( final ServerInfo server, final String message, final long offset, final long delay ) {
        this.server = server;
        this.message = message;
        this.offset = offset;
        this.delay = delay;
        this.lastAnnouncement = System.currentTimeMillis() + (offset * 1000);
        
    }
    
}