package xyz.haoshoku.haonick.scoreboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PlayerScoreboard {

    private final List<Object> teams;
    private Object scoreboard;

    public PlayerScoreboard() {
        this.teams = new ArrayList<>();

        try {
            Class<?> scoreboardClass;
            if ( !ScoreboardHandling.VERSION.equals( "v1_17_R1" ) && !ScoreboardHandling.VERSION.equals( "v1_18_R1" ) && !ScoreboardHandling.VERSION.equals( "v1_18_R2" ) && !ScoreboardHandling.VERSION.equals( "v1_19_R1" ))
                scoreboardClass = Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".Scoreboard" );
            else
                scoreboardClass = Class.forName( "net.minecraft.world.scores.Scoreboard" );
            this.scoreboard = scoreboardClass.newInstance();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public synchronized void addTeam( String team ) {
        team = team.toLowerCase();

        if ( !this.teamExists( team ) ) {
            try {
                Class<?> scoreboardTeamClass;
                if ( !ScoreboardHandling.VERSION.equals( "v1_17_R1" ) && !ScoreboardHandling.VERSION.equals( "v1_18_R1" ) && !ScoreboardHandling.VERSION.equals( "v1_18_R2" ) && !ScoreboardHandling.VERSION.equals( "v1_19_R1" ) )
                    scoreboardTeamClass = Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".ScoreboardTeam" );
                else
                    scoreboardTeamClass = Class.forName( "net.minecraft.world.scores.ScoreboardTeam" );

                Constructor<?> scoreboardTeamConstructor = scoreboardTeamClass.getConstructor( this.scoreboard.getClass(), team.getClass() );
                Object scoreboardTeamInstance = scoreboardTeamConstructor.newInstance( this.scoreboard, team );
                this.teams.add( scoreboardTeamInstance );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setPrefix( String team, String prefix ) {
        team = team.toLowerCase();

        try {
            Object scoreboardTeam = this.getScoreboardTeam( team );

            switch ( ScoreboardHandling.VERSION ) {
                case "v1_8_R3": case "v1_9_R1": case "v1_10_R1": case "v1_11_R1": case "v1_12_R1":
                    scoreboardTeam.getClass().getMethod( "setPrefix", String.class ).invoke( scoreboardTeam, prefix );
                    break;

                case "v1_17_R1": {
                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.network.chat.IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "setPrefix", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( prefix ) );
                    break;
                }

                case "v1_18_R1":
                case "v1_19_R1":
                case "v1_18_R2": {

                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.network.chat.IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "b", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( prefix ) );
                    break;
                }

                default: {
                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "setPrefix", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( prefix ) );
                    break;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public synchronized void setSuffix( String team, String suffix ) {
        team = team.toLowerCase();

        try {
            Object scoreboardTeam = this.getScoreboardTeam( team );
            switch ( ScoreboardHandling.VERSION ) {
                case "v1_8_R3": case "v1_9_R1": case "v1_10_R1": case "v1_11_R1": case "v1_12_R1":
                    scoreboardTeam.getClass().getMethod( "setSuffix", String.class ).invoke( scoreboardTeam, suffix );
                    break;

                case "v1_17_R1": {
                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.network.chat.IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "setSuffix", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( suffix ) );
                    break;
                }

                case "v1_18_R1":
                case "v1_19_R1":
                case "v1_18_R2": {
                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.network.chat.IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "c", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( suffix ) );
                    break;
                }

                default: {
                    Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.server." + ScoreboardHandling.VERSION + ".IChatBaseComponent" );
                    scoreboardTeam.getClass().getMethod( "setSuffix", iChatBaseComponentClass ).invoke( scoreboardTeam, PlayerScoreboard.getMessage( suffix ) );
                    break;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public synchronized boolean teamExists( String team ) {
        for ( Object scoreboardTeam : this.teams ) {
            try {
                String name;
                if ( ScoreboardHandling.VERSION.equals( "v1_18_R1" ) || ScoreboardHandling.VERSION.equals( "v1_18_R2" ) || ScoreboardHandling.VERSION.equals( "v1_19_R1" ))
                    name = (String) scoreboardTeam.getClass().getMethod( "b" ).invoke( scoreboardTeam );
                else
                    name = (String) scoreboardTeam.getClass().getMethod( "getName" ).invoke( scoreboardTeam );
                if ( team.equalsIgnoreCase( name ) )
                    return true;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized Object getScoreboardTeam( String team ) {
        team = team.toLowerCase();

        for ( Object scoreboardTeam : this.teams ) {
            try {
                String loopName;
                if ( ScoreboardHandling.VERSION.equals( "v1_18_R1" ) || ScoreboardHandling.VERSION.equals( "v1_18_R2" ) || ScoreboardHandling.VERSION.equals( "v1_19_R1" ) )
                    loopName = (String) scoreboardTeam.getClass().getMethod( "b" ).invoke( scoreboardTeam );
                else
                    loopName = (String) scoreboardTeam.getClass().getMethod( "getName" ).invoke( scoreboardTeam );
                if ( loopName.equals( team ) )
                    return scoreboardTeam;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Object getMessage( String message ) {
        try {
            Class<?> craftChatMessageClass = Class.forName( "org.bukkit.craftbukkit." + ScoreboardHandling.VERSION + ".util.CraftChatMessage" );
            Method method = craftChatMessageClass.getMethod( "fromString", String.class );
            Object[] instance = (Object[]) method.invoke( craftChatMessageClass, message );
            return instance[0];
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
}
