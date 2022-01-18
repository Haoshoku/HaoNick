package xyz.haoshoku.haonick.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerDeathListener implements Listener {

    private final HaoConfig settingsConfig;

    public PlayerDeathListener() {
        this.settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onDeath( PlayerDeathEvent event ) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if ( !this.settingsConfig.getBoolean( "settings.death_message.active" ) ) return;
        if ( killer != null )
            event.setDeathMessage( this.settingsConfig.getMessage( "settings.death_message.text.killed_by_player", null ).replace( "%killer%", NickAPI.getName( killer ) ).replace( "%player%", NickAPI.getName( player ) ) );
        else
            event.setDeathMessage( this.settingsConfig.getMessage( "settings.death_message.text.dead", null ).replace( "%player%", NickAPI.getName( player ) ) );
    }


}
