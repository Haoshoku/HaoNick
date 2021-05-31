package xyz.haoshoku.haonick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.manager.HaoUserManager;
import xyz.haoshoku.haonick.user.HaoUser;
import xyz.haoshoku.haonick.util.CommandUtils;
import xyz.haoshoku.haonick.util.NickUtils;
import xyz.haoshoku.haonick.util.TabUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.ArrayList;
import java.util.List;

public class FakeRankCommand extends BukkitCommand {

    private final HaoConfig commandsConfig, fakeRanksConfig, messagesConfig;

    public FakeRankCommand( @NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases ) {
        super( name, description, usageMessage, aliases );
        this.commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        this.fakeRanksConfig = HaoNick.getPlugin().getConfigManager().getFakeRanksConfig();
        this.messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
    }

    @Override
    public boolean execute( @NotNull CommandSender sender, @NotNull String s, @NotNull String[] args ) {

        if ( !CommandUtils.hasPermission( sender, "commands.fake_rank_module.command_permission" ) ) {
            sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.no_permission_player", sender ) );
            return true;
        }

        Player player;

        switch ( args.length ) {
            case 1: {
                if ( !CommandUtils.isPlayer( sender ) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.no_player", null ) );
                    return true;
                }
                String name = args[0].toLowerCase();
                player = (Player) sender;
                this.setFakeRank( null, name, player );
                break;
            }

            case 2: {
                if ( !CommandUtils.hasPermission( sender, "commands.fake_rank_module.change_another_player_permission" ) ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.fake_rank_module.commands.no_permission_target", sender ) );
                    return true;
                }

                String name = args[1].toLowerCase();
                Player target = Bukkit.getPlayer( args[0] );

                if ( target == null ) {
                    sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.target_not_online", sender ) );
                    return true;
                }

                this.setFakeRank( sender, name, target );
                break;
            }

            default:
                List<String> list = new ArrayList<>();

                for ( String fakeRank : this.fakeRanksConfig.getSection( "fake_ranks" ).getKeys( false ) )
                    list.add( ChatColor.translateAlternateColorCodes( '&', this.fakeRanksConfig.getString( "fake_ranks." + fakeRank + ".display_name" ) ) );

                StringBuilder data = new StringBuilder();
                for ( String value : list ) data.append( value ).append( "§8, " );
                data = new StringBuilder( data.substring( 0, data.length() - 2 ) );

                sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.usage", sender ).replace( "%fake_ranks%", data.toString() ));
                break;

        }

        return true;
    }

    private void setFakeRank( CommandSender sender, String name, Player target ) {
        HaoUser targetUser = HaoUserManager.getUser( target );
        Player cooldownPlayer;

        if ( sender instanceof Player )
            cooldownPlayer = (Player) sender;
        else
            cooldownPlayer = target;

        HaoUser user = HaoUserManager.getUser( cooldownPlayer );
        if ( user.getFakeRankModuleCooldown() >= System.currentTimeMillis() ) {
            cooldownPlayer.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.cooldown", sender ) );
            return;
        }

        List<String> list = new ArrayList<>();

        for ( String fakeRank : this.fakeRanksConfig.getSection( "fake_ranks" ).getKeys( false ) )
            list.add( ChatColor.translateAlternateColorCodes( '&', fakeRank ).toLowerCase() );

        if ( !list.contains( name ) ) {
            cooldownPlayer.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.rank_not_exist", sender ) );
            return;
        }

        String permission = this.fakeRanksConfig.getString( "fake_ranks." + name + ".permission" );

        if ( !cooldownPlayer.hasPermission( permission ) ) {
            cooldownPlayer.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.rank_no_permission", sender ) );
            return;
        }

        NickUtils.setNickedValue( target, "fake_rank", name );
        if ( sender != null )
            sender.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.player_changes_target", sender ).replace( "%target%", NickAPI.getName( target ) ) );
        target.sendMessage( this.messagesConfig.getMessage( "messages.commands.fake_rank_module.rank_updated",sender ) );
        targetUser.setFakeRank( name );

        if ( !cooldownPlayer.hasPermission( this.commandsConfig.getString( "commands.fake_rank_module.cooldown_bypass_permission" ) ) )
            HaoUserManager.getUser( cooldownPlayer ).setFakeRankModuleCooldown( System.currentTimeMillis()
                    + ( (long) this.commandsConfig.getInt( "commands.fake_rank_module.cooldown" ) * 1000L ) );

        TabUtils.updateNamesFromScoreboard();
    }
}
