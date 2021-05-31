package xyz.haoshoku.haonick.manager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.user.HaoUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HaoUserManager {

    private static final Map<UUID, HaoUser> UUID_HAO_USER_MAP = new HashMap<>();

    public static HaoUser getUserByUUID( UUID uuid ) {
        if ( !UUID_HAO_USER_MAP.containsKey( uuid ) )
            UUID_HAO_USER_MAP.put( uuid, new HaoUser( uuid ) );
        return UUID_HAO_USER_MAP.get( uuid );
    }

    public static HaoUser getUser( Player player ) {
        return getUserByUUID( player.getUniqueId() );
    }

    public static void deleteUser( Player player ) {
        UUID_HAO_USER_MAP.remove( player.getUniqueId() );
    }

}
