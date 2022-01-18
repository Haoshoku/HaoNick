package xyz.haoshoku.haonick.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Object getDeclaredField( Object object, String f ) {
        try {
            Field field = object.getClass().getDeclaredField( f );
            field.setAccessible( true );
            return field.get( object );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setField( Object clazz, String f, Object value ) {
        try {
            Field field = clazz.getClass().getDeclaredField( f );
            field.setAccessible( true );
            field.set( clazz, value );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
