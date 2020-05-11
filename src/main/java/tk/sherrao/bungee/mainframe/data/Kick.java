package tk.sherrao.bungee.mainframe.data;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Kick {

    public final UUID targetUUID;
    public final UUID issuerUUID;
    public final String targetName;
    public final String issuerName;
    public final String reason;
    public final ZonedDateTime time;
    public final String server;
    
    public Kick( final UUID targetUUID, final UUID issuerUUID, final String targetName, final String issuerName, final String reason, final ZonedDateTime time, final String server ) {
        this.targetUUID = targetUUID;
        this.issuerUUID = issuerUUID;
        this.targetName = targetName;
        this.issuerName = issuerName;
        this.reason = reason;
        this.time = time;
        this.server =  server;
        
    }
    
}