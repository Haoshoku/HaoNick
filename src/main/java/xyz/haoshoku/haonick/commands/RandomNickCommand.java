package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.handler.HaoUserHandler;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.haonick.util.MsgUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

public class RandomNickCommand extends BukkitCommand {

    private HaoConfig commandsConfig, messagesConfig;
    private boolean uuid, tag, skin, gameProfileName;
    private List<String> randomNamesList, resetTagsList;

    public RandomNickCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.random_nick_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.random_nick_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.random_nick_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.random_nick_module.game_profile_change" );
        this.randomNamesList = this.commandsConfig.getStringList( "commands.random_nick_module.random_names" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.random_nick_module.reset_args" );
        this.listToLowerCase( this.resetTagsList );
        this.resetTagsList.add( "force_unnick" );
    }


    @Override
    public boolean execute( CommandSender sender, String s, String[] args ) {

        if ( !CommandUtils.hasPermission( sender, "commands.random_nick_module.command_permission" ) ) {
            MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.random_nick_module.no_permission_player", sender ) );
            return true;
        }

        Player player;

        switch ( args.length ) {
            case 0: {
                if ( !CommandUtils.isPlayer( sender ) ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.no_player", sender ) );
                    return true;
                }

                player = (Player) sender;
                String name = this.getRandomName();

                if ( name == null ) {
                    MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.random_nick_module.no_names_available", sender ) );
                    return true;
                }

                if ( NickAPI.isNicked( player ) )
                    name = "force_unnick";

                if ( !this.checkConditions( player ) ) return true;

                boolean nicked = this.nick( null, player, name );

                if ( !nicked )
                    MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.random_nick_module.player_reset", sender ) );
                else
                    MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.random_nick_module.player_nicks", sender ).replace( "%name%", name ) );
                break;
            }

            case 1: {
                String name = this.getRandomName();
                Player target = Bukkit.getPlayer( args[0] );

                if ( !sender.hasPermission( this.commandsConfig.getString( "commands.random_nick_module.change_another_player_permission" ) ) ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.random_nick_module.no_permission_target", sender ) );
                    return true;
                }

                if ( target == null ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.random_nick_module.target_not_online", sender ) );
                    return true;
                }

                if ( !this.resetTagsList.contains( name.toLowerCase() ) && CommandUtils.isPlayer( sender ) ) {
                    player = (Player) sender;
                    if ( !this.checkConditions( player ) )
                        return true;
                }

                if ( NickAPI.isNicked( target ) && this.commandsConfig.getBoolean( "commands.random_nick_module.always_new_name" ) )
                    name = "force_unnick";

                boolean nicked = this.nick( sender, target, name );

                if ( !nicked ) {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.random_nick_module.player_resets_target", target )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    MsgUtils.sendMessage( target, this.messagesConfig.getMessage( "messages.commands.random_nick_module.target_gets_reset", target )
                            .replace( "%sender%", sender.getName() ) );
                } else {
                    MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.random_nick_module.player_nicks_target", target )
                            .replace( "%name%", name )
                            .replace( "%target%", NickAPI.getOriginalName( target ) ) );
                    MsgUtils.sendMessage( target, this.messagesConfig.getMessage( "messages.commands.random_nick_module.target_gets_nicked", target )
                            .replace( "%name%", name )
                            .replace( "%sender%", sender.getName() ));
                }
                break;
            }
        }

        return true;
    }

    private void listToLowerCase( List<String> list ) {
        ListIterator<String> iterator = list.listIterator();
        while ( iterator.hasNext() )
            iterator.set( iterator.next().toLowerCase() );
    }

    private boolean checkConditions( Player player ) {
        if ( HaoUserHandler.getUser( player ).getRandomModuleCooldown() >= System.currentTimeMillis() ) {
            MsgUtils.sendMessage( player, this.messagesConfig.getMessage( "messages.commands.random_nick_module.cooldown", player ) );
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

        if ( !cooldownPlayer.hasPermission( this.commandsConfig.getString( "commands.random_nick_module.cooldown_bypass_permission" ) ) )
            HaoUserHandler.getUser( cooldownPlayer ).setRandomModuleCooldown( System.currentTimeMillis()
                    + ( (long) this.commandsConfig.getInt( "commands.random_nick_module.cooldown" ) * 1000L ) );



        if ( this.resetTagsList.contains( name.toLowerCase() ) ) {
            if ( this.uuid ) NickAPI.resetUniqueId( target );
            if ( this.tag ) NickAPI.resetNick( target );
            if ( this.skin ) NickAPI.resetSkin( target );
            if ( this.gameProfileName ) NickAPI.resetGameProfileName( target );
            NickAPI.refreshPlayer( target );
            return false;
        }

        name = ChatColor.translateAlternateColorCodes( '&', name );

        if ( this.uuid ) NickAPI.setUniqueId( target, name );
        if ( this.tag ) NickAPI.nick( target, name );
        if ( this.skin ) NickAPI.setSkin( target, name );
        if ( this.gameProfileName ) NickAPI.setGameProfileName( target, name );
        NickAPI.refreshPlayer( target );

        for ( String command : this.commandsConfig.getStringList( "commands.random_nick_module.command_execution" ) ) {
            if ( !command.equalsIgnoreCase( "none" ) )
                target.performCommand( command );
        }
        return true;
    }

    public String getRandomName() {
        List<String> randomNamesList = this.randomNamesList;
        List<String> availableNamesList = new LinkedList<>();

        for ( String name : randomNamesList ) {
            if ( !NickAPI.isNickedName( name ) )
                availableNamesList.add( name );
        }

        if ( availableNamesList.isEmpty() )
            return null;

        String randomName = availableNamesList.get( ThreadLocalRandom.current().nextInt( availableNamesList.size() ) );
        return randomName;
    }

    public void reloadConfig() {
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.uuid = this.commandsConfig.getBoolean( "commands.random_nick_module.uuid" );
        this.tag = this.commandsConfig.getBoolean( "commands.random_nick_module.tag" );
        this.skin = this.commandsConfig.getBoolean( "commands.random_nick_module.skin" );
        this.gameProfileName = this.commandsConfig.getBoolean( "commands.random_nick_module.game_profile_change" );
        this.randomNamesList = this.commandsConfig.getStringList( "commands.random_nick_module.random_names" );
        this.resetTagsList = this.commandsConfig.getStringList( "commands.random_nick_module.reset_args" );
        this.listToLowerCase( this.resetTagsList );
        this.resetTagsList.add( "force_unnick" );
    }

}
