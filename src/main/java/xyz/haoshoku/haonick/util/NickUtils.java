package xyz.haoshoku.haonick.util;

import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;

import java.util.logging.Level;

public class NickUtils {

    public static void setNickedValue( Player player, String key, String value ) {
        DBConnection connection = HaoNick.getPlugin().getDBConnection();
        HaoConfig playersConfig = HaoNick.getPlugin().getConfigManager().getPlayersConfig();
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        if ( settingsConfig.getBoolean( "settings.keep_nick.mysql.active" ) ) {
            if ( connection != null )
                connection.setDataAsync( player.getUniqueId(), key, value );
            else
                HaoNick.getPlugin().getLogger().log( Level.WARNING, "No mysql connection available" );
        } else {
            playersConfig.set( player.getUniqueId() + "." + key, value );
            playersConfig.saveConfig();
        }
    }

}
