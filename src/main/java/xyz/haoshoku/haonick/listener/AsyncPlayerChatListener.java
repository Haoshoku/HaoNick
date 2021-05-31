package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.Field;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler( priority = EventPriority.HIGH )
    public void onChat( AsyncPlayerChatEvent event ) {
        Player player = event.getPlayer();
        HaoUser user = HaoUserManager.getUser( player );

        HaoConfig fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();
        HaoConfig ranksConfig = HaoNick.getPlugin().getConfigManager().getRanksConfig();
        if ( NickAPI.isNicked( player ) ) {
            if ( !fakeRanksConfig.getBoolean( "fake_ranks_settings.chat" ) ) return;
            String fakeRank = user.getFakeRank();
            if ( fakeRank == null || user.getFakeRank().equals( "" ) || fakeRanksConfig.getConfig().getString( "fake_ranks." + fakeRank ) == null ) {
                event.setCancelled( true );
                Bukkit.broadcastMessage( "" );
                Bukkit.broadcastMessage( "§cHaoNick error: Player " + NickAPI.getOriginalName( player ) + " do not have a fake rank to write!" );
                Bukkit.broadcastMessage( "§cPlease delete the whole HaoNick config if you need a quick fix." );
                Bukkit.broadcastMessage( "§cIf this error is still not fixed, please contact Haoshoku by joining https://haoshoku.xyz/go/discord" );
                Bukkit.broadcastMessage( "" );
                return;
            }

            String chatMessage = fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".chat.message" );
            chatMessage = chatMessage.replace( "%name%", NickAPI.getName( player ) );
            chatMessage = chatMessage.replace( "%message%", event.getMessage() );
            chatMessage = chatMessage.replace( "%world%", player.getWorld().getName() );

            if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null )
                chatMessage = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( player, chatMessage );

            if ( HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.chat.broadcast_message_instead_of_format" ) ) {
                event.setCancelled( true );

                if ( fakeRanksConfig.getBoolean( "fake_ranks." + fakeRank + ".chat.chatcolor" ) )
                    chatMessage = ChatColor.translateAlternateColorCodes( '&', chatMessage );
                Bukkit.broadcastMessage( chatMessage );
            } else {
                event.setFormat( chatMessage );
                if ( fakeRanksConfig.getBoolean( "fake_ranks." + fakeRank + ".chat.chatcolor" ) )
                    this.setField( event, "format", ChatColor.translateAlternateColorCodes( '&', event.getFormat() ) );
            }

        } else {
            if ( !ranksConfig.getBoolean( "ranks_settings.chat" ) ) return;
            String rank = user.getRank();

            if ( rank == null ) {
                event.setCancelled( true );
                return;
            }

            String chatMessage = ranksConfig.getString( "ranks." + rank + ".chat.message" );
            chatMessage = chatMessage.replace( "%name%", NickAPI.getOriginalName( player ) );
            chatMessage = chatMessage.replace( "%message%", "%2$s" );
            chatMessage = chatMessage.replace( "%name%", player.getWorld().getName() );

            if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null )
                chatMessage = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( player, chatMessage );

            event.setFormat( chatMessage );
            if ( ranksConfig.getBoolean( "ranks." + rank + ".chat.chatcolor" ) )
                this.setField( event, "format", ChatColor.translateAlternateColorCodes( '&', event.getFormat() ) );
        }
    }

    private void setField( Object packet, String f, Object value ) {
        try {
            Field field = packet.getClass().getDeclaredField( f );
            field.setAccessible( true );
            field.set( packet, value );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
