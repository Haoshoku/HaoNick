package xyz.haoshoku.haonick.util;

import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;

public class NickUtils {

    public static void setNickedValue( Player player, String key, String value ) {
        DBConnection connection = HaoNick.getPlugin().getDBConnection();
        HaoConfig playersConfig = HaoNick.getPlugin().getConfigManager().getPlayersConfig();
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        if ( settingsConfig.getBoolean( "settings.keep_nick.mysql.active" ) ) {
            connection.setDataAsync( player.getUniqueId(), key, value );
        } else {
            playersConfig.set( player.getUniqueId() + "." + key, value );
            playersConfig.saveConfig();
        }
    }

}
