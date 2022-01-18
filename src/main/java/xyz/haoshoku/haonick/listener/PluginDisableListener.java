package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.handler.HaoUserHandler;

public class PluginDisableListener implements Listener {

    @EventHandler
    public void onDisable( PluginDisableEvent event ) {
        if ( event.getPlugin().getName().equals( "HaoNick" ) ) {
            Bukkit.getScheduler().cancelTasks( HaoNick.getPlugin() );
            HaoUserHandler.deleteAllUser();
        }
    }

}
