package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onLogin( PlayerLoginEvent event ) {
        Player player = event.getPlayer();
        HaoUser user = HaoUserManager.getUser( player );
        user.setPlayer( player );

        for ( Player players : Bukkit.getOnlinePlayers() ) {
            if ( players == player ) continue;
            if ( NickAPI.getUniqueId( players ).equals( player.getUniqueId() ) || NickAPI.getName( players ).equalsIgnoreCase( player.getName() )
            || NickAPI.getName( players ).equalsIgnoreCase( user.getNickedTag() ) ) {
                NickAPI.resetUniqueId( players );
                NickAPI.resetNick( players );
                NickAPI.resetGameProfileName( players );
                NickAPI.resetSkin( players );
                NickAPI.refreshPlayerSync( players );
                players.sendMessage( HaoNick.getPlugin().getConfigManager().getMessagesConfig().getMessage( "messages.join_reset", player ) );
            }
        }
    }

}
