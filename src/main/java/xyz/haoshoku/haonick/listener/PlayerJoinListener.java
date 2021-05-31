package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
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
        HaoUser user = HaoUserManager.getUser( player );

        if ( this.plugin.getConfigManager().getSettingsConfig().getBoolean( "settings.join_message" ) )
            event.setJoinMessage( null );

        if ( player.isOp() && Bukkit.getPluginManager().getPlugin( "NickAPI" ) == null ) {
            player.sendMessage( "§7[§5HaoNick§7] §aThank you for using HaoNick!" );
            player.sendMessage( "§7[§5HaoNick§7] §eHaoNick §cneeds §eNickAPI §cto work." );
            player.sendMessage( "§7[§5HaoNick§7] §cDiscord: §ehttps://haoshoku.xyz/go/discord" );
            player.sendMessage( "§7[§5HaoNick§7] §cPlease download it here:" );
            player.sendMessage( "§7[§5HaoNick§7] §ehttps://haoshoku.xyz/go/nickapi" );
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously( this.plugin, () -> {
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
                            nickDataKept = true;
                        }
                    }

                    if ( user.getNickedGameProfile() != null ) {
                        if ( NickAPI.getConfig().isGameProfileChanges() ) {
                            if ( this.settingsConfig.getBoolean( "settings.keep_nick.data.game_profile_name" ) ) {
                                NickAPI.setGameProfileName( player, user.getNickedGameProfile() );
                            }
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
                player.sendMessage( this.plugin.getConfigManager().getMessagesConfig().getMessage( "messages.nick_data_kept", player ).replace( "%name%", NickAPI.getName( player ) ) );
            TabUtils.updateNamesFromScoreboard();

        }, 3L );

        Bukkit.getScheduler().runTaskLater( HaoNick.getPlugin(), () -> {
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
