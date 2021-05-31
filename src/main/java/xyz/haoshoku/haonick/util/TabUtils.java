package xyz.haoshoku.haonick.util;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TabUtils {

    public static void updateNamesFromScoreboard() {
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        HaoConfig ranksConfig = HaoNick.getPlugin().getConfigManager().getRanksConfig();
        HaoConfig fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();
        if ( !fakeRanksConfig.getBoolean( "fake_ranks_settings.tab" ) ) return;

        Bukkit.getScheduler().runTaskLater( HaoNick.getPlugin(), () -> {
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                Scoreboard scoreboard;

                if ( player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() ) {
                    if ( settingsConfig.getBoolean( "settings.tab.new_scoreboard" ) )
                        player.setScoreboard( Bukkit.getScoreboardManager().getNewScoreboard() );
                }

                scoreboard = player.getScoreboard();
                Set<UUID> addedTeam = Sets.newHashSet();

                if ( ranksConfig.getBoolean( "ranks_settings.tab" ) ) {
                    for( String rank : ranksConfig.getSection( "ranks" ).getKeys( false ) ) {
                        String scoreboardName = StringLimiterUtils.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.name" );

                        Team team = scoreboard.getTeam( scoreboardName ) == null ? scoreboard.registerNewTeam( scoreboardName ) : scoreboard.getTeam( scoreboardName );
                        team.setPrefix( StringLimiterUtils.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.prefix" ) );
                        team.setSuffix( StringLimiterUtils.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.suffix" ) );
                        try {
                            team.setColor( ChatColor.valueOf( ranksConfig.getString( "ranks." + rank + ".tab.color" ) ) );
                        } catch ( NoSuchMethodError ignore ) {}

                        for ( Player online : Bukkit.getOnlinePlayers() ) {
                            if ( online.hasPermission( ranksConfig.getConfig().getString( "ranks." + rank + ".permission" ) ) || ranksConfig.getBoolean( "ranks." + rank + ".default" ) ) {
                                if ( !addedTeam.contains( online.getUniqueId() ) ) {
                                    team.addEntry( NickAPI.getOriginalName( online ) );
                                    addedTeam.add( online.getUniqueId() );
                                    HaoUserManager.getUser( online ).setRank( rank );
                                }
                            }
                        }
                    }
                }

                Collection<String> nickedNames = NickAPI.getNickedPlayers().values();

                for ( String name : nickedNames ) {
                    Player online = NickAPI.getPlayerOfNickedName( name );
                    if ( online == null ) continue;
                    HaoUser user = HaoUserManager.getUser( online );

                    String defaultRankScoreboard = "", defaultRank = "";
                    String scoreboardName = null;

                    for ( String fakeRank : fakeRanksConfig.getSection( "fake_ranks" ).getKeys( false ) ) {
                        if ( !user.getFakeRank().equals( fakeRank ) ) continue;
                        scoreboardName = StringLimiterUtils.getTabEntry( online, fakeRanksConfig, "fake_ranks." + fakeRank + ".tab.name" ) + new Random().nextInt( 999999999 ) + 1000000000;

                        if ( scoreboardName.length() >= 16 )
                            scoreboardName = scoreboardName.substring( 0, 16 );

                        Team team = scoreboard.getTeam( scoreboardName ) != null ? scoreboard.getTeam( scoreboardName ) : scoreboard.registerNewTeam( scoreboardName );
                        team.setPrefix( StringLimiterUtils.getTabEntry( online, fakeRanksConfig, "fake_ranks." + fakeRank + ".tab.prefix" ) );
                        team.setSuffix( StringLimiterUtils.getTabEntry( online, fakeRanksConfig, "fake_ranks." + fakeRank + ".tab.suffix" ) );
                        try {
                            team.setColor( ChatColor.valueOf( fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".tab.color" ) ) );
                        } catch ( NoSuchMethodError ignore ) {}

                        if ( fakeRanksConfig.getBoolean( "fake_ranks." + fakeRank + ".default" ) ) {
                            defaultRankScoreboard = scoreboardName;
                            defaultRank = fakeRank;
                        }
                    }

                    if ( user.getFakeRank() == null ) {
                        if ( defaultRank == null || defaultRankScoreboard.equals( "" ) || defaultRank.equals( "" ) ) {
                            Bukkit.broadcastMessage( "§5HaoNick §8| §cWarning: Default rank in fake_ranks.yml does not exist! Add it by doing default: true in a rank! [1]" );
                            continue;
                        }
                        scoreboard.getTeam( defaultRankScoreboard ).addEntry( NickAPI.getName( online ) );
                        user.setFakeRank( defaultRank );
                    } else {
                        if ( fakeRanksConfig.getConfig().getString( "fake_ranks." + user.getFakeRank() ) == null )
                            user.setFakeRank( defaultRank );

                        if ( user.getFakeRank() != null && !user.getFakeRank().equals( "" ) && scoreboardName != null ) {
                            scoreboard.getTeam( scoreboardName ).addEntry( NickAPI.getName( online ) );
                        } else
                            Bukkit.broadcastMessage( "§5HaoNick §8| §cWarning: Default rank in fake_ranks.yml does not exist! Add it by doing default: true in a rank! [2]" );
                    }
                }
            }
        }, settingsConfig.getInt( "settings.tab.scoreboard_creation_time" ) );
    }


    public static void sendTabList( Player player, String header, String footer ) {
        /*
        SRC: Discord User: Zey'#1662
         */
        /*
        Change to reflection by Haoshoku
         */

        try {
            player.setPlayerListHeaderFooter( header, footer );
        } catch ( NoSuchMethodError apply ) {
            TabUtils.sendTabListWithReflection( player, header, footer );
        }
    }

    private static void sendTabListWithReflection( Player player, String header, String footer ) {
        if ( header == null ) header = "";
        if ( footer == null ) footer = "";

        header = PlaceholderUtils.applyPlaceholder( player, header );
        footer = PlaceholderUtils.applyPlaceholder( player, footer );

        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[3];
        header = ChatColor.translateAlternateColorCodes( '&', header );
        footer = ChatColor.translateAlternateColorCodes( '&', footer );

        try {
            Class<?> tabHeaderChatBaseComponentA = Class.forName( "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer" );
            Method methodOfTabHeaderChatBaseComponent = tabHeaderChatBaseComponentA.getMethod( "a", String.class );
            Object resultOfTabHeaderChatBaseComponent = methodOfTabHeaderChatBaseComponent.invoke( tabHeaderChatBaseComponentA, "{\"text\": \"" + header + "\"}" );
            Class<?> tabFooterChatBaseComponentA = Class.forName( "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer" );
            Method methodOfTabFooterChatBaseComponent = tabFooterChatBaseComponentA.getMethod( "a", String.class );
            Object resultOfTabFooterChatBaseComponent = methodOfTabFooterChatBaseComponent.invoke( tabFooterChatBaseComponentA, "{\"text\": \"" + footer + "\"}" );
            Class<?> packetPlayOutPlayerListHeaderFooter = Class.forName( "net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter" );
            Object newInstanceOfPlayerListHeaderFooter = packetPlayOutPlayerListHeaderFooter.newInstance();
            TabUtils.setField( newInstanceOfPlayerListHeaderFooter, "a", resultOfTabHeaderChatBaseComponent );
            TabUtils.setField( newInstanceOfPlayerListHeaderFooter, "b", resultOfTabFooterChatBaseComponent );
            Object entityPlayerObject = player.getClass().getMethod( "getHandle" ).invoke( player ); // EntityPlayer
            Class<?> packetClass = Class.forName( "net.minecraft.server." + version + ".Packet" );
            Object playerConnectionField = entityPlayerObject.getClass().getField( "playerConnection" ).get( entityPlayerObject );
            playerConnectionField.getClass().getMethod( "sendPacket", packetClass ).invoke( playerConnectionField, newInstanceOfPlayerListHeaderFooter );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static void setField( Object packet, String fieldAsString, Object value ) {
        try {
            Field field = packet.getClass().getDeclaredField( fieldAsString );
            field.setAccessible( true );
            field.set( packet, value );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
