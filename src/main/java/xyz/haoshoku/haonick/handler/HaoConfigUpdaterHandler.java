package xyz.haoshoku.haonick.handler;

import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.config.HaoConfig;

import java.util.*;

public class HaoConfigUpdaterHandler {

    private final List<Map<String, Object>> commandsYMLConfig;
    private final List<Map<String, Object>> messagesYMLConfig;
    private final List<Map<String, Object>> settingsYMLConfig;

    public HaoConfigUpdaterHandler() {
        this.commandsYMLConfig = new LinkedList<>();
        this.messagesYMLConfig = new LinkedList<>();
        this.settingsYMLConfig = new LinkedList<>();

        this.initializeCommandsYML();
        this.initializeMessagesYML();
        this.initializeSettingsYML();
        this.updateCommands();
        this.updateMessages();
        this.updateSettings();
    }

    private void initializeCommandsYML() {
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.command", "nick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.command_permission", "haonick.nick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.cooldown_bypass_permission", "haonick.nick.bypass" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.change_another_player_permission", "haonick.nick.others" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.chatcolor_permission", "haonick.chatcolor" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.min_length", 3 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.cooldown", 5 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.uuid", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.tag", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.skin", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.game_profile_change", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.description", "Change your player tag and uuid" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.usage", "/nick <Name>" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.aliases", Collections.singletonList( "changenick" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.reset_args", Arrays.asList( "reset", "off", "clear", "unnick" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.whitelist", Collections.singletonList( "Minecraft" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.blacklist", Arrays.asList( "Haoshoku", "Notch", "Craft" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.blacklist_contains_check", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_module.command_execution", Collections.singletonList( "none" ) ) );


        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.command", "nicklist" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.command_permission", "haonick.nicklist" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.description", "List up all names" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.usage", "/nicklist" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.aliases", Collections.singletonList( "nlist" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_list_module.command_execution", Collections.singletonList( "none" ) ) );


        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload_module.command", "nickreload" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload_module.command_permission", "haonick.nickreload" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload_module.description", "Reload the config" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload_module.usage", "/nickreload" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload_module.aliases", Collections.singletonList( "nreload" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.nick_reload.command_execution", Collections.singletonList( "none" ) ) );

        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.command", "skin" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.command_permission", "haonick.skin" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.cooldown_bypass_permission", "haonick.skin.bypass" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.change_another_player_permission", "haonick.skin.others" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.nick_exists_check", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.cooldown", 5 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.uuid", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.tag", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.skin", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.game_profile_change", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.description", "Change your skin" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.usage", "/skin <Name>" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.aliases", Collections.singletonList( "changeskin" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.reset_args", Arrays.asList( "reset", "off", "clear", "unnick" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.blacklist", Arrays.asList( "Haoshoku", "Notch" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.skin_module.command_execution", Collections.singletonList( "none" ) ) );


        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.command", "unnick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.command_permission", "haonick.unnick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.cooldown_bypass_permission", "haonick.unnick.bypass" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.change_another_player_permission", "haonick.unnick.others" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.cooldown", 5 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.description", "Change your player tag and uuid" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.usage", "/unnick <Name>" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.aliases", Collections.singletonList( "unnickplayer" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.unnick_module.command_execution", Collections.singletonList( "none" ) ) );


        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.command", "randomnick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.command_permission", "haonick.randomnick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.cooldown_bypass_permission", "haonick.randomnick.bypass" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.change_another_player_permission", "haonick.randomnick.others" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.cooldown", 5 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.uuid", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.tag", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.skin", true ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.game_profile_change", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.description", "Gives yourself a random nick" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.usage", "/randomnick <Name>" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.always_new_name", false ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.aliases", Collections.singletonList( "random" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.reset_args", Arrays.asList( "reset", "off", "clear", "unnick" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.random_names", Arrays.asList( "RandomNick1", "RandomNick2", "RandomNick3", "RandomNick4", "RandomNick5" ) ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.random_nick_module.command_execution", Collections.singletonList( "none" ) ) );


        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.command", "fakerank" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.command_permission", "haonick.fakerank" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.cooldown_bypass_permission", "haonick.fakerank.bypass" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.change_another_player_permission", "haonick.fakerank.others" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.cooldown", 5 ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.description", "Set a fake rank" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.usage", "/fakerank <rank>" ) );
        this.commandsYMLConfig.add( this.getConfigValue( "commands.fake_rank_module.aliases", Arrays.asList( "setrank", "setfakerank" ) ) );
    }

    private void initializeMessagesYML() {
        this.messagesYMLConfig.add( this.getConfigValue( "messages.prefix", "&5HaoNick &8|&7" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.no_player", "You must be a player to execute this command!" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.join_reset", "%prefix% &cYour fake uuid and your tag have been reset because a player with the same uuid joined the server" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.nick_data_kept", "%prefix% &aYour name is &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.join_message", "%prefix% &c%player% &ajoined the game" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.quit_message", "%prefix% &c%player% &aleft the game" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.no_permission_player", "%prefix% &cYou do not have the permission to use the nick module" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.usage", "%prefix% &e/nick <Name> %line% %prefix% &e/nick <Player> <Name> %line% %prefix% &e/nick reset" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.cooldown", "%prefix% &cPlease wait 5 seconds until you are using this command again" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.nick_exists", "%prefix% &cThe nick %name% is already being used" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.blacklist", "%prefix% &cThis name is blacklisted" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.min_length", "%prefix% &cThis name is too short" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.player_reset", "%prefix% &aYou successfully reset your name" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.player_nicks", "%prefix% &aYour new name is now &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.no_permission_target", "%prefix% &cYou do not have the permissions to rename other players" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.target_not_online", "%prefix% &cThis player is not online" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.player_resets_target", "%prefix% &aYou successfully reset &e%target% &aname" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.target_gets_reset", "%prefix% &cYour nickname has been reset by &e%sender%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.player_nicks_target", "%prefix% &aYou successfully changed the name of &e%target% &ato &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_module.target_gets_nicked", "%prefix% &aYour name has been changed by &e%sender% &ato &e%name%" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_list_module.no_permission_player", "%prefix% &cYou do not have the permission having a look at the nick list" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_list_module.nobody_is_nicked", "%prefix% &cNobody is nicked!" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_list_module.head_line", "%prefix% &eNick-List:" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_list_module.message", "&c%original_name% &8-> &a%nicked_name%" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_reload_module.no_permission_player", "%prefix% &cYou do not have the permission to reload the config" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.nick_reload_module.reload_done", "%prefix% &aThe configuration has been reloaded successfully" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.no_permission_player", "%prefix% &cYou do not have the permission to use the random module" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.usage", "%prefix% &e/random <Name> %line% %prefix% &e/random <Player> <Name> %line% %prefix% &e/random reset" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.cooldown", "%prefix% &cPlease wait 5 seconds until you are using this command again" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.no_names_available", "%prefix% &cNo randomnames available" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.player_reset", "%prefix% &aYou successfully reset your name" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.player_nicks", "%prefix% &aYour new name is now &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.no_permission_target", "%prefix% &cYou do not have the permissions to rename other players" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.target_not_online", "%prefix% &cThis player is not online" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.player_resets_target", "%prefix% &aYou successfully reset &e%target% &aname" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.target_gets_reset", "%prefix% &cYour nickname has been reset by &e%sender%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.player_nicks_target", "%prefix% &aYou successfully changed the name of &e%target% &ato &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.random_nick_module.target_gets_nicked", "%prefix% &aYour name has been changed by &e%sender% &ato &e%name%" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.no_permission_player", "%prefix% &cYou do not have the permission to use the skin module" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.usage", "%prefix% &e/skin <Name> %line% %prefix% &e/skin <Player> <Name> %line% %prefix% &e/skin reset" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.cooldown", "%prefix% &cPlease wait 5 seconds until you are using this command again" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.blacklist", "%prefix% &cThis name is blacklisted" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.player_reset", "%prefix% &aYou successfully reset your skin" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.player_changes_skin", "%prefix% &aYour new skin is now &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.no_permission_target", "%prefix% &cYou do not have the permissions to rename other players" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.target_not_online", "%prefix% &cThis player is not online" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.player_resets_target", "%prefix% &aYou successfully reset &e%target% &askin" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.target_gets_reset", "%prefix% &cYour skin has been reset by &e%sender%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.player_changes_skin_target", "%prefix% &aYou successfully changed the skin of &e%target% &ato &e%name%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.skin_module.target_gets_skin_changed", "%prefix% &aYour skin has been changed by &e%sender% &ato &e%name%" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.no_permission_player", "%prefix% &cYou do not have the permission to use the unnick module" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.usage", "%prefix% &e/unnick {Player}" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.cooldown", "%prefix% &cPlease wait 5 seconds until you are using this command again" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.player_reset", "%prefix% &aYou successfully reset your name" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.no_permission_target", "%prefix% &cYou do not have the permissions to rename other players" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.target_not_online", "%prefix% &cThis player is not online" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.player_resets_target", "%prefix% &aYou successfully reset &e%target% &aname" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.unnick_module.target_gets_reset", "%prefix% &cYour nickname has been reset by &e%sender%" ) );

        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.no_permission_player", "%prefix% &cYou do not have the permission to use the fake rank module" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.usage", "%prefix% &aThese fake ranks are available after being nicked: %line% &7- &e%fake_ranks% %line% &aWrite &e/fakerank <rank> &ato set your fake rank!" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.cooldown", "%prefix% &cPlease wait 5 seconds until you are using this command again" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.rank_not_exist", "%prefix% &cThis fake rank does not exist" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.rank_updated", "%prefix% &aYour fake rank has been updated" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.rank_no_permission", "%prefix% &cYou do not have the permissions to give yourself this rank" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.no_permission_target", "%prefix% &cYou do not have the permissions to change fake rank of other players" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.target_not_online", "%prefix% &cThis player is not online" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.player_changes_target", "%prefix% &aSuccessfully changed fake rank of &e%target%" ) );
        this.messagesYMLConfig.add( this.getConfigValue( "messages.commands.fake_rank_module.target_gets_changed", "%prefix% &cYour fake rank has been updated" ) );
    }

    private void initializeSettingsYML() {
        this.settingsYMLConfig.add( this.getConfigValue( "settings.join_message", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.quit_message", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.active", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.permission", "haonick.keep" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.data.uuid", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.data.tag", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.data.skin", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.data.game_profile_name", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.random_name_on_join", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.active", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.host", "localhost" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.port", 3306 ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.database", "database" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.username", "root" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.password", "password" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.keep_nick.mysql.table_name", "nick" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.chat.broadcast_message_instead_of_format", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.timer.active", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.timer.update_interval", 60 ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.async", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.fake_ranks_permission_based", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.scoreboard_creation_time", 10 ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.header_and_footer.active", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.header_and_footer.header", "&aYour header can be edited within settings.yml of HaoNick folder" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.header_and_footer.footer", "&cYour footer can be edited within settings.yml of HaoNick folder" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.tab.header_and_footer.update_interval", 600 ) );

        this.settingsYMLConfig.add( this.getConfigValue( "settings.death_message.active", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.death_message.text.dead", "Player %player% died" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.death_message.text.killed_by_player", "Player %player% was killed by %killer%" ) );

        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.active", false ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.permanent", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.text.permanent", "&cName: &c%name%" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.text.while_nicked", "&cYou are currently nicked as &e%name%" ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.update_interval", 20 ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.action_bar.worlds", Collections.singletonList( "world" ) ) );

        this.settingsYMLConfig.add( this.getConfigValue( "settings.blacklisted_worlds", Collections.singletonList( "yourWorldNameHere" ) ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.luckperms_support", false ) );

        this.settingsYMLConfig.add( this.getConfigValue( "settings.placeholder_support", true ) );
        this.settingsYMLConfig.add( this.getConfigValue( "settings.error_log", true ) );
    }

    private void updateCommands() {
        HaoConfig commandsConfig = HaoNick.getPlugin().getConfigManager().getCommandsConfig();
        List<String> currentConfigList = new LinkedList<>( commandsConfig.getConfig().getConfigurationSection( "" ).getKeys( true ) ); // aktuelle, not updated
        List<Map<String, Object>> commandsYMLConfigCopyList = new LinkedList<>( this.commandsYMLConfig );
        List<String> bypassList = Arrays.asList( "commands", "commands.nick_module", "commands.nick_list_module",
                "commands.nick_reload_module", "commands.skin_module", "commands.unnick_module", "commands.random_nick_module", "commands.fake_rank_module" );
        for ( Map<String, Object> map : commandsYMLConfigCopyList ) {
            for ( String key : map.keySet() ) {
                if ( currentConfigList.contains( key ) ) {
                    currentConfigList.remove( key );
                    this.commandsYMLConfig.remove( map );
                }
            }
        }
        for ( Map<String, Object> map : this.commandsYMLConfig ) {
            for ( Map.Entry<String, Object> entry : map.entrySet() )
                commandsConfig.set( entry.getKey(), entry.getValue() );
        }
        for ( String key : currentConfigList ) {
            if ( !bypassList.contains( key ) )
                commandsConfig.set( key, null );
        }
        commandsConfig.saveConfig();
    }

    private void updateMessages() {
        HaoConfig messagesConfig = HaoNick.getPlugin().getConfigManager().getMessagesConfig();
        List<String> currentConfigList = new LinkedList<>( messagesConfig.getConfig().getConfigurationSection( "" ).getKeys( true ) ); // aktuelle, not updated
        List<Map<String, Object>> messagesYMLConfigCopyList = new LinkedList<>( this.messagesYMLConfig );
        List<String> bypassList = Arrays.asList( "messages", "messages.commands", "messages.commands.nick_module",
                "messages.commands.nick_list_module", "messages.commands.nick_reload_module", "messages.commands.random_nick_module",
                "messages.commands.skin_module", "messages.commands.unnick_module", "messages.commands.fake_rank_module" );
        for ( Map<String, Object> map : messagesYMLConfigCopyList ) {
            for ( String key : map.keySet() ) {
                if ( currentConfigList.contains( key ) ) {
                    currentConfigList.remove( key );
                    this.messagesYMLConfig.remove( map );
                }
            }
        }
        for ( Map<String, Object> map : this.messagesYMLConfig ) {
            for ( Map.Entry<String, Object> entry : map.entrySet() )
                messagesConfig.set( entry.getKey(), entry.getValue() );
        }
        for ( String key : currentConfigList ) {
            if ( !bypassList.contains( key ) )
                messagesConfig.set( key, null );
        }
        messagesConfig.saveConfig();
    }


    private void updateSettings() {
        HaoConfig settingsConfig = HaoNick.getPlugin().getConfigManager().getSettingsConfig();
        List<String> currentConfigList = new LinkedList<>( settingsConfig.getConfig().getConfigurationSection( "" ).getKeys( true ) ); // aktuelle, not updated
        List<Map<String, Object>> settingsYMLConfigCopyList = new LinkedList<>( this.settingsYMLConfig );
        List<String> bypassList = Arrays.asList( "settings", "settings.keep_nick", "settings.keep_nick.data",
                "settings.keep_nick.mysql", "settings.chat", "settings.tab", "settings.tab.timer", "settings.tab.header_and_footer",
                "settings.action_bar", "settings.action_bar.text", "settings.death_message", "settings.death_message.text", "settings.blacklisted_worlds" );
        for ( Map<String, Object> map : settingsYMLConfigCopyList ) {
            for ( String key : map.keySet() ) {
                if ( currentConfigList.contains( key ) ) {
                    currentConfigList.remove( key );
                    this.settingsYMLConfig.remove( map );
                }
            }
        }
        for ( Map<String, Object> map : this.settingsYMLConfig ) {
            for ( Map.Entry<String, Object> entry : map.entrySet() )
                settingsConfig.set( entry.getKey(), entry.getValue() );
        }
        for ( String key : currentConfigList ) {
            if ( !bypassList.contains( key ) )
                settingsConfig.set( key, null );

        }
        settingsConfig.saveConfig();
    }

    private Map<String, Object> getConfigValue( String path, Object value ) {
        Map<String, Object> map = new HashMap<>();
        map.put( path, value );
        return map;
    }

}
