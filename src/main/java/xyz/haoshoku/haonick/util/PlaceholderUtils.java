package xyz.haoshoku.haonick.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;

public class PlaceholderUtils {

    public static String applyPlaceholder( Player player, String text ) {
        if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null && HaoNick.getPlugin().getConfigManager().getSettingsConfig().getConfig().getBoolean( "settings.placeholder_support" ) )
            me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( player, text );
        return text;
    }

}
