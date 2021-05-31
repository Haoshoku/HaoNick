package xyz.haoshoku.haonick.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.UUID;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin( AsyncPlayerPreLoginEvent event ) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        if ( uuid != null && name != null ) {
            HaoUser user = HaoUserManager.getUserByUUID( uuid );
            DBConnection connection = HaoNick.getPlugin().getDBConnection();
            if ( connection == null ) {
                HaoConfig playersConfig = HaoNick.getPlugin().getConfigManager().getPlayersConfig();

                if ( playersConfig.getConfig().getString( uuid + ".nicked_uuid" ) == null ) {
                    playersConfig.set( uuid + ".lastName", "-" );
                    playersConfig.set( uuid + ".nicked_uuid", "-" );
                    playersConfig.set( uuid + ".nicked_tag", "-" );
                    playersConfig.set( uuid + ".nicked_skin_value", "-" );
                    playersConfig.set( uuid + ".nicked_skin_signature", "-" );
                    playersConfig.set( uuid + ".nicked_game_profile_name", "-" );
                    playersConfig.set( uuid + ".fake_rank", "normal" );
                }

                playersConfig.set( uuid + ".lastName", name );
                if ( !NickAPI.getConfig().isGameProfileChanges() )
                    playersConfig.set( uuid + ".nicked_game_profile_name", "-" );
                playersConfig.saveConfig();

                if ( !playersConfig.getConfig().getString( uuid + ".nicked_uuid" ).equals( "-" ) )
                    try {
                        user.setNickedUUID( UUID.fromString( playersConfig.getString( uuid + ".nicked_uuid" ) ) );
                    } catch ( Exception ignore ) {}

                if ( !playersConfig.getConfig().getString( uuid + ".nicked_tag" ).equals( "-" ) ) user.setNickedTag( playersConfig.getConfig().getString( uuid + ".nicked_tag" ) );
                if ( !playersConfig.getConfig().getString( uuid + ".nicked_skin_value" ).equals( "-" ) ) user.setNickedSkinValue( playersConfig.getConfig().getString( uuid + ".nicked_skin_value" ) );
                if ( !playersConfig.getConfig().getString( uuid + ".nicked_skin_signature" ).equals( "-" ) ) user.setNickedSkinSignature( playersConfig.getConfig().getString( uuid + ".nicked_skin_signature" ) );
                if ( !playersConfig.getConfig().getString( uuid + ".nicked_game_profile_name" ).equals( "-" ) ) user.setNickedGameProfile( playersConfig.getConfig().getString( uuid + ".nicked_game_profile_name" ) );
                if ( !playersConfig.getConfig().getString( uuid + ".fake_rank" ).equals( "-" ) ) user.setFakeRank( playersConfig.getConfig().getString( uuid + ".fake_rank" ) );

                return;
            }

            connection.setDataSync( uuid, "last_name", name );
            if ( !connection.getDataSync( uuid, "nicked_uuid" ).equals( "-" ) )
                user.setNickedUUID( UUID.fromString( connection.getDataSync( uuid, "nicked_uuid" ) ) );
            if ( !connection.getDataSync( uuid, "nicked_tag" ).equals( "-" ) ) {
                user.setNickedTag( connection.getDataSync( uuid, "nicked_tag" ) );
            }
            if ( !connection.getDataSync( uuid, "nicked_skin_value" ).equals( "-" ) )
                user.setNickedSkinValue( connection.getDataSync( uuid, "nicked_skin_value" ) );
            if ( !connection.getDataSync( uuid, "nicked_skin_signature" ).equals( "-" ) )
                user.setNickedSkinSignature( connection.getDataSync( uuid, "nicked_skin_signature" ) );
            if ( !connection.getDataSync( uuid, "nicked_game_profile_name" ).equals( "-" ) )
                user.setNickedGameProfile( connection.getDataSync( uuid, "nicked_game_profile_name" ) );
            if ( !connection.getDataSync( uuid, "fake_rank" ).equals( "-" ) )
                user.setFakeRank( connection.getDataSync( uuid, "fake_rank" ) );

        }

    }
}
