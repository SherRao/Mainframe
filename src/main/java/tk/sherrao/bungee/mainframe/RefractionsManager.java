package tk.sherrao.bungee.mainframe;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.config.Configuration;
import tk.sherrao.bungee.mainframe.data.Ban;
import tk.sherrao.bungee.mainframe.data.Hack;
import tk.sherrao.bungee.mainframe.data.Kick;
import tk.sherrao.bungee.mainframe.data.Mute;
import tk.sherrao.utils.TimeUtils;
import tk.sherrao.utils.strings.StringMultiJoiner;

public class RefractionsManager implements Runnable {

    private final Object lock = new Object();
    
    private BungeeMainframe pl;
    private BukkitCommunicator bukkit;
    private HacksDatabase hacks;
    private DateTimeFormatter time;
    
    private String banFormat;
    private String unbanFormat;
    private String kickFormat;
    private String muteFormat;
    private String unmuteFormat;

    private String loadingDataMessage;
    private String lookupNoMuteFormat, lookupNoKickFormat, lookupNoBanFormat;
    private List<String> lookupMuteFormat, lookupKickFormat, lookupBanFormat, lookupFormat;
    
    private File refractionsFile;
    private Configuration refractions;
    
    private List<Ban> bans;
    private List<Kick> kicks;
    private List<Mute> mutes;
    
    public RefractionsManager( BungeeMainframe pl ) {
        this.pl = pl;
        this.bukkit = pl.getBukkitCommunicator();
        this.hacks = pl.getHacksDatabase();
        this.time = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );
        
