package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.haonick.util.MsgUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;
import java.util.ListIterator;

public class SkinCommand extends BukkitCommand {

    private HaoConfig commandsConfig, messagesConfig;
    private boolean uuid, tag, skin, gameProfileName;
    private List<String> blackList, resetTagsList;

    public SkinCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.skin_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.skin_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.skin_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.skin_module.game_profile_change" );
        this.blackList = this.commandsConfig.getStringList( "commands.skin_module.blacklist" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.skin_module.reset_args" );
        this.listToLowerCase( this.blackList );
        this.listToLowerCase( this.resetTagsList );
    }


    @Override
    public boolean execute( CommandSender sender, String s, String[] args ) {

        if ( !CommandUtils.hasPermission( sender, "commands.skin_module.command_permission" ) ) {
            MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.no_permission_player", sender ) );
            return true;
        }

        Player player;

        switch ( args.length ) {
            case 1: {
                if ( !CommandUtils.isPlayer( sender ) ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.no_player", sender ) );
                    return true;
                }
                player = (Player) sender;
                String name = args[0];

                if ( !this.checkConditions( player, name ) ) return true;

                boolean skinChanged = this.changeSkin( null, player, name );

                if ( !skinChanged )
                    MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.skin_module.player_reset", sender ) );
                else
                    MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.skin_module.player_changes_skin", sender ).replace( "%name%", name ) );
                break;
            }

            case 2: {
                String name = args[1];
                Player target = Bukkit.getPlayer( args[0] );

                if ( !CommandUtils.hasPermission( sender, "commands.skin_module.change_another_player_permission" ) ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.no_permission_target", sender ) );
                    return true;
                }

                if ( target == null ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.target_not_online", sender ) );
                    return true;
                }

                if ( !this.resetTagsList.contains( name.toLowerCase() ) && CommandUtils.isPlayer( sender ) ) {
                    player = (Player) sender;
                    if ( !this.checkConditions( player, name ) )
                        return true;
                }

                boolean skinChanged = this.changeSkin( sender, target, name );

                if ( !skinChanged ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.player_resets_target", target )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    MsgUtils.sendMessage( target, this.messagesConfig.getMessage( "messages.commands.skin_module.target_gets_reset", target )
                            .replace( "%sender%", sender.getName() ) );
                } else {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.player_changes_skin_target", target )
                            .replace( "%name%", name )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    MsgUtils.sendMessage( target, this.messagesConfig.getMessage( "messages.commands.skin_module.target_gets_skin_changed", target )
                            .replace( "%name%", name )
                            .replace( "%sender%", sender.getName() ) );
                }
                break;
            }

            default:
                MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.skin_module.usage", sender ) );
                break;
        }

        return true;
    }

    private void listToLowerCase( List<String> list ) {
        ListIterator<String> iterator = list.listIterator();
        while ( iterator.hasNext() )
            iterator.set( iterator.next().toLowerCase() );
    }

    private boolean checkConditions( Player player, String name ) {
        if ( HaoUserHandler.getUser( player ).getSkinModuleCooldown() >= System.currentTimeMillis() ) {
            MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.skin_module.cooldown", player ) );
            return false;
        }

        if ( NickAPI.nickExists( name ) && this.commandsConfig.getBoolean( "commands.skin_module.nick_exists" ) ) {
            MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.nick_module.nick_exists", player ).replace( "%name%", name ) );
            return false;
        }

        if ( this.blackList.contains( name.toLowerCase() ) ) {
            MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.skin_module.blacklist", player ) );
            return false;
        }

        return true;
    }

    private boolean changeSkin( CommandSender sender, Player target, String name ) {
        Player cooldownPlayer;

        if ( sender instanceof Player )
            cooldownPlayer = (Player) sender;
        else
            cooldownPlayer = target;

        if ( !cooldownPlayer.hasPermission( this.commandsConfig.getString( "commands.skin_module.cooldown_bypass_permission" ) ) )
            HaoUserHandler.getUser( cooldownPlayer ).setSkinModuleCooldown( System.currentTimeMillis()
                    + ( (long) this.commandsConfig.getInt( "commands.skin_module.cooldown" ) * 1000L ) );

        if ( this.resetTagsList.contains( name.toLowerCase() ) ) {
            if ( this.uuid ) NickAPI.resetUniqueId( target );
            if ( this.tag ) NickAPI.resetNick( target );
            if ( this.skin ) NickAPI.resetSkin( target );
            if ( this.gameProfileName ) NickAPI.resetGameProfileName( target );
            NickAPI.refreshPlayer( target );
            return false;
        }

        if ( this.uuid ) NickAPI.setUniqueId( target, name );
        if ( this.tag ) NickAPI.nick( target, name );
        if ( this.skin ) NickAPI.setSkin( target, name );
        if ( this.gameProfileName ) NickAPI.setGameProfileName( target, name );
        NickAPI.refreshPlayer( target );

        for ( String command : this.commandsConfig.getStringList( "commands.skin_module.command_execution" ) ) {
            if ( !command.equalsIgnoreCase( "none" ) )
                target.performCommand( command );
        }
        return true;
    }

    public void reloadConfig() {
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.skin_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.skin_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.skin_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.skin_module.game_profile_change" );
        this.blackList = this.commandsConfig.getStringList( "commands.skin_module.blacklist" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.skin_module.reset_args" );
        this.listToLowerCase( this.blackList );
        this.listToLowerCase( this.resetTagsList );
    }

}
