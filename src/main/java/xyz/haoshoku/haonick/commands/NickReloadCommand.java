package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.handler.HaoCommandHandler;
import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.haonick.util.MsgUtils;

import java.util.List;

public class NickReloadCommand extends BukkitCommand {

    private final HaoConfig commandsConfig, fakeRanksConfig, messagesConfig, ranksConfig, settingsConfig;

    public NickReloadCommand( String name, String description, String usageMessage, List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();
        this.ranksConfig = HaoNick.getPlugin().getConfigManager().getRanksConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        this.settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
    }

    @Override
    public boolean execute( @NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings ) {

        if ( !CommandUtils.hasPermission( sender, "commands.nick_reload_module.command_permission" ) ) {
            MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.nick_reload_module.no_permission_player", sender ) );
            return true;
        }

        this.commandsConfig.reloadConfig();
        this.fakeRanksConfig.reloadConfig();
        this.messagesConfig.reloadConfig();
        this.ranksConfig.reloadConfig();
        this.settingsConfig.reloadConfig();

        HaoCommandHandler manager = HaoNick.getPlugin().getCommandManager();
        manager.getNickCommand().reloadConfig();
        manager.getNickListCommand().reloadConfig();
        manager.getRandomNickCommand().reloadConfig();
        manager.getSkinCommand().reloadConfig();
        manager.getUnnickCommand().reloadConfig();
        this.updatePlaceholderAPI();
        ScoreboardHandling.PLAYER_SCOREBOARD_MAP.clear();
        ScoreboardHandling.updateNamesFromScoreboard();
        MsgUtils.sendMessage( sender, this.messagesConfig.getMessage( "messages.commands.nick_reload_module.reload_done", sender ) );

        if ( sender instanceof Player ) {
            for ( String command : this.commandsConfig.getStringList( "commands.nick_reload_module.command_execution" ) ) {
                if ( !command.equalsIgnoreCase( "none" ) )
                    ( (Player) sender ) .performCommand( command );
            }
        }
        return true;
    }

    private void updatePlaceholderAPI() {
        if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null ) {
            HaoNick.getPlugin().getPlaceholderAPIManager().unregister();
            HaoNick.getPlugin().getPlaceholderAPIManager().register();
        }
    }
}
