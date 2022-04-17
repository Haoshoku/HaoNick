package xyz.haoshoku.haonick.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.haonick.util.BlacklistedWorldUtils;
import xyz.haoshoku.haonick.util.ErrorUtils;
import xyz.haoshoku.haonick.util.PlaceholderUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ScoreboardHandling {

    public static final Map<UUID, PlayerScoreboard> PLAYER_SCOREBOARD_MAP = new HashMap<>();
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[3];
    private static boolean LIMIT;

    static {
        switch ( VERSION ) {
            case "v1_8_R3": case "v1_9_R1": case "v1_10_R1": case "v1_11_R1": case "v1_12_R1":
                ScoreboardHandling.LIMIT = true;
                break;

            default:
                ScoreboardHandling.LIMIT = false;
                break;
        }
    }

    public static void setScoreboard( Player player ) {
        ScoreboardHandling.PLAYER_SCOREBOARD_MAP.put( player.getUniqueId(), new PlayerScoreboard() );
    }

    public static String getTabEntry( Player player, HaoConfig config, String path ) {
        String name = config.getString( path );
        if ( player != null )
            name = PlaceholderUtils.applyPlaceholder( player, name );

        if ( ScoreboardHandling.LIMIT && name.length() > 16 )
            name = name.substring( 0, 16 );
        else if ( name.length() >= 64 )
            name = name.substring( 0, 64 );
        return name;
    }

    public static void updateNamesFromScoreboardAsync() {
        Bukkit.getScheduler().runTaskAsynchronously( HaoNick.getPlugin(), ScoreboardHandling::updateNamesFromScoreboard );
    }

    public static void updateNamesFromScoreboard() {
        HaoConfig ranksConfig = HaoNick.getPlugin().getConfigManager().getRanksConfig();
        HaoConfig fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if ( !ScoreboardHandling.PLAYER_SCOREBOARD_MAP.containsKey( player.getUniqueId() ) )
                ScoreboardHandling.PLAYER_SCOREBOARD_MAP.put( player.getUniqueId(), new PlayerScoreboard() );

            if ( BlacklistedWorldUtils.isInABlacklistedWorld( player ) ) continue;

            PlayerScoreboard playerScoreboard = ScoreboardHandling.PLAYER_SCOREBOARD_MAP.get( player.getUniqueId() );

            {
                boolean ranksActiveTab = ranksConfig.getBoolean( "ranks_settings.tab" );
                boolean ranksActiveChat = ranksConfig.getBoolean( "ranks_settings.chat" );

                if ( ranksActiveChat || ranksActiveTab ) {
                    Map<String, Object[]> playerWeightMap = new HashMap<>();
                    List<String> defaultPlayersList = new ArrayList<>();
                    String defaultRank = null;
                    String defaultRankWeight = "99";

                    if ( ranksConfig.getSection( "ranks" ) != null ) {
                        for( String rank : ranksConfig.getSection( "ranks" ).getKeys( false ) ) {
                            String weight = ranksConfig.getString( "ranks." + rank + ".tab.weight" );

                            if ( ranksConfig.getBoolean( "ranks." + rank + ".default" ) ) {
                                defaultRank = rank;
                                defaultRankWeight = ranksConfig.getString( "ranks." + rank + ".tab.weight" );
                            }

                            for ( Player online : Bukkit.getOnlinePlayers() ) {
                                if ( online.hasPermission( ranksConfig.getConfig().getString( "ranks." + rank + ".permission" ) ) && !playerWeightMap.containsKey( NickAPI.getOriginalName( online ) ) ) {
                                    playerWeightMap.put( NickAPI.getOriginalName( online ), new Object[] { rank, weight } );
                                    if ( ranksActiveTab ) ScoreboardHandling.setPlayerListName( online, false, rank );
                                }
                            }
                        }

                        for ( Player online : Bukkit.getOnlinePlayers() ) {
                            if ( !playerWeightMap.containsKey( NickAPI.getOriginalName( online ) ) )
                                defaultPlayersList.add( NickAPI.getOriginalName( online ) );
                        }

                        for ( Map.Entry<String, Object[]> entry : playerWeightMap.entrySet() ) {
                            String playerName = entry.getKey();
                            String rank = (String) entry.getValue()[0];
                            String weight = (String) entry.getValue()[1];
                            HaoUserHandler.getUser( NickAPI.getPlayerOfOriginalName( playerName ) ).setRank( rank );
                            if ( !ranksActiveTab ) continue;
                            ScoreboardHandling.generateScoreboardData( player, weight, playerName, playerScoreboard, ranksConfig, rank );
                        }

                        for ( String defaultPlayer : defaultPlayersList ) {
                            if ( defaultRank == null ) {
                                ErrorUtils.err( "§cDefault rank does not exist in ranks.yml. Select a rank in ranks.yml and add default: true as an entry" );
                                break;
                            }
                            HaoUserHandler.getUser( NickAPI.getPlayerOfOriginalName( defaultPlayer ) ).setRank( defaultRank );

                            if ( !ranksActiveTab ) continue;
                            ScoreboardHandling.generateScoreboardData( player, defaultRankWeight, defaultPlayer, playerScoreboard, ranksConfig, defaultRank );
                            ScoreboardHandling.setPlayerListName( NickAPI.getPlayerOfOriginalName( defaultPlayer ), false, defaultRank );
                        }

                    }
                }
            }

            {
                boolean fakeRanksActiveTab = fakeRanksConfig.getBoolean( "fake_ranks_settings.tab" );
                boolean fakeRanksActiveChat = fakeRanksConfig.getBoolean( "fake_ranks_settings.chat" );

                if ( fakeRanksActiveChat || fakeRanksActiveTab ) {
                    Collection<String> nickedNames = NickAPI.getNickedPlayers().values();

                    for ( String name : nickedNames ) {
                        Player online = NickAPI.getPlayerOfNickedName( name );
                        if ( online == null || !online.isOnline() ) continue;
                        if ( BlacklistedWorldUtils.isInABlacklistedWorld( online ) ) continue;
                        HaoUser user = HaoUserHandler.getUser( online );

                        String scoreboardName = null;
                        String defaultRank = null;
                        String defaultRankWeight = "99";

                        for ( String fakeRank : fakeRanksConfig.getSection( "fake_ranks" ).getKeys( false ) ) {
                            if ( fakeRanksConfig.getBoolean( "fake_ranks." + fakeRank + ".default" ) ) {
                                defaultRank = fakeRank;
                                defaultRankWeight = fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".tab.weight" );
                            }
                        }

                        boolean permissionBased = HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.tab.fake_ranks_permission_based" );
                        boolean rankReceived = false;
                        boolean errorSent = false;

                        if ( fakeRanksConfig.getSection( "fake_ranks" ) != null ) {
                            for ( String fakeRank : fakeRanksConfig.getSection( "fake_ranks" ).getKeys( false ) ) {
                                if ( permissionBased ) {
                                    if ( online.hasPermission( fakeRanksConfig.getConfig().getString( "fake_ranks." + fakeRank + ".permission" ) ) && !rankReceived ) {
                                        user.setFakeRank( fakeRank );
                                        rankReceived = true;
                                    }
                                }
                                if ( user.getFakeRank() == null )
                                    continue;

                                if ( user.getFakeRank() != null && !user.getFakeRank().equalsIgnoreCase( fakeRank ) && fakeRanksConfig.getConfig().getString( "fake_ranks." + user.getFakeRank() ) != null )
                                    continue;

                                user.setFakeRankLoop( true );
                                scoreboardName = fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".tab.weight" ) + name.hashCode();
                                if ( scoreboardName.length() > 16 )
                                    scoreboardName = scoreboardName.substring( 0, 16 );

                                String prefix = ScoreboardHandling.getTabEntry( online, fakeRanksConfig, "fake_ranks." + fakeRank + ".tab.prefix" );
                                String suffix = ScoreboardHandling.getTabEntry( online, fakeRanksConfig, "fake_ranks." + fakeRank + ".tab.suffix" );
                                online.setDisplayName( prefix + NickAPI.getName( online ) + suffix );
                                ScoreboardHandling.applyScoreboardData( player, scoreboardName, fakeRank, prefix, suffix, fakeRanksConfig );
                                user.setFakeRank( fakeRank );
                            }

                            if ( !user.isFakeRankLoop() && defaultRank != null ) {
                                scoreboardName = defaultRankWeight + name.hashCode();

                                if ( scoreboardName.length() > 16 )
                                    scoreboardName = scoreboardName.substring( 0, 16 );

                                String prefix = ScoreboardHandling.getTabEntry( online, fakeRanksConfig, "fake_ranks." + defaultRank + ".tab.prefix" );
                                String suffix = ScoreboardHandling.getTabEntry( online, fakeRanksConfig, "fake_ranks." + defaultRank + ".tab.suffix" );
                                online.setDisplayName( prefix + NickAPI.getName( online ) + suffix );
                                ScoreboardHandling.applyScoreboardData( player, scoreboardName, defaultRank, prefix, suffix, fakeRanksConfig );
                                user.setFakeRank( defaultRank );

                            } else if ( defaultRank == null ) {
                                errorSent = true;
                                ErrorUtils.err( "§cDefault rank in §efake_ranks.yml §cdoes not exist! Set it by selecting a fake rank and add §edefault: true §cinto its data." );
                            }

                            if ( user.getFakeRank() != null && !user.getFakeRank().equals( "" ) && scoreboardName != null ) {
                                Object scoreboardTeam = playerScoreboard.getScoreboardTeam( scoreboardName );

                                try {
                                    Collection<String> collection;

                                    if ( ScoreboardHandling.VERSION.equalsIgnoreCase( "v1_18_R1" ) )
                                        collection = (Collection<String>) scoreboardTeam.getClass().getMethod( "g" ).invoke( scoreboardTeam );
                                    else
                                        collection = (Collection<String>) scoreboardTeam.getClass().getMethod( "getPlayerNameSet" ).invoke( scoreboardTeam );

                                    collection.add( NickAPI.getName( online ) );
                                } catch ( Exception e ) {
                                    e.printStackTrace();
                                }


                                if ( !fakeRanksActiveTab ) continue;

                                Object packet1 = ScoreboardHandling.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 1 );
                                Object packet2 = ScoreboardHandling.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 0 );

                                ScoreboardHandling.sendPacket( player, packet1 );
                                ScoreboardHandling.sendPacket( player, packet2 );
                                ScoreboardHandling.setPlayerListName( user.getPlayer(), true, user.getFakeRank() );
                            } else {
                                if ( !errorSent )
                                    ErrorUtils.err( "Error code 1337 - This error shouldn't happen. Please report it!" );
                            }
                        }

                    }
                }


            }


        }
    }

    /*


                    String prefix = ScoreboardReflection.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.prefix" );
                    String suffix = ScoreboardReflection.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.suffix" );

                    playerScoreboard.addTeam( scoreboardName );
                    playerScoreboard.setPrefix( scoreboardName, ScoreboardReflection.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.prefix" ) );
                    playerScoreboard.setSuffix( scoreboardName, ScoreboardReflection.getTabEntry( player, ranksConfig, "ranks." + rank + ".tab.suffix" ) );
                    try {
                        try {
                            ChatColor color = ChatColor.valueOf( ranksConfig.getString( "ranks." + rank + ".tab.color" ) );
                            ScoreboardReflection.setColor( playerScoreboard.getScoreboardTeam( scoreboardName ), color );
                        } catch ( IllegalArgumentException exception ) {
                            ErrorUtils.err( "§cColor from rank " + rank + " is not valid \n§cGet the list here: \n§ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html" );
                        }
                    } catch ( NoSuchMethodError ignore ) {}

                    try {
                        Object scoreboardTeam = playerScoreboard.getScoreboardTeam( scoreboardName );
                        Collection<String> collection = (Collection<String>) scoreboardTeam.getClass().getMethod( "getPlayerNameSet" ).invoke( scoreboardTeam );

                        for ( Player online : Bukkit.getOnlinePlayers() ) {
                            if ( online.hasPermission( ranksConfig.getConfig().getString( "ranks." + rank + ".permission" ) ) || ranksConfig.getBoolean( "ranks." + rank + ".default" ) ) {
                                if ( !addedTeam.contains( online.getUniqueId() ) ) {
                                    collection.add( NickAPI.getOriginalName( online ) );
                                    addedTeam.add( online.getUniqueId() );
                                    HaoUserHandler.getUser( online ).setRank( rank );
                                    //online.setDisplayName( prefix + NickAPI.getName( online ) + suffix );
                                }
                            }
                        }

                    //    ReflectionUtils.setField( scoreboardTeam, "l", Class.forName( "net.minecraft.server." + VERSION + ".ScoreboardTeamBase$EnumTeamPush" ).getEnumConstants()[1] );

                        Object packet1 = ScoreboardReflection.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 1 );
                        Object packet2 = ScoreboardReflection.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 0 );

                        ScoreboardReflection.sendPacket( player, packet1 );
                        ScoreboardReflection.sendPacket( player, packet2 );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                }


     */

    private static void generateScoreboardData( Player player, String weight, String playerName, PlayerScoreboard playerScoreboard, HaoConfig ranksConfig, String rank ) {
        String scoreboardName = weight + playerName.hashCode();

        if ( scoreboardName.length() > 16 )
            scoreboardName = scoreboardName.substring( 0, 16 );



        playerScoreboard.addTeam( scoreboardName );
        playerScoreboard.setPrefix( scoreboardName, ScoreboardHandling.getTabEntry( NickAPI.getPlayerOfOriginalName( playerName ), ranksConfig, "ranks." + rank + ".tab.prefix" ) );
        playerScoreboard.setSuffix( scoreboardName, ScoreboardHandling.getTabEntry( NickAPI.getPlayerOfOriginalName( playerName ), ranksConfig, "ranks." + rank + ".tab.suffix" ) );

        try {
            try {
                ChatColor color = ChatColor.valueOf( ranksConfig.getString( "ranks." + rank + ".tab.color" ) );
                ScoreboardHandling.setColor( playerScoreboard.getScoreboardTeam( scoreboardName ), color );
            } catch ( IllegalArgumentException exception ) {
                ErrorUtils.err( "§cColor from rank " + rank + " is not valid \n§cGet the list here: \n§ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html" );
            }
        } catch ( NoSuchMethodError ignore ) {}

        try {
            Object scoreboardTeam = playerScoreboard.getScoreboardTeam( scoreboardName );
            Collection<String> collection;

            if ( ScoreboardHandling.VERSION.equalsIgnoreCase( "v1_18_R1" ) )
                collection = (Collection<String>) scoreboardTeam.getClass().getMethod( "g" ).invoke( scoreboardTeam );
            else
                collection = (Collection<String>) scoreboardTeam.getClass().getMethod( "getPlayerNameSet" ).invoke( scoreboardTeam );


            Player playerObject = NickAPI.getPlayerOfOriginalName( playerName );
            if ( playerObject != null && playerObject.isOnline() && !BlacklistedWorldUtils.isInABlacklistedWorld( playerObject ) ) {
                collection.add( playerName );
                Object packet1 = ScoreboardHandling.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 1 );
                Object packet2 = ScoreboardHandling.getPacketPlayOutScoreboardTeamPacket( scoreboardTeam, 0 );

                ScoreboardHandling.sendPacket( player, packet1 );
                ScoreboardHandling.sendPacket( player, packet2 );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static Object getPacketPlayOutScoreboardTeamPacket( Object scoreboardTeam, int id ) {
        if ( !ScoreboardHandling.VERSION.equals( "v1_17_R1" ) && !ScoreboardHandling.VERSION.equals( "v1_18_R1" ) ) {
            try {
                Class<?> clazz = Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".PacketPlayOutScoreboardTeam" );
                return clazz.getConstructor( scoreboardTeam.getClass(), int.class ).newInstance( scoreboardTeam, id );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        } else {
            try {
                Class<?> clazz = Class.forName( "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam" );

                Method scoreboardTeamMethod;
                if ( id == 1 ) {
                    scoreboardTeamMethod = clazz.getMethod( "a", Class.forName( "net.minecraft.world.scores.ScoreboardTeam" ) );
                    return scoreboardTeamMethod.invoke( clazz, scoreboardTeam );
                } else {
                    scoreboardTeamMethod = clazz.getMethod( "a", Class.forName( "net.minecraft.world.scores.ScoreboardTeam" ), boolean.class );
                    return scoreboardTeamMethod.invoke( clazz, scoreboardTeam, true );
                }

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Object getEntityPlayer( Player player ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return player.getClass().getMethod( "getHandle" ).invoke( player );
    }

    private static void sendPacket( Player player, Object packet ) {
        try {
            Object entityPlayerInstance = ScoreboardHandling.getEntityPlayer( player );
            Object playerConnectionInstance;
            Method sendPacketMethod;


            if ( ScoreboardHandling.VERSION.equals( "v1_18_R1" ) ) {
                playerConnectionInstance = entityPlayerInstance.getClass().getField( "b" ).get( entityPlayerInstance );
                sendPacketMethod = playerConnectionInstance.getClass().getMethod( "a", Class.forName( "net.minecraft.network.protocol.Packet" ) );
            } else if ( !ScoreboardHandling.VERSION.equals( "v1_17_R1" ) ) {
                playerConnectionInstance = entityPlayerInstance.getClass().getField( "playerConnection" ).get( entityPlayerInstance );
                sendPacketMethod = playerConnectionInstance.getClass().getMethod( "sendPacket", Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".Packet" ) );
            } else {
                playerConnectionInstance = entityPlayerInstance.getClass().getField( "b" ).get( entityPlayerInstance );
                sendPacketMethod = playerConnectionInstance.getClass().getMethod( "sendPacket", Class.forName( "net.minecraft.network.protocol.Packet" ) );
            }

            sendPacketMethod.invoke( playerConnectionInstance, packet );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public static void updateNamesFromScoreboardDelayed() {
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        if ( settingsConfig.getBoolean( "settings.tab.async" ) )
            Bukkit.getScheduler().runTaskLaterAsynchronously( HaoNick.getPlugin(), ScoreboardHandling::updateNamesFromScoreboard, settingsConfig.getInt( "settings.tab.scoreboard_creation_time" ) );
        else
            Bukkit.getScheduler().runTaskLater( HaoNick.getPlugin(), ScoreboardHandling::updateNamesFromScoreboard, settingsConfig.getInt( "settings.tab.scoreboard_creation_time" ) );

    }

    private static void applyScoreboardData( Player player, String scoreboardName, String fakeRank, String prefix, String suffix, HaoConfig fakeRanksConfig ) {
        if ( scoreboardName.length() >= 16 )
            scoreboardName = scoreboardName.substring( 0, 16 );

        PlayerScoreboard playerScoreboard = ScoreboardHandling.PLAYER_SCOREBOARD_MAP.get( player.getUniqueId() );

        playerScoreboard.addTeam( scoreboardName );
        playerScoreboard.setPrefix( scoreboardName, prefix );
        playerScoreboard.setSuffix( scoreboardName, suffix );
        try {
            try {
                ChatColor color = ChatColor.valueOf( fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".tab.color" ) );
                ScoreboardHandling.setColor( playerScoreboard.getScoreboardTeam( scoreboardName ), color );
            } catch ( IllegalArgumentException ex ) {
                ErrorUtils.err( "§cColor from fakeRank " + fakeRank + " is not valid \n§cGet the list here: \n§ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html" );
            }
        } catch ( NoSuchMethodError ignore ) {}
    }

    private static void setColor( Object scoreboardTeam, ChatColor color ) {
        try {
            Class<?> craftChatMessageClass = Class.forName( "org.bukkit.craftbukkit." + ScoreboardHandling.VERSION + ".util.CraftChatMessage" );
            if ( ScoreboardHandling.VERSION.equals( "v1_18_R1" ) )
                scoreboardTeam.getClass().getMethod( "a", Class.forName( "net.minecraft.EnumChatFormat" ) )
                        .invoke( scoreboardTeam, craftChatMessageClass.getMethod( "getColor", ChatColor.class ).invoke( craftChatMessageClass, color ) );
            else if ( !ScoreboardHandling.VERSION.equals( "v1_17_R1" ) )
                scoreboardTeam.getClass().getMethod( "setColor", Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".EnumChatFormat" ) )
                    .invoke( scoreboardTeam, craftChatMessageClass.getMethod( "getColor", ChatColor.class ).invoke( craftChatMessageClass, color ) );
            else
                scoreboardTeam.getClass().getMethod( "setColor", Class.forName( "net.minecraft.EnumChatFormat" ) )
                        .invoke( scoreboardTeam, craftChatMessageClass.getMethod( "getColor", ChatColor.class ).invoke( craftChatMessageClass, color ) );
        } catch ( NoSuchMethodError | Exception ignore ) { }
    }

    private static void setPlayerListName( Player player, boolean fakeRankConfig, String rankGroup ) {
        String rank;

        if ( fakeRankConfig )
            rank = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig().getMessage( "fake_ranks." + rankGroup + ".tab.player_list_name", player ).replace( "%name%", NickAPI.getName( player ) ).replace( "%player%", NickAPI.getName( player ) );
        else
            rank = HaoNick.getPlugin().getConfigManager().getRanksConfig().getMessage( "ranks." + rankGroup + ".tab.player_list_name", player ).replace( "%name%", NickAPI.getName( player ) ).replace( "%player%", NickAPI.getName( player ) );

        if ( !rank.equalsIgnoreCase( "none" ) && !rank.equalsIgnoreCase( "" ) )
            player.setPlayerListName( rank );
    }

}
