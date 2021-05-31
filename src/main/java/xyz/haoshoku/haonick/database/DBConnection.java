package xyz.haoshoku.haonick.database;

import org.bukkit.Bukkit;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.nick.NickPlugin;

import java.sql.*;
import java.util.UUID;

public class DBConnection {

    private final String host, database, username, password, tableName;
    private final int port;

    private Connection connection;

    public DBConnection( String host, int port, String database, String username, String password, String tableName ) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection( 
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", 
                    this.username, this.password );
            this.createTable();
            this.preventShutdownForTimeoutUser();
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage( "§7[§5HaoNick§7] §cMySQL data are not correct! Please check it in settings.yml again!" );
            HaoNick.getPlugin().setDBConnection( null );
        }
    }

    public void disconnect() {
        if ( this.connection != null ) {
            try {
                this.connection.close();
                this.connection = null;
            } catch ( SQLException throwables ) {
                throwables.printStackTrace( );
            }
        }
    }

    private void createTable() {
        try ( PreparedStatement preparedStatement = this.connection.prepareStatement
                ( "CREATE TABLE IF NOT EXISTS `" + this.tableName + "` " +
                        "( player_uuid VARCHAR(36), last_name VARCHAR(16), nicked_uuid VARCHAR(36), " +
                        "nicked_tag VARCHAR(16), nicked_skin_value TEXT, nicked_skin_signature TEXT, nicked_game_profile_name VARCHAR(16), fake_rank TEXT )" ) ) {
            preparedStatement.executeUpdate();
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
        }
    }

    private void createPlayer( UUID uuid ) {
        if ( this.playerExists( uuid ) ) return;
        try ( PreparedStatement preparedStatement = this.connection.prepareStatement
                ( "INSERT INTO `" + this.tableName + "` " +
                        "( player_uuid, last_name, nicked_uuid, nicked_tag, nicked_skin_value, nicked_skin_signature, nicked_game_profile_name, fake_rank ) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )" ) ) {

            preparedStatement.setString( 1, uuid.toString() );
            preparedStatement.setString( 2, "-" );
            preparedStatement.setString( 3, "-" );
            preparedStatement.setString( 4, "-" );
            preparedStatement.setString( 5, "-" );
            preparedStatement.setString( 6, "-" );
            preparedStatement.setString( 7, "-" );
            preparedStatement.setString( 8, "-" );
            preparedStatement.executeUpdate();
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
        }
    }

    public boolean playerExists( UUID uuid ) {
        boolean value = false;
        try ( PreparedStatement preparedStatement = this.connection.prepareStatement
                ( "SELECT * FROM `" + this.tableName + "` WHERE `player_uuid` = ?" ) ) {
            preparedStatement.setString( 1, uuid.toString() );

            try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                while ( resultSet.next() )
                    value = true;
            }
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
        }
        return value;
    }

    public String getDataSync( UUID uuid, String key ) {
        if ( !this.playerExists( uuid ) ) {
            this.createPlayer( uuid );
            return this.getDataSync( uuid, key );
        }

        String value = null;
        try ( PreparedStatement preparedStatement = this.connection.prepareStatement
                ( "SELECT `" + key + "` FROM `" + this.tableName + "` WHERE `player_uuid` = ?" ) ) {
            preparedStatement.setString( 1, uuid.toString() );

            try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                while ( resultSet.next() )
                    value = resultSet.getString( key );
            }
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
        }
        return value;
    }

    public void setDataAsync( UUID uuid, String key, String value ) {
        Bukkit.getScheduler().runTaskAsynchronously( NickPlugin.getPlugin(), () -> this.setDataSync( uuid, key, value ) );
    }

    public void setDataSync( UUID uuid, String key, String value ) {
        if ( !this.playerExists( uuid ) ) {
            this.createPlayer( uuid );
            this.setDataAsync( uuid, key, value );
            return;
        }

        try ( PreparedStatement preparedStatement = this.connection.prepareStatement
                ( "UPDATE `" + this.tableName + "` SET `" + key + "` = ? WHERE `player_uuid` = ?" ) ) {
            preparedStatement.setString( 1, value );
            preparedStatement.setString( 2, String.valueOf( uuid ) );
            preparedStatement.executeUpdate();
        } catch ( SQLException throwables ) {
            throwables.printStackTrace();
        }
    }

    private void preventShutdownForTimeoutUser() {
        Bukkit.getScheduler().runTaskTimerAsynchronously( HaoNick.getPlugin(), () -> this.createTable(), 20L*60L*8L, 20L*60L*8L );
    }

}
