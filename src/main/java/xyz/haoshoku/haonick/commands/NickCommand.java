package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;
import java.util.ListIterator;

public class NickCommand extends BukkitCommand {

    private HaoConfig commandsConfig;
    private HaoConfig messagesConfig;
    private boolean uuid;
    private boolean tag;
    private boolean skin;
    private boolean gameProfileName;
    private List<String> blackList;
    private List<String> resetTagsList;

    public NickCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.nick_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.nick_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.nick_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.nick_module.game_profile_change" );
        this.blackList = this.commandsConfig.getStringList( "commands.nick_module.blacklist" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.nick_module.reset_args" );
        this.listToLowerCase( this.blackList );
        this.listToLowerCase( this.resetTagsList );
    }


    @Override
    public boolean execute( CommandSender sender, String s, String[] args ) {

        Player player;

        if ( !CommandUtils.hasPermission( sender, "commands.nick_module.command_permission" ) ) {
            sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.no_permission_player", sender ) );
            return true;
        }

        switch ( args.length ) {
            case 1: {
                if ( !CommandUtils.isPlayer( sender ) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.no_player", sender ) );
                    return true;
                }
                player = (Player) sender;
                String name = args[0];

                if ( !this.checkConditions( player, name ) ) return true;

                boolean nicked = this.nick( null, player, name );

                if ( !nicked )
                    player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.player_reset", sender ) );
                else
                    player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.player_nicks", sender ).replace( "%name%", name ) );
                break;
            }

            case 2: {
                String name = args[1];
                Player target = Bukkit.getPlayer( args[0] );

                if ( !CommandUtils.hasPermission( sender, "commands.nick_module.change_another_player_permission" ) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.no_permission_target", sender ) );
                    return true;
                }


                if ( target == null ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.target_not_online", sender ) );
                    return true;
                }

                if ( !this.resetTagsList.contains( name.toLowerCase() ) && CommandUtils.isPlayer( sender ) ) {
                    player = (Player) sender;
                    if ( !this.checkConditions( player, name ) )
                        return true;
                }

                boolean nicked = this.nick( sender, target, name );

                if ( !nicked ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.player_resets_target", target )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    target.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.target_gets_reset", target )
                            .replace( "%sender%", sender.getName() ) );
                } else {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.player_nicks_target", target )
                            .replace( "%name%", name )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    target.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.target_gets_nicked", target )
                            .replace( "%name%", name )
                            .replace( "%sender%", sender.getName() ));
                }
                break;
            }

            default:
                sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.usage", sender ) );
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
        if ( HaoUserManager.getUser( player ).getNickModuleCooldown() >= System.currentTimeMillis() ) {
            player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.cooldown", player ) );
            return false;
        }

        if ( name.length() < this.commandsConfig.getInt( "commands.nick_module.min_length" ) ) {
            player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.min_length", player ) );
            return false;
        }

        if ( NickAPI.nickExists( name ) ) {
            player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.nick_exists", player ).replace( "%name%", name ) );
            return false;
        }

        boolean containsCheck = this.commandsConfig.getBoolean( "commands.nick_module.blacklist_contains_check" );

        if ( containsCheck ) {
            for ( String contents : this.blackList ) {
                if ( name.toLowerCase().contains( contents.toLowerCase() ) ) {
                    player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.blacklist", player ) );
                    return false;
                }
            }
        }

        if ( this.blackList.contains( name.toLowerCase() ) ) {
            player.sendMessage( this.messagesConfig.getMessage( "messages.commands.nick_module.blacklist", player ) );
            return false;
        }

        return true;
    }

    private boolean nick( CommandSender sender, Player target, String name ) {
        Player cooldownPlayer;

        if ( sender instanceof Player )
            cooldownPlayer = (Player) sender;
        else
            cooldownPlayer = target;

        if ( !cooldownPlayer.hasPermission( this.commandsConfig.getString( "commands.nick_module.cooldown_bypass_permission" ) ) )
            HaoUserManager.getUser( cooldownPlayer ).setNickModuleCooldown( System.currentTimeMillis()
                    + ( (long) this.commandsConfig.getInt( "commands.nick_module.cooldown" ) * 1000L ) );


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
        return true;
    }

    public void reloadConfig() {
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.nick_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.nick_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.nick_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.nick_module.game_profile_change" );
        this.blackList = this.commandsConfig.getStringList( "commands.nick_module.blacklist" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.nick_module.reset_args" );
        this.listToLowerCase( this.blackList );
        this.listToLowerCase( this.resetTagsList );
    }

}
