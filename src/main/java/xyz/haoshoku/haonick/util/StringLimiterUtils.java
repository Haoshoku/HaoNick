package xyz.haoshoku.haonick.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.config.HaoConfig;

public class StringLimiterUtils {

    private static boolean LIMIT;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[3];

        switch ( version.toLowerCase() ) {
            case "v1_8_R3": case "v1_9_R1": case "v1_10_R1": case "v1_11_R1": case "v1_12_R1":
                StringLimiterUtils.LIMIT = true;
                break;

            default:
                StringLimiterUtils.LIMIT = false;
                break;
        }
    }

    public static String getTabEntry( Player player, HaoConfig haoConfig, String path ) {
        String name = haoConfig.getString( path );
        if ( player != null && Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null )
            name = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( player, name );

        if ( StringLimiterUtils.LIMIT && name.length() >= 32 )
            name = name.substring( 0, 32 );
        else if ( name.length() >= 64 )
            name = name.substring( 0, 64 );
        return name;
    }

}
