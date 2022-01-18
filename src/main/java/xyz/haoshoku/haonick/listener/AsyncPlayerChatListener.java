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
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.haonick.util.ErrorUtils;
import xyz.haoshoku.haonick.util.PatternUtils;
import xyz.haoshoku.haonick.util.PlaceholderUtils;
import xyz.haoshoku.nick.api.NickAPI;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onChat( AsyncPlayerChatEvent event ) {
        Player player = event.getPlayer();
        HaoUser user = HaoUserHandler.getUser( player );

        if ( event.isCancelled() ) return;

        HaoConfig fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();
        HaoConfig ranksConfig = HaoNick.getPlugin().getConfigManager().getRanksConfig();
        if ( NickAPI.isNicked( player ) ) {
            if ( !fakeRanksConfig.getBoolean( "fake_ranks_settings.chat" ) ) return;
            String fakeRank = user.getFakeRank();
            if ( fakeRank == null || user.getFakeRank().equals( "" ) || fakeRanksConfig.getConfig().getString( "fake_ranks." + fakeRank ) == null ) {
                event.setCancelled( true );
                ErrorUtils.err( "Player " + NickAPI.getOriginalName( player ) + " does not have a fake_rank to write! You probably forgot to add a default rank! Do it by writing §edefault: true §cinto a specific rank in §efake_ranks.yml" );
                return;
            }

            String chatMessage = fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".chat.message" );
            chatMessage = chatMessage.replace( "%name%", NickAPI.getName( player ) );
            chatMessage = chatMessage.replace( "%world%", player.getWorld().getName() );
            chatMessage = PlaceholderUtils.applyPlaceholder( player, chatMessage );

            if ( HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.chat.broadcast_message_instead_of_format" ) ) {
                chatMessage = chatMessage.replace( "%message%", event.getMessage() );
                event.setCancelled( true );

                if ( fakeRanksConfig.getBoolean( "fake_ranks." + fakeRank + ".chat.chatcolor" ) ) {
                    chatMessage = ChatColor.translateAlternateColorCodes( '&', chatMessage );
                    chatMessage = PatternUtils.format( chatMessage );
                }

                Bukkit.broadcastMessage( chatMessage );
            } else {
                chatMessage = chatMessage.replace( "%message%", "%2$s" );
                event.setFormat( chatMessage );
            }

        } else {
            if ( !ranksConfig.getBoolean( "ranks_settings.chat" ) ) return;
            String rank = user.getRank();

            if ( rank == null ) {
                event.setCancelled( true );
                ErrorUtils.err( "Player " + NickAPI.getOriginalName( player ) + " does not have a rank to write! You probably forgot to add a default rank! Do it by writing §edefault: true §cinto a specific rank in §eranks.yml" );
                return;
            }
            String chatMessage = ranksConfig.getString( "ranks." + rank + ".chat.message" );
            chatMessage = chatMessage.replace( "%name%", NickAPI.getOriginalName( player ) );
            chatMessage = chatMessage.replace( "%message%", event.getMessage() );
            chatMessage = chatMessage.replace( "%world%", player.getWorld().getName() );
            chatMessage = PlaceholderUtils.applyPlaceholder( player, chatMessage );

            if ( HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.chat.broadcast_message_instead_of_format" ) ) {
                chatMessage = chatMessage.replace( "%message%", event.getMessage() );
                event.setCancelled( true );

                if ( ranksConfig.getBoolean( "ranks." + rank + ".chat.chatcolor" ) ) {
                    chatMessage = ChatColor.translateAlternateColorCodes( '&', chatMessage );
                    chatMessage = PatternUtils.format( chatMessage );
                }

                Bukkit.broadcastMessage( chatMessage );
            } else {
                chatMessage = chatMessage.replace( "%message%", "%2$s" );
                event.setFormat( chatMessage );
            }
        }
    }

}
