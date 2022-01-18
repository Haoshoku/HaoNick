package xyz.haoshoku.haonick.util;

import org.bukkit.entity.Player;

public class NMSUtils {

    public static void sendPacket( Player player, Object packet, String version ) {
        try {
            Object entityPlayerObject = player.getClass().getMethod( "getHandle" ).invoke( player ); // EntityPlayer
            Class<?> packetClass = Class.forName( "net.minecraft.server." + version + ".Packet" );
            Object playerConnectionField = entityPlayerObject.getClass().getField( "playerConnection" ).get( entityPlayerObject );
            playerConnectionField.getClass().getMethod( "sendPacket", packetClass ).invoke( playerConnectionField, packet );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