        this.banFormat = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "ban-format" ) );
        this.unbanFormat = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "unban-format" ) );
        this.kickFormat = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "kick-format" ) );
        this.muteFormat = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "mute-format" ) );
        this.unmuteFormat = ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "unmute-format" ) );
        
        this.loadingDataMessage = ChatColor.translateAlternateColorCodes( '&', pl.getMessages().getString( "command-success.player-info" ) );
        this.lookupNoMuteFormat =  ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "player-lookup.no-mutes" ) );
        this.lookupNoKickFormat =  ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "player-lookup.no-kicks" ) );
        this.lookupNoBanFormat =  ChatColor.translateAlternateColorCodes( '&', pl.getConfig().getString( "player-lookup.no-bans" ) );
        this.lookupMuteFormat =  pl.getConfig().getStringList( "player-lookup.mute-format" );
        this.lookupKickFormat =  pl.getConfig().getStringList( "player-lookup.kick-format" );
        this.lookupBanFormat =  pl.getConfig().getStringList( "player-lookup.ban-format" );
        this.lookupFormat =  pl.getConfig().getStringList( "player-lookup.format" );

        this.refractionsFile = pl.getRefractionsFile();
        this.refractions = pl.getRefractions();
        
        this.bans = Collections.synchronizedList( new ArrayList<>() );
        this.kicks = Collections.synchronizedList( new ArrayList<>() );
        this.mutes = Collections.synchronizedList( new ArrayList<>() );

    }
    
    public void load() {
        synchronized( lock ) {
            for( String id : refractions.getKeys() ) {
                UUID targetUUID = UUID.fromString( id );
                Configuration cbans = refractions.getSection( id + ".bans" );
                Configuration ckicks = refractions.getSection( id + ".kicks" );
                Configuration cmutes = refractions.getSection( id + ".mutes" );

                for( String ban : cbans.getKeys() ) {
                    UUID issuerUUID = null;
                    try {
                        issuerUUID = UUID.fromString( cbans.getString( ban + ".issuer-uuid" ) );

                    } catch( IllegalArgumentException ignored ) {}

                    String issuerName = cbans.getString( ban + ".issuer-name" );
                    String targetName = cbans.getString( ban + ".target-name" );
                    String server = cbans.getString( ban + ".server" );
                    Hack reason = hacks.resolve( cbans.getString( ban + ".reason" ) );
                    int durationMultiplier = cbans.getInt( ban + ".duration-multiplier" );
                    ZonedDateTime time = epochToTime( Long.valueOf( ban ) );
                    boolean isActive = cbans.getBoolean( ban + ".is-active" );
                    
                    bans.add( new Ban( targetUUID, issuerUUID, targetName, issuerName, reason, durationMultiplier, time, server, isActive ) );
                    
                }
                
                for( String kick : ckicks.getKeys() ) {
                    UUID issuerUUID = null;
                    try {
                        issuerUUID = UUID.fromString( ckicks.getString( kick + ".issuer-uuid" ) );
                    
                    } catch( IllegalArgumentException ignored ) {}
                    
                    String issuerName = ckicks.getString( kick + ".issuer-name" );
                    String targetName = ckicks.getString( kick + ".target-name" );
                    String server = ckicks.getString( kick + ".server" );
                    String reason = ckicks.getString( kick + ".reason" );
                    ZonedDateTime time = epochToTime( Long.valueOf( kick ) );
                    
                    kicks.add( new Kick( targetUUID, issuerUUID, targetName, issuerName, reason, time, server ) );
                    
                }
                
                for( String mute : cmutes.getKeys() ) {
                    UUID issuerUUID = null;
                    try {
                        issuerUUID = UUID.fromString( cmutes.getString( mute + ".issuer-uuid" ) );
                    
                    } catch( IllegalArgumentException ignored ) {}
                    
                    String issuerName = cmutes.getString( mute + ".issuer-name" );
                    String targetName = cmutes.getString( mute + ".target-name" );
                    String server = cmutes.getString( mute + ".server" );
                    String reason = cmutes.getString( mute + ".reason" );
                    Duration duration = Duration.ofSeconds( cmutes.getLong( mute + ".duration" ) );
                    ZonedDateTime time = epochToTime( Long.valueOf( mute ) );
                    boolean isActive = cmutes.getBoolean( mute + ".is-active" );

                    mutes.add( new Mute( targetUUID, issuerUUID, targetName, issuerName, reason, time, duration, server, isActive ) );
                    
                }
                
            }            
        }
    }
    
    @Override
    public void run() {
        synchronized( lock ) {
            for( Ban ban : bans ) {
                if( TimeUtils.isTimedOut( ban.time.toEpochSecond() * 1000, ban.reason.duration.toMillis() * ban.durationMultiplier ) )
                    ban.isActive = false;
                
                else continue;
                
            }
            
            for( Mute mute : mutes ) {
                if( TimeUtils.isTimedOut( mute.time.toEpochSecond() * 1000, mute.duration.toMillis() ) )
                    mute.isActive = false;
                
                else continue;
                
            }
            
        }
    }
     
    public void sendPlayerDataTo( CommandSender to, Player about ) {
        bukkit.sendData( to, "SendMessage", loadingDataMessage.replace( "[player]", about.getUsername() ) );
        pl.getProxy().getScheduler().runAsync( pl, () -> {
            List<Ban> playerBans = getBansFor( about );
            List<Kick> playerKicks = getKicksFor( about );
            List<Mute> playerMutes = getMutesFor( about );

            StringMultiJoiner bansOutput = new StringMultiJoiner( "\n" )
                    .setEmptyValue( lookupNoBanFormat );
            for( Ban ban : playerBans ) {
                StringMultiJoiner sj = new StringMultiJoiner( "\n" );
                for( String line : lookupBanFormat )
                    sj.add( line
                            .replace( "[time]", "\n" + time.format( ban.time ) )
                            .replace( "[issuer]", ban.issuerName )
                            .replace( "[reason]", ban.reason.name )
                            .replace( "[server]", ban.server )
                            .replace( "[duration]", Long.toString(ban.durationMultiplier * ban.reason.duration.getSeconds() ) )
                            .replace( "[active]", Boolean.toString( ban.isActive ) ) );
                
                bansOutput.add( sj );
                
            }
            
            StringMultiJoiner kicksOutput = new StringMultiJoiner( "\n" )
                    .setEmptyValue( lookupNoKickFormat );
            for( Kick kick : playerKicks ) {
                StringMultiJoiner sj = new StringMultiJoiner( "\n" );
                for( String line : lookupKickFormat )
                    sj.add( line
                            .replace( "[time]", "\n" + time.format( kick.time ) )
                            .replace( "[issuer]", kick.issuerName )
                            .replace( "[reason]", kick.reason )
                            .replace( "[server]", kick.server ) );
                
                kicksOutput.add( sj );
                
            }
            
            StringMultiJoiner mutesOutput = new StringMultiJoiner( "\n" )
                    .setEmptyValue( lookupNoMuteFormat );
            for( Mute mute : playerMutes ) {
                StringMultiJoiner sj = new StringMultiJoiner( "\n" );
                for( String line : lookupMuteFormat )
                    sj.add( line
                            .replace( "[time]", "\n" + time.format( mute.time ) )
                            .replace( "[issuer]", mute.issuerName )
                            .replace( "[reason]", mute.reason )
                            .replace( "[server]", mute.server ) 
                            .replace( "[duration]", Long.toString( mute.duration.getSeconds() ) )
                            .replace( "[active]", Boolean.toString( mute.isActive ) ) );
    
                mutesOutput.add( sj );
                
            }
            
            bukkit.sendData( to, "SendMessage", ChatColor.translateAlternateColorCodes( '&', new StringMultiJoiner( "\n" ).add( lookupFormat, str -> { 
                return str.replace( "[player]", about.getUsername() )
                        .replace( "[bans]", bansOutput )
                        .replace( "[kicks]", kicksOutput )
                        .replace( "[mutes]", mutesOutput ); 
                
            } ).toString() ) );
            
        } );
        
    }
    
    /**
    public void __sendPlayerDataTo( CommandSender to, ProxiedPlayer about ) {
        bukkit.sendData( to, "SendMessage", loadingDataMessage.replace( "[player]", about.getName() ) );
        
        pl.getProxy().getScheduler().runAsync( pl, () -> {
            StringMultiJoiner sj = new StringMultiJoiner( "\n" );
            sj.add( ChatColor.BLACK + "" + ChatColor.BOLD + "##################################" );
            sj.add( ChatColor.AQUA + "Showing refraction history for " + about.getName() );
            sj.add( "" );
            
            List<Ban> pBans = getBansFor( about );
            List<Kick> pKicks = getKicksFor( about );
            List<Mute> pMutes = getMutesFor( about );
            
            if( pBans.size() > 0 ) {
                sj.add( "  " + ChatColor.GOLD + "" + ChatColor.BOLD + "Bans:" );
                for( Ban ban : pBans ) {
                    sj.add( ChatColor.RED + "\t" + time.format( ban.time ) + ":" );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Issuer: " + ChatColor.GOLD + ban.issuerUUID.toString() );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Reason: " + ChatColor.GOLD + ban.reason.name );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Server: " + ChatColor.GOLD + pl.getProxy().getServerInfo( ban.server ).getName() );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Duration: " + ChatColor.GOLD + ((ban.reason.duration.toMillis() / 1000) * ban.durationMultiplier) );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Still Banned?: " + ChatColor.GOLD + (TimeUtils.isTimedOut(ban.time.toEpochMilli(), ban.reason.duration.toMillis())) );
                    sj.add( "" );
                    
                }
                 
            } else sj.add( "\t" + ChatColor.GOLD + "" + ChatColor.BOLD + "Bans: " + ChatColor.GREEN + "No Data :D" );

            if( pKicks.size() > 0 ) {
                sj.add( "" );
                sj.add( "  " + ChatColor.GOLD + "" + ChatColor.BOLD + "Kicks:" );
                for( Kick kick : pKicks ) {
                    sj.add( ChatColor.RED + "\t" + time.format( kick.time ) + ":" );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Issuer: " + ChatColor.GOLD + kick.issuerUUID.toString() );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Reason: " + ChatColor.GOLD + kick.reason );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Server: " + ChatColor.GOLD + pl.getProxy().getServerInfo( kick.server ).getName() );
                    sj.add( "" );
                    
                }
                 
            } else sj.add( "\t" + ChatColor.GOLD + "" + ChatColor.BOLD + "Kicks: " + ChatColor.GREEN + "No Data :D" );

            if( pMutes.size() > 0 ) {
                sj.add( "" );
                sj.add( "  " + ChatColor.GOLD + "" + ChatColor.BOLD + "Mutes:" );
                for( Mute mute : mutes ) {
                    sj.add( ChatColor.RED + "\t" + time.format( mute.time ) + ":" );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Issuer: " + ChatColor.GOLD + mute.issuerUUID.toString() );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Reason: " + ChatColor.GOLD + mute.reason );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Server: " + ChatColor.GOLD + pl.getProxy().getServerInfo( mute.server ).getName() );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Duration: " + ChatColor.GOLD + (mute.duration.toMillis() / 1000) );
                    sj.add( ChatColor.GRAY + "\t >" + ChatColor.RED + "Still Muted?: " + ChatColor.GOLD + (TimeUtils.isTimedOut(mute.time.toEpochMilli(), mute.duration.toMillis())) );
                    sj.add( "" );
                    
                }
                 
            } else sj.add( "\t" + ChatColor.GOLD + "" + ChatColor.BOLD + "Mutes: " + ChatColor.GREEN + "No Data :D" );

            sj.add( "" );
            sj.add( ChatColor.BLACK + "" + ChatColor.BOLD + "##################################" );
            bukkit.sendData( to, "SendMessage", sj.toString() );
            
        } );
    }
    */
    
    public void kick( CommandSender kicker, Player target, String reason ) {
        synchronized( lock ) {
            UUID issuerUUID = kicker instanceof ConsoleCommandSender ? null : ((ProxiedPlayer) kicker).getUniqueId();
            String server = target.getProxy().getServer().getInfo().getName();
            Kick kick = new Kick( target.getId(), issuerUUID, target.getUsername(), kicker.getName(), reason, ZonedDateTime.now(), server );
            kicks.add( kick );
            
            target.getProxy().disconnect( new TextComponent( reason ) );
            String out = kickFormat
                    .replace( "[server]", kick.server )
                    .replace( "[executor]", kick.issuerName )
                    .replace( "[target]", kick.targetName )
                    .replace( "[reason]", kick.reason );
            
            pl.getProxy().getConsole().sendMessage( new TextComponent( out ) );
            for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                    bukkit.sendData( player, "SendMessage", out );
                    
                else continue;
            
            }
        
            String id = target.getId().toString() + ".kicks." + kick.time.toEpochSecond() + ".";
            refractions.set( id + "server", kick.server );
            refractions.set( id + "issuer-uuid", issuerUUID == null ? "" : issuerUUID.toString() );
            refractions.set( id + "issuer-name", kicker.getName() );
            refractions.set( id + "target-name", target.getUsername() );
            refractions.set( id + "reason", kick.reason );
            try {
                pl.getConfigProvider().save( refractions, refractionsFile );
        
            } catch ( IOException e ) { e.printStackTrace(); }

        }
    }
    
    public void mute( CommandSender muter, Player target, String reason, long duration ) {
        synchronized( lock ) {
            UUID issuerUUID = muter instanceof ConsoleCommandSender ? null : ((ProxiedPlayer) muter).getUniqueId();
            String server = target.getProxy() == null ? "OFFLINE" : target.getProxy().getServer().getInfo().getName();
            Mute mute = new Mute( target.getId(), issuerUUID, target.getUsername(), muter.getName(), reason, ZonedDateTime.now(), Duration.ofSeconds( duration ), server, true );
            System.out.println( mute.server );
            mutes.add( mute );
        
            String out = muteFormat
                    .replace( "[server]", mute.server )
                    .replace( "[executor]", mute.issuerName )
                    .replace( "[target]", mute.targetName )
                    .replace( "[reason]", mute.reason )
                    .replace( "[duration]", Long.toString( mute.duration.getSeconds() ) );
            
            pl.getProxy().getConsole().sendMessage( new TextComponent( out ) );
            for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                    bukkit.sendData( player, "SendMessage", out );
                    
                else continue;
                
            }
        
            String id = target.getId().toString() + ".mutes." + mute.time.toEpochSecond() + ".";
            refractions.set( id + "server", mute.server );
            refractions.set( id + "issuer-uuid", issuerUUID == null ? "" : issuerUUID.toString() );
            refractions.set( id + "issuer-name", muter.getName() );
            refractions.set( id + "target-name", target.getUsername() );
            refractions.set( id + "reason", mute.reason );
            refractions.set( id + "duration", mute.duration.getSeconds() );
            refractions.set( id + "is-active", mute.isActive );
            try {
                pl.getConfigProvider().save( refractions, refractionsFile );
        
            } catch ( IOException e ) { e.printStackTrace(); }
    
        }
    }
    
    public void ban( CommandSender banner, Player target, Hack reason ) {
        synchronized( lock ) {
            UUID issuerUUID = banner instanceof ConsoleCommandSender ? null : ((ProxiedPlayer) banner).getUniqueId();
            int durationMultiplier = (int) Math.pow( 2, getBansFor( target ).size() );
            String server = target.getProxy() == null ? "OFFLINE" : target.getProxy().getServer().getInfo().getName();
            Ban ban = new Ban( target.getId(), issuerUUID, target.getUsername(), banner.getName(), reason, durationMultiplier, ZonedDateTime.now(), server, true );
            bans.add( ban );
        
            if( target.getProxy() != null ) {
                pl.getProxy().getScheduler().schedule( pl, () -> {
                    try {
                        bukkit.sendData( target.getProxy(), "StrikeLightning" );
                        Thread.sleep( 5000L  );
                        target.getProxy().disconnect( new TextComponent( reason.kickMessage ) );
                
                    } catch( InterruptedException ignored ) {}
                
                }, 100L, TimeUnit.MILLISECONDS );
            
            }
            
            String out = banFormat
                    .replace( "[server]", ban.server )
                    .replace( "[executor]", ban.issuerName )
                    .replace( "[target]", ban.targetName )
                    .replace( "[reason]", ban.reason.name )
                    .replace( "[duration]", Long.toString( ban.durationMultiplier * ban.reason.duration.getSeconds() ) )
                    .replace( "[formatted-time]", reason.formattedTime )
                    .replace( "[duration-multiplier]", Long.toString( ban.durationMultiplier ) );
            
            pl.getProxy().getConsole().sendMessage( new TextComponent( out ) );
            for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                    bukkit.sendData( player, "SendMessage", out );
                    
                else continue;
            
            }
        
            String id = target.getId().toString() + ".bans." + ban.time.toEpochSecond() + ".";
            refractions.set( id + "server", ban.server );
            refractions.set( id + "issuer-uuid", issuerUUID == null ? "" : issuerUUID.toString() );
            refractions.set( id + "issuer-name", banner.getName() );
            refractions.set( id + "target-name", target.getUsername() );
            refractions.set( id + "reason", ban.reason.name );
            refractions.set( id + "duration-multiplier", ban.durationMultiplier );
            refractions.set( id + "is-active", ban.isActive );
            
            try {
                pl.getConfigProvider().save( refractions, refractionsFile );
        
            } catch ( IOException e ) { e.printStackTrace(); }
        
        }
    }
    
    public void pardonMute( CommandSender pardoner, Mute mute ) {
        synchronized( lock ) {
            mute.isActive = false;
            String out = unmuteFormat
                    .replace( "[server]", mute.server )
                    .replace( "[pardoner]", pardoner.getName() )
                    .replace( "[target]", mute.targetName )
                    .replace( "[reason]", mute.reason )
                    .replace( "[duration]", Long.toString( mute.duration.getSeconds() ) );
            
            pl.getProxy().getConsole().sendMessage( new TextComponent( out ) );
            for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                    bukkit.sendData( player, "SendMessage", out );
                    
                else continue;
                
            }
            
            String id = mute.targetUUID.toString() + ".mutes." + mute.time.toEpochSecond() + ".";
            refractions.set( id + "is-active", mute.isActive );
            try {
                pl.getConfigProvider().save( refractions, refractionsFile );
        
            } catch ( IOException e ) { e.printStackTrace(); }
            
        }
    }
    
    public void pardonBan( CommandSender pardoner, Ban ban ) {
        synchronized( lock ) {
            ban.isActive = false;
            String out = unbanFormat
                    .replace( "[server]", ban.server )
                    .replace( "[pardoner]", pardoner.getName() )
                    .replace( "[target]", ban.targetName )
                    .replace( "[reason]", ban.reason.name )
                    .replace( "[duration]", Long.toString( ban.durationMultiplier * ban.reason.duration.getSeconds() ) )
                    .replace( "[formatted-time]", ban.reason.formattedTime )
                    .replace( "[duration-multiplier]", Long.toString( ban.durationMultiplier ) );
            
            pl.getProxy().getConsole().sendMessage( new TextComponent( out ) );
            for( ProxiedPlayer player : pl.getProxy().getPlayers() ) {
                if( player.hasPermission( "mainframe.*" ) || player.hasPermission( "mainframe.sc" ) )
                    bukkit.sendData( player, "SendMessage", out );
                    
                else continue;
            
            }
            
            String id = ban.targetUUID.toString() + ".bans." + ban.time.toEpochSecond() + ".";
            refractions.set( id + "is-active", ban.isActive );
            try {
                pl.getConfigProvider().save( refractions, refractionsFile );
        
            } catch ( IOException e ) { e.printStackTrace(); }
            
        }
    }
    
    public ZonedDateTime epochToTime( long input ) {
        return ZonedDateTime.ofInstant( Instant.ofEpochSecond( input, 0L ), ZoneId.systemDefault() );
        
    }
    
    
    public Ban getActiveBan( UUID id ) {
        synchronized( lock ) {
            for( Ban ban : bans ) {
                if( ban.targetUUID.equals( id ) && ban.isActive )                     
                    return ban;
                    
                else continue;
                
            }
        
            return null;
        
        }
    }
    
    public Mute getActiveMute( UUID id ) {
        synchronized( lock ) {
            for( Mute mute : mutes ) {
                if( mute.targetUUID.equals( id ) && mute.isActive )
                    return mute;
            
                else continue;
                
            }
        
            return null;
        
        }
    }
    
    public List<Ban> getBansFor( ProxiedPlayer player ) {
        List<Ban> pBans = new ArrayList<>();
        for( Ban ban : bans ) {
            if( ban.targetUUID.equals( player.getUniqueId() ) )
                pBans.add( ban );
        
            else continue;
        }
        
        return pBans;
        
    }
    
    public List<Ban> getBansFor( Player player ) {
        List<Ban> pBans = new ArrayList<>();
        for( Ban ban : bans ) {
            if( ban.targetUUID.equals( player.getId() ) )
                pBans.add( ban );
        
            else continue;
        }
        
        return pBans;
        
    }
    
    public List<Kick> getKicksFor( ProxiedPlayer player ) {
        List<Kick> pKicks = new ArrayList<>();
        for( Kick kick : kicks ) {
            if( kick.targetUUID.equals( player.getUniqueId() ) )
                pKicks.add( kick );
        
            else continue;
        }
        
        return pKicks;
        
    }
    
    public List<Kick> getKicksFor( Player player ) {
        List<Kick> pKicks = new ArrayList<>();
        for( Kick kick : kicks ) {
            if( kick.targetUUID.equals( player.getId() ) )
                pKicks.add( kick );
        
            else continue;
        }
        
        return pKicks;
        
    }
    
    public List<Mute> getMutesFor( ProxiedPlayer player ) {
        List<Mute> pMutes = new ArrayList<>();
        for( Mute mute : mutes ) {
            if( mute.targetUUID.equals( player.getUniqueId() ) )
                pMutes.add( mute );
        
            else continue;
        }
        
        return pMutes;
    }
    
    public List<Mute> getMutesFor( Player player ) {
        List<Mute> pMutes = new ArrayList<>();
        for( Mute mute : mutes ) {
            if( mute.targetUUID.equals( player.getId() ) )
                pMutes.add( mute );
        
            else continue;
        }
        
        return pMutes;
    }
    
    public List<Ban> getBans() {
        synchronized( lock ) {
            return bans;
        
        }
    }
    
    public List<Kick> getKicks() {
        synchronized( lock ) {
            return kicks;
        
        }
    }
    
    public List<Mute> getMutes() {
        synchronized( lock ) {
            return mutes;
        
        }
    }
    
}
