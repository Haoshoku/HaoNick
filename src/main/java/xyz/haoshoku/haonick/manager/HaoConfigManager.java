package xyz.haoshoku.haonick.manager;

import org.bukkit.ChatColor;
import xyz.haoshoku.haonick.config.HaoConfig;

public class HaoConfigManager {

    private final HaoConfig commandsConfig, fakeRanksConfig, messagesConfig, playersConfig, ranksConfig, settingsConfig;

    private String prefix;

    public HaoConfigManager() {
        this.commandsConfig = new HaoConfig( "commands.yml" );
        this.fakeRanksConfig = new HaoConfig( "fake_ranks.yml" );
        this.messagesConfig = new HaoConfig( "messages.yml" );
        this.playersConfig = new HaoConfig( "players.yml" );
        this.ranksConfig = new HaoConfig( "ranks.yml" );
        this.settingsConfig = new HaoConfig( "settings.yml" );
    }

    public HaoConfig getCommandsConfig() {
        return this.commandsConfig;
    }

    public HaoConfig getFakeRanksConfig() {
        return this.fakeRanksConfig;
    }

    public HaoConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public HaoConfig getSettingsConfig() {
        return this.settingsConfig;
    }

    public HaoConfig getRanksConfig() {
        return this.ranksConfig;
    }

    public HaoConfig getPlayersConfig() {
        return playersConfig;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix() {
        this.prefix = ChatColor.translateAlternateColorCodes( '&', this.messagesConfig.getString( "messages.prefix" ) );
    }
}
