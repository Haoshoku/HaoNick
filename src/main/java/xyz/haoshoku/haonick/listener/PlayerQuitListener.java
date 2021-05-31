package xyz.haoshoku.haonick.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerQuitListener implements Listener {

    private HaoNick plugin;

    public PlayerQuitListener() {
        this.plugin = HaoNick.getPlugin();
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();

        if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.quit_message" ) )
            event.setQuitMessage( this.plugin.getConfigManager().getMessagesConfig().getMessage( "messages.quit_message", player )
                    .replace( "%player%", NickAPI.getName( player ) )  );

        HaoUserManager.deleteUser( player );
    }

}
