package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;

public class UnnickCommand extends BukkitCommand {

    private HaoConfig commandsConfig, messagesConfig;

    public UnnickCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
    }


    @Override
    public boolean execute( CommandSender sender, String s, String[] args ) {
        if ( !sender.hasPermission( this.commandsConfig.getString( "commands.unnick_module.command_permission" ) ) ) {
            sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.no_permission_player", sender ) );
            return true;
        }

        Player player;


        switch ( args.length ) {
            case 0: {
                if ( ! ( sender instanceof Player) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.no_player", sender ) );
                    return true;
                }
                player = (Player) sender;
                if ( !this.checkConditions( player ) ) return true;
                this.unnick( null, player );
                player.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.player_reset", sender ) );
                break;
            }

            case 1: {
                Player target = Bukkit.getPlayer( args[0] );

                if ( !sender.hasPermission( this.commandsConfig.getString( "commands.unnick_module.change_another_player_permission" ) ) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.no_permission_target", sender ) );
                    return true;
                }

                if ( target == null ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.target_not_online", sender ) );
                    return true;
                }

                if ( sender instanceof Player ) {
                    player = (Player) sender;
                    if ( !this.checkConditions( player ) ) return true;
                }

                this.unnick( sender, target );
                sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.player_resets_target", target )
                        .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                target.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.target_gets_reset", target )
                        .replace( "%sender%", sender.getName() ) );
                break;
            }

            default:
                sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.usage", sender ) );
                break;
        }
        return true;
    }


    private boolean checkConditions( Player player ) {
        if ( HaoUserManager.getUser( player ).getUnnickModuleCooldown() >= System.currentTimeMillis() ) {
            player.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.cooldown", player ) );
            return false;
        }

        return true;
    }

    private void unnick( CommandSender sender, Player target ) {
        HaoUser haoUser = HaoUserManager.getUser( target );

        Player cooldownPlayer;

        if ( sender instanceof Player )
            cooldownPlayer = (Player) sender;
        else
            cooldownPlayer = target;

        if ( HaoUserManager.getUser( cooldownPlayer ).getNickModuleCooldown() >= System.currentTimeMillis() ) {
            cooldownPlayer.sendMessage( this.messagesConfig.getMessage( "messages.commands.unnick_module.cooldown", cooldownPlayer ) );
            return;
        }

        if ( !cooldownPlayer.hasPermission( this.commandsConfig.getString( "commands.unnick_module.cooldown_bypass_permission" ) ) )
            HaoUserManager.getUser( cooldownPlayer ).setUnnickModuleCooldown( System.currentTimeMillis()
                    + ( (long) this.commandsConfig.getInt( "commands.unnick_module.cooldown" ) * 1000L ) );

        NickAPI.resetNick( target );
        NickAPI.resetGameProfileName( target );
        NickAPI.resetSkin( target );
        NickAPI.resetUniqueId( target );
        NickAPI.refreshPlayer( target );

        DBConnection connection = HaoNick.getPlugin().getDBConnection();
        if ( connection != null ) {
            haoUser.setNickedUUID( null );
            haoUser.setNickedTag( null );
            haoUser.setNickedSkinValue( null );
            haoUser.setNickedSkinSignature( null );
            haoUser.setNickedGameProfile( null );
        }

    }

    public void reloadConfig() {
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
    }
}
