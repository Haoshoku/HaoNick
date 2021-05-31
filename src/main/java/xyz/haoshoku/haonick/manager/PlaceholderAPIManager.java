package xyz.haoshoku.haonick.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.nick.api.NickAPI;

public class PlaceholderAPIManager extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "haonick";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Haoshoku";
    }

    @Override
    public @NotNull String getVersion() {
        return HaoNick.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest( Player player, @NotNull String params ) {
        if ( player == null || !player.isOnline() )
            return "";

        switch ( params ) {
            case "original_name": case "originalName":
                return NickAPI.getOriginalName( player );

            case "nick_name": case "nicked_name": case "nickname": case "name":
                return NickAPI.getName( player );

            case "isNicked": case "is_nicked":
                return String.valueOf( NickAPI.isNicked( player ) );

            case "isSkinChanged": case "is_skin_changed":
                return String.valueOf( NickAPI.isSkinChanged( player ) );

            case "uuid":
                return String.valueOf( NickAPI.getUniqueId( player ) );
        }

        return super.onPlaceholderRequest( player, params );
    }


}
