package tk.sherrao.bungee.mainframe.data;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Mute {

    public final UUID targetUUID;
    public final UUID issuerUUID;
    public final String targetName;
    public final String issuerName;
    public final String reason;
    public final ZonedDateTime time;
    public final Duration duration;
    public final String server;
    public boolean isActive;
    
    public Mute( final UUID targetUUID, final UUID issuerUUID, final String targetName, final String issuerName, final String reason, final ZonedDateTime time, final Duration duration, final String server, final boolean isActive ) {
        this.targetUUID = targetUUID;
        this.issuerUUID = issuerUUID;
        this.targetName = targetName;
        this.issuerName = issuerName;
        this.reason = reason;
        this.time = time;
        this.duration = duration;
        this.server = server;
        this.isActive = isActive;
        
    }
    
}