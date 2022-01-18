package xyz.haoshoku.haonick.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MsgUtils {

    public static void sendMessage( CommandSender sender, String message ) {
        if ( message != null ) {
            if ( message.equals( "null" ) || message.equals( "empty" ) )
                return;
            sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', message ) );
        }

    }

}
