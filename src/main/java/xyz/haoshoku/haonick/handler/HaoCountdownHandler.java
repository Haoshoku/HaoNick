package xyz.haoshoku.haonick.handler;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;
import xyz.haoshoku.haonick.util.NMSUtils;
import xyz.haoshoku.haonick.util.PlaceholderUtils;
import xyz.haoshoku.haonick.util.TabUtils;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.Constructor;
import java.util.List;

public class HaoCountdownHandler {

    public void startCountdown() {
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        Bukkit.getScheduler().runTaskTimerAsynchronously( HaoNick.getPlugin(), () -> {
            if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.tab.header_and_footer.active" ) )
                return;// Duplicate checks if people deactivates this entry while reloading

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                String header = settingsConfig.getMessage( "settings.tab.header_and_footer.header", player );
                String footer = settingsConfig.getMessage( "settings.tab.header_and_footer.footer", player );

                header = header.replace( "%online%", String.valueOf( Bukkit.getOnlinePlayers().size( ) ) );
                footer = footer.replace( "%online%", String.valueOf( Bukkit.getOnlinePlayers().size( ) ) );
                header = header.replace( "%maxPlayers%", String.valueOf( Bukkit.getMaxPlayers() ) );
                footer = footer.replace( "%maxPlayers%", String.valueOf( Bukkit.getMaxPlayers() ) );
                header = header.replace( "%name%", player.getName() );
                footer = footer.replace( "%name%", player.getName() );
                header = PlaceholderUtils.applyPlaceholder( player, header );
                footer = PlaceholderUtils.applyPlaceholder( player, footer );
                TabUtils.sendTabList( player, header, footer );
            }
        }, 20L, 20L * HaoNick.getPlugin().getConfigManager().getSettingsConfig().getInt( "settings.tab.header_and_footer.update_interval" ) );

        Bukkit.getScheduler().runTaskTimerAsynchronously( HaoNick.getPlugin(), () -> {
            if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.action_bar.active" ) )
                return; // Duplicate checks if people deactivates this entry while reloading
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                List<String> worlds = settingsConfig.getStringList( "settings.action_bar.worlds" );
                String whileNickedMessage = settingsConfig.getMessage( "settings.action_bar.text.while_nicked", player );
                whileNickedMessage = whileNickedMessage.replace( "%name%", NickAPI.getName( player ) );
                if ( HaoUserHandler.getUser( player ).getFakeRank() != null )
                    whileNickedMessage = whileNickedMessage.replace( "%fakeRank%", HaoUserHandler.getUser( player ).getFakeRank() );
                if ( settingsConfig.getBoolean( "settings.action_bar.permanent" ) ) {
                    String permanentMessage = settingsConfig.getMessage( "settings.action_bar.text.permanent", player );
                    permanentMessage = permanentMessage.replace( "%name%", NickAPI.getName( player ) );
                    if ( HaoUserHandler.getUser( player ).getFakeRank() != null )
                        permanentMessage = permanentMessage.replace( "%fakeRank%", HaoUserHandler.getUser( player ).getFakeRank() );
                    if ( worlds.contains( player.getWorld().getName() ) ) {
                        if ( NickAPI.isNicked( player ) )
                            this.sendActionBar( player, whileNickedMessage );
                        else
                            this.sendActionBar( player, permanentMessage );
                    }
                } else {
                    if ( NickAPI.isNicked( player ) ) {
                        if ( worlds.contains( player.getWorld().getName() ) )
                            this.sendActionBar( player, whileNickedMessage );
                    }
                }
            }
        }, 20L, HaoNick.getPlugin().getConfigManager().getSettingsConfig().getInt( "settings.action_bar.update_interval" ) );

        Bukkit.getScheduler().runTaskTimerAsynchronously( HaoNick.getPlugin(), ScoreboardHandling::updateNamesFromScoreboardDelayed, 20L, 20L * HaoNick.getPlugin().getConfigManager().getSettingsConfig().getInt( "settings.tab.timer.update_interval" ) );
    }

    private void sendActionBar( Player player, String message ) {
        try {
            player.spigot().sendMessage( ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText( message ) );
            return;
        } catch ( NoSuchMethodError ignore ) {}

        /*
        For 1.8.8
         */
        String version = Bukkit.getServer().getClass().getName().split( "\\." )[3];
        try {
            Class<?> packetPlayOutChatClass = Class.forName( "net.minecraft.server." + version + ".PacketPlayOutChat" );
            Class<?> iChatBaseComponentClass = Class.forName( "net.minecraft.server." + version + ".IChatBaseComponent" );
            Class<?> chatComponentTextClass = Class.forName( "net.minecraft.server." + version + ".ChatComponentText" );

            Object chatComponentInstance = chatComponentTextClass.getConstructor( String.class ).newInstance( message );
            Constructor<?> constructor = packetPlayOutChatClass.getConstructor( iChatBaseComponentClass, byte.class );
            Object playOutChatInstance = constructor.newInstance( chatComponentInstance, (byte) 2 );
            NMSUtils.sendPacket( player, playOutChatInstance, version );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
