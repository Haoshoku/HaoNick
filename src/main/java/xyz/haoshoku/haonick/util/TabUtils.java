package xyz.haoshoku.haonick.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;

import java.lang.reflect.Method;

public class TabUtils {

    public static void sendTabList( Player player, String header, String footer ) {
        if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.tab.header_and_footer.active" ) ) return;
        try {
            player.setPlayerListHeaderFooter( header, footer );
        } catch ( NoSuchMethodError apply ) {
            TabUtils.sendTabListWithReflection( player, header, footer );
        }
    }

    private static void sendTabListWithReflection( Player player, String header, String footer ) {
        if ( header == null ) header = "";
        if ( footer == null ) footer = "";

        header = PlaceholderUtils.applyPlaceholder( player, header );
        footer = PlaceholderUtils.applyPlaceholder( player, footer );

        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[3];
        header = ChatColor.translateAlternateColorCodes( '&', header );
        footer = ChatColor.translateAlternateColorCodes( '&', footer );

        try {
            Class<?> tabHeaderChatBaseComponentA = Class.forName( "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer" );
            Method methodOfTabHeaderChatBaseComponent = tabHeaderChatBaseComponentA.getMethod( "a", String.class );
            Object resultOfTabHeaderChatBaseComponent = methodOfTabHeaderChatBaseComponent.invoke( tabHeaderChatBaseComponentA, "{\"text\": \"" + header + "\"}" );
            Class<?> tabFooterChatBaseComponentA = Class.forName( "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer" );
            Method methodOfTabFooterChatBaseComponent = tabFooterChatBaseComponentA.getMethod( "a", String.class );
            Object resultOfTabFooterChatBaseComponent = methodOfTabFooterChatBaseComponent.invoke( tabFooterChatBaseComponentA, "{\"text\": \"" + footer + "\"}" );
            Class<?> packetPlayOutPlayerListHeaderFooter = Class.forName( "net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter" );
            Object newInstanceOfPlayerListHeaderFooter = packetPlayOutPlayerListHeaderFooter.newInstance();
            ReflectionUtils.setField( newInstanceOfPlayerListHeaderFooter, "a", resultOfTabHeaderChatBaseComponent );
            ReflectionUtils.setField( newInstanceOfPlayerListHeaderFooter, "b", resultOfTabFooterChatBaseComponent );
            NMSUtils.sendPacket( player, newInstanceOfPlayerListHeaderFooter, version );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
