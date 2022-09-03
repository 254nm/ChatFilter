package me.txmc.chatshit;

import org.bukkit.ChatColor;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.logging.Level;

public class Utils {
    private static final String PREFIX = Main.getInstance().getConfig().getString("MessagePrefix");
    public static void unpackResource(String resourceName, File file) {
        try {
            InputStream is = Main.class.getClassLoader().getResourceAsStream(resourceName);
            if (is == null) throw new NullPointerException(String.format("Resource %s is not present in the jar", resourceName));
            Files.copy(is, file.toPath());
            is.close();
        } catch (Throwable t) {
            log("&cFailed to extract resource from jar due to &r&3 %s&r&c! Please see the stacktrace below for more info", t.getMessage());
            t.printStackTrace();
        }
    }

    public static void log(String format, Object... args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String message = String.format(format, args);
        message = translateChars(message);
        Main.getInstance().getLogger().log(Level.INFO, String.format("%s%c%s", message, Character.MIN_VALUE, element.getClassName()));
    }

    public static void sendMessage(Object obj, String message) {
        sendMessage(obj, message, true);
    }

    public static void sendMessage(Object obj, String msg, boolean prefix) {
        if (prefix) msg = String.format("%s &7➠&r %s", PREFIX, msg);
        msg = translateChars(msg);
        try {
            Method method = obj.getClass().getMethod("sendMessage", String.class);
            method.setAccessible(true);
            method.invoke(obj, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String translateChars(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
