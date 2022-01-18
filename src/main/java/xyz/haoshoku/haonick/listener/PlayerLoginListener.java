package xyz.haoshoku.haonick.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import xyz.haoshoku.haonick.handler.HaoReloadHandler;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onLogin( PlayerLoginEvent event ) {
        new HaoReloadHandler().executeOnLoginEvent( event.getPlayer() );
    }

}
