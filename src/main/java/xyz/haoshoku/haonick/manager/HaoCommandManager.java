package xyz.haoshoku.haonick.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.defaults.BukkitCommand;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.commands.*;
import xyz.haoshoku.haonick.config.HaoConfig;

public class HaoCommandManager {

    private final FakeRankCommand fakeRankCommand;
    private final NickCommand nickCommand;
    private final NickListCommand nickListCommand;
    private final NickReloadCommand nickReloadCommand;
    private final RandomNickCommand randomNickCommand;
    private final SkinCommand skinCommand;
    private final UnnickCommand unnickCommand;

    public HaoCommandManager() {
        HaoConfig config = HaoNick.getPlugin().getConfigManager().getCommandsConfig();

        this.fakeRankCommand = new FakeRankCommand( config.getString( "commands.fake_rank_module.command" ),
                config.getString( "commands.fake_rank_module.description" ), config.getString( "commands.fake_rank_module.usage" ), config.getStringList( "commands.fake_rank_module.aliases" ) );
        this.nickCommand = new NickCommand( config.getString( "commands.nick_module.command" ),
                config.getString( "commands.nick_module.description" ), config.getString( "commands.nick_module.usage" ), config.getStringList( "commands.nick_module.aliases" ) );
        this.nickListCommand = new NickListCommand( config.getString( "commands.nick_list_module.command" ),
                config.getString( "commands.nick_list_module.description" ), config.getString( "commands.nick_list_module.usage" ), config.getStringList( "commands.nick_list_module.aliases" ) );
        this.nickReloadCommand = new NickReloadCommand( config.getString( "commands.nick_reload_module.command" ),
                config.getString( "commands.nick_reload_module.description" ), config.getString( "commands.nick_reload_module.usage" ), config.getStringList( "commands.nick_reload_module.aliases" ) );
        this.randomNickCommand = new RandomNickCommand( config.getString( "commands.random_nick_module.command" ),
                config.getString( "commands.random_nick_module.description" ), config.getString( "commands.random_nick_module.usage" ), config.getStringList( "commands.random_nick_module.aliases" ) );
        this.skinCommand = new SkinCommand( config.getString( "commands.skin_module.command" ),
                config.getString( "commands.skin_module.description" ), config.getString( "commands.skin_module.usage" ), config.getStringList( "commands.skin_module.aliases" ) );
        this.unnickCommand = new UnnickCommand( config.getString( "commands.unnick_module.command" ),
                config.getString( "commands.unnick_module.description" ), config.getString( "commands.unnick_module.usage" ), config.getStringList( "commands.unnick_module.aliases" ) );


        String fakeRankModuleCommand = config.getString( "commands.fake_rank_module.command" );
        String nickModuleCommand = config.getString( "commands.nick_module.command" );
        String nickListModuleCommand = config.getString( "commands.nick_list_module.command" );
        String nickReloadModuleCommand = config.getString( "commands.nick_reload_module.command" );
        String randomNickModuleCommand = config.getString( "commands.random_nick_module.command" );
        String skinModuleCommand = config.getString( "commands.skin_module.command" );
        String unnickModuleCommand = config.getString( "commands.unnick_module.command" );

        if ( fakeRankModuleCommand != null && !fakeRankModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", fakeRankCommand );
        if ( nickModuleCommand != null && !nickModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", nickCommand );
        if ( nickListModuleCommand != null && !nickListModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", nickListCommand );
        if ( nickReloadModuleCommand != null && !nickReloadModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", nickReloadCommand );
        if ( randomNickModuleCommand != null && !randomNickModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", randomNickCommand );
        if ( skinModuleCommand != null && !skinModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", skinCommand );
        if ( unnickModuleCommand != null && !unnickModuleCommand.equalsIgnoreCase( "null" ) ) this.registerCommandDynamically( "haonick", unnickCommand );
    }


    private void registerCommandDynamically( String command, BukkitCommand commandInstance ) {
        try {
            Object commandMapMethod = Bukkit.getServer().getClass().getMethod( "getCommandMap" ).invoke( Bukkit.getServer() );
            commandMapMethod.getClass().getMethod( "register", String.class, Command.class ).invoke( commandMapMethod, command, commandInstance );
        } catch ( Exception e ) {
            System.out.println( "Failed to register " + command );
            e.printStackTrace();
        }
    }

    public FakeRankCommand getFakeRankCommand() {
        return this.fakeRankCommand;
    }

    public NickCommand getNickCommand() {
        return this.nickCommand;
    }

    public NickListCommand getNickListCommand() {
        return this.nickListCommand;
    }

    public RandomNickCommand getRandomNickCommand() {
        return this.randomNickCommand;
    }

    public SkinCommand getSkinCommand() {
        return this.skinCommand;
    }

    public UnnickCommand getUnnickCommand() {
        return this.unnickCommand;
    }
}
