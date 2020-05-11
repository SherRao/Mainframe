package tk.sherrao.bungee.mainframe.data;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Ban {

    public final UUID targetUUID;
    public final UUID issuerUUID;
    public final String targetName;
    public final String issuerName;
    public final Hack reason;
    public final int durationMultiplier;
    public final ZonedDateTime time;
    public final String server;
    public boolean isActive;
    
    public Ban( final UUID targetUUID, final UUID issuerUUID, final String targetName, final String issuerName, final Hack reason, final int durationMultiplier, final ZonedDateTime time, final String server, final boolean isActive ) {
        this.targetUUID = targetUUID;
        this.issuerUUID = issuerUUID;
        this.targetName = targetName;
        this.issuerName = issuerName;
        this.reason = reason;
        this.durationMultiplier = durationMultiplier;
        this.time = time;
        this.server = server;
        this.isActive = isActive;
        
    }
    
}