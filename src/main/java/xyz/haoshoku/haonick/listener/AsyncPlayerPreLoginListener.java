package xyz.haoshoku.haonick.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.haoshoku.haonick.handler.HaoReloadHandler;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin( AsyncPlayerPreLoginEvent event ) {
        new HaoReloadHandler().executeAsyncPlayerPreLoginEvent( event.getUniqueId(), event.getName() );
    }
}
