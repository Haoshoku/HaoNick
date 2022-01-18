package xyz.haoshoku.haonick.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;

public class CommandUtils {

    public static boolean hasPermission( CommandSender sender, String permission ) {
        HaoConfig config = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        return sender.hasPermission( config.getString( permission ) ) || sender.hasPermission( "haonick.*" );
    }

    public static boolean isPlayer( CommandSender sender ) {
        return sender instanceof Player;
    }

}
