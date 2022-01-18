package xyz.haoshoku.haonick.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import xyz.haoshoku.haonick.handler.HaoUserHandler;

public class PlayerKickListener implements Listener {

    @EventHandler
    public void onKick( PlayerKickEvent event ) {
        Player player = event.getPlayer();
        HaoUserHandler.deleteUser( player );
    }

}
