package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.haonick.util.MsgUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NickListCommand extends BukkitCommand {

    private HaoConfig commandsConfig, messagesConfig;

    public NickListCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
    }

    @Override
    public boolean execute( CommandSender sender, String s, String[] args ) {
        if ( !CommandUtils.hasPermission( sender, "commands.nick_list_module.command_permission" ) ) {
            MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.nick_list_module.no_permission_player", sender ) );
            return true;
        }

        MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.nick_list_module.head_line", sender ) );
        Map<UUID, String> nickedPlayers = NickAPI.getNickedPlayers();

        if ( nickedPlayers.size() == 0 ) {
            MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.nick_list_module.nobody_is_nicked", sender ) );
            return true;
        }

        for ( Map.Entry<UUID, String> entry : nickedPlayers.entrySet() ) {
            Player player = Bukkit.getPlayer( entry.getKey() );
            String originalName = NickAPI.getOriginalName( player );
            String nickedName = entry.getValue();

            String msg = this.messagesConfig.getMessage( "messages.commands.nick_list_module.message", sender );
            msg = msg.replace( "%original_name%", originalName );
            msg = msg.replace( "%nicked_name%", nickedName );
            MsgUtils.sendMessage( sender, msg );
        }

        if ( sender instanceof Player ) {
            for ( String command : this.commandsConfig.getStringList( "commands.nick_list_module.command_execution" ) ) {
                if ( !command.equalsIgnoreCase( "none" ) )
                    ( (Player) sender ) .performCommand( command );
            }
        }

        return true;
    }

    public void reloadConfig() {
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
    }
}
