package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import xyz.haoshoku.haonick.handler.HaoReloadHandler;
import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;

public class PluginEnableListener implements Listener {

    @EventHandler
    public void onEnable( PluginEnableEvent event ) {
        if ( event.getPlugin().getName().equals( "HaoNick" ) ) {
            HaoReloadHandler handler = new HaoReloadHandler();
            for ( Player online : Bukkit.getOnlinePlayers() ) {
                handler.executeAsyncPlayerPreLoginEvent( online.getUniqueId(), online.getName() );
                handler.executeOnLoginEvent( online );
                ScoreboardHandling.updateNamesFromScoreboardDelayed();
            }
        }
    }

}
