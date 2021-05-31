package xyz.haoshoku.haonick.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.haoshoku.haonick.HaoNick;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onCommand( PlayerCommandPreprocessEvent event ) {
        Player player = event.getPlayer();

        if ( event.getMessage().toLowerCase().startsWith( "/haonickinfo" ) ) {
            event.setCancelled( true );
            player.sendMessage( HaoNick.getPlugin().getConfigManager().getPrefix() + " §5HaoNick §ev" + HaoNick.getPlugin().getDescription().getVersion() + "§7 by §cHaoshoku" );
        }
    }

}
