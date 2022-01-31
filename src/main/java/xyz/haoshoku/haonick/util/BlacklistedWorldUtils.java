package xyz.haoshoku.haonick.util;

import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;

import java.util.List;

public class BlacklistedWorldUtils {

    public static boolean isInABlacklistedWorld( Player player ) {
        List<String> list = HaoNick.getPlugin().getConfigManager().getSettingsConfig().getStringList( "settings.blacklisted_worlds" );

        for ( String worldName : list ) {
            if ( player.getWorld().getName().equalsIgnoreCase( worldName ) )
                return true;
        }
        return false;
    }

}
