package xyz.haoshoku.haonick.util;

import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

    private static final Pattern PATTERN = Pattern.compile( "(?<!\\\\)(#[a-fA-F0-9]{6})" );

    public static String format( String message ) {
        String version = ScoreboardHandling.VERSION;

        if ( !version.equals( "v1_16_R3" ) && !version.equals( "v1_17_R1" ) && !version.equals( "v1_18_R1" ) )
            return message;

        Matcher matcher = PatternUtils.PATTERN.matcher( message ); // Creates a matcher with the given pattern & message

        while ( matcher.find() ) { // Searches the message for something that matches the pattern
            String color = message.substring( matcher.start(), matcher.end() ); // Extracts the color from the message
            message = message.replace( color, "" +  net.md_5.bungee.api.ChatColor.of( color ) ); // Places the color in the message
        }

        return message; // Returns the message
    }

}
