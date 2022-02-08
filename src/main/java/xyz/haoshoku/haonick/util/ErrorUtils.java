package xyz.haoshoku.haonick.util;

import org.bukkit.Bukkit;
import xyz.haoshoku.haonick.HaoNick;

public class ErrorUtils {

    public static void err( String message ) {
        if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.error_log" ) ) return;
        Bukkit.broadcastMessage( "§7[]-------------------------[]" );
        Bukkit.broadcastMessage( "§5HaoNick §cthrew an error:" );
        Bukkit.broadcastMessage( "§cError-Log:" );
        Bukkit.broadcastMessage( "§c" + message );
        Bukkit.broadcastMessage( "§cYou can get support by joining §ehttps://haoshoku.xyz/go/discord" );
        Bukkit.broadcastMessage( "§7[]-------------------------[]" );
    }

}
