package xyz.haoshoku.haonick.handler;

import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.user.HaoUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HaoUserHandler {

    private static final Map<UUID, HaoUser> UUID_HAO_USER_MAP = new HashMap<>();

    public static HaoUser getUserByUUID( UUID uuid ) {
        if ( !HaoUserHandler.UUID_HAO_USER_MAP.containsKey( uuid ) )
            HaoUserHandler.UUID_HAO_USER_MAP.put( uuid, new HaoUser( uuid ) );
        return HaoUserHandler.UUID_HAO_USER_MAP.get( uuid );
    }

    public static HaoUser getUser( Player player ) {
        return HaoUserHandler.getUserByUUID( player.getUniqueId() );
    }

    public static void deleteUser( Player player ) {
        HaoUserHandler.UUID_HAO_USER_MAP.remove( player.getUniqueId() );
    }

    public static void deleteAllUser() {
        HaoUserHandler.UUID_HAO_USER_MAP.clear();
    }

}
