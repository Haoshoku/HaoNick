package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.haonick.util.ErrorUtils;
import xyz.haoshoku.haonick.util.MsgUtils;
import xyz.haoshoku.haonick.util.NickUtils;
import xyz.haoshoku.haonick.util.TabUtils;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerJoinListener implements Listener {

    private final HaoNick plugin;
    private final HaoConfig settingsConfig;

    public PlayerJoinListener() {
        this.plugin = HaoNick.getPlugin();
        this.settingsConfig = this.plugin.getConfigManager().getSettingsConfig();
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();
        HaoUser user = HaoUserHandler.getUser( player );

        if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.join_message" ) )
            event.setJoinMessage( null );

        if ( player.isOp() && Bukkit.getPluginManager().getPlugin( "NickAPI" ) == null ) {
            ErrorUtils.err( "§cHaoNick §cneeds §eNickAPI §cto work. \n§cDownload the NickAPI here: §ehttps://haoshoku.xyz/go/nickapi"  );
            return;
        }

        if ( !settingsConfig.getBoolean( "settings.tab.async" ) )
            ScoreboardHandling.updateNamesFromScoreboard();
        else
            ScoreboardHandling.updateNamesFromScoreboardAsync();

        Bukkit.getScheduler().runTaskLaterAsynchronously( this.plugin, () -> {
            if ( !player.isOnline() ) return;
            boolean nickDataKept = false;
            if ( player.hasPermission( "haonick.*" ) || player.hasPermission(  this.settingsConfig.getString( "settings.keep_nick.permission" ) ) ) {
                if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.keep_nick.active" ) ) {
                    if ( user.getNickedUUID() != null ) {
                        boolean allowChange = true;
                        for ( Player players : Bukkit.getOnlinePlayers() ) {
                            if ( NickAPI.getUniqueId( players ).equals( user.getNickedUUID() ) )
                                allowChange = false;
                        }

                        if ( allowChange ) {
                            if ( this.settingsConfig.getBoolean( "settings.keep_nick.data.uuid" ) ) {
                                NickAPI.setUniqueId( player, user.getNickedUUID() );
                                nickDataKept = true;
                            }

                        }
                    }

                    if ( user.getNickedTag() != null ) {
                        if ( this.settingsConfig.getBoolean( "settings.keep_nick.data.tag" ) ) {
                            NickAPI.nick( player, user.getNickedTag() );
                            nickDataKept = true;
                            this.applyRandomNameCheck( user );
                        }
                    }

                    if ( user.getNickedSkinValue() != null && user.getNickedSkinSignature() != null ) {
                        if ( this.settingsConfig.getBoolean( "settings.keep_nick.data.skin" ) ) {
                            NickAPI.setSkin( player, user.getNickedSkinValue(), user.getNickedSkinSignature() );
                        }
                    }

                    if ( user.getNickedGameProfile() != null ) {
                        if ( NickAPI.getConfig().isGameProfileChanges() ) {
                            if ( this.settingsConfig.getBoolean( "settings.keep_nick.data.game_profile_name" ) )
                                NickAPI.setGameProfileName( player, user.getNickedGameProfile() );
                        }
                        nickDataKept = true;
                    }

                    NickAPI.refreshPlayer( player );
                }

            } else {
                NickUtils.setNickedValue( player, "nicked_uuid", "-" );
                NickUtils.setNickedValue( player, "nicked_tag", "-" );
                NickUtils.setNickedValue( player, "nicked_skin_value", "-" );
                NickUtils.setNickedValue( player, "nicked_skin_signature", "-" );
                NickUtils.setNickedValue( player, "nicked_game_profile_name", "-" );
            }


            if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.join_message" ) )
                Bukkit.broadcastMessage( this.plugin.getConfigManager().getMessagesConfig().getMessage( "messages.join_message", player )
                        .replace( "%player%", NickAPI.getName( player ) ) );
            if ( nickDataKept )
                MsgUtils.sendMessage( player, this.plugin.getConfigManager().getMessagesConfig().getMessage( "messages.nick_data_kept", player ).replace( "%name%", NickAPI.getName( player ) ) );
            ScoreboardHandling.updateNamesFromScoreboardDelayed();
        }, 3L );

        Bukkit.getScheduler().runTaskLaterAsynchronously( HaoNick.getPlugin(), () -> {
            if ( this.settingsConfig.getConfig().getBoolean( "settings.tab.header_and_footer.active" ) ) {
                for ( Player online : Bukkit.getOnlinePlayers() )
                    TabUtils.sendTabList( online, this.settingsConfig.getMessage( "settings.tab.header_and_footer.header", player ), this.settingsConfig.getMessage( "settings.tab.header_and_footer.footer", player ) );
            }
        }, 3L );
    }


    private void applyRandomNameCheck( HaoUser user ) {
        if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.keep_nick.random_name_on_join" ) ) {
            String randomName = this.plugin.getCommandManager().getRandomNickCommand().getRandomName();
            if ( randomName != null )
                user.setNickedTag( randomName );
        }
    }

}
