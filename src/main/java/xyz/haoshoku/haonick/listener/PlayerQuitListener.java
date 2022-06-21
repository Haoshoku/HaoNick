package xyz.haoshoku.haonick.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.haonick.util.NickUtils;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerQuitListener implements Listener {

    private final HaoNick plugin;

    public PlayerQuitListener() {
        this.plugin = HaoNick.getPlugin();
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        HaoUser user = HaoUserHandler.getUser( player );

        if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.quit_message" ) )
            event.setQuitMessage( this.plugin.getConfigManager().getMessagesConfig().getMessage( "messages.quit_message", player )
                    .replace( "%player%", NickAPI.getName( player ) )  );

        if ( user.getFakeRank() != null )
            NickUtils.setNickedValue( player, "fake_rank", user.getFakeRank() );
        HaoUserHandler.deleteUser( player );
    }

}
