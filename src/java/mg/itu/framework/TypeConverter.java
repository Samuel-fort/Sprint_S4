package mg.itu.framework;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Conversion d'une valeur de formulaire (String) vers un type Java simple. */
public final class TypeConverter {

    private static final Set<Class<?>> SIMPLE = new HashSet<>(Arrays.<Class<?>>asList(
            String.class, Integer.class, Long.class, Double.class, Float.class, Short.class,
            Byte.class, Boolean.class, Character.class, BigDecimal.class, BigInteger.class,
            LocalDate.class, LocalDateTime.class));

    private TypeConverter() {
    }

    public static boolean isSimple(Class<?> type) {
        return type.isPrimitive() || type.isEnum() || SIMPLE.contains(type);
    }

    public static boolean isSimpleArray(Class<?> type) {
        return type.isArray() && isSimple(type.getComponentType());
    }

    /** Valeurs multiples (ex. cases à cocher) -> tableau. Renvoie null si le paramètre est absent. */
    public static Object convertArray(String[] raw, Class<?> arrayType, String name) {
        if (raw == null) {
            return null;
        }
        Class<?> component = arrayType.getComponentType();
        Object array = Array.newInstance(component, raw.length);
        for (int i = 0; i < raw.length; i++) {
            Array.set(array, i, convert(raw[i], component, name));
        }
        return array;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object convert(String raw, Class<?> type, String name) {
        if (raw == null) {
            return defaultValue(type);
        }
        if (type == String.class) {
            return raw;
        }
        String v = raw.trim();
        if (v.isEmpty()) {
            return defaultValue(type);
        }
        try {
            if (type == int.class || type == Integer.class) return Integer.valueOf(v);
            if (type == long.class || type == Long.class) return Long.valueOf(v);
            if (type == double.class || type == Double.class) return Double.valueOf(v);
            if (type == float.class || type == Float.class) return Float.valueOf(v);
            if (type == short.class || type == Short.class) return Short.valueOf(v);
            if (type == byte.class || type == Byte.class) return Byte.valueOf(v);
            if (type == boolean.class || type == Boolean.class) return parseBoolean(v);
            if (type == char.class || type == Character.class) return Character.valueOf(raw.charAt(0));
            if (type == BigDecimal.class) return new BigDecimal(v);
            if (type == BigInteger.class) return new BigInteger(v);
            if (type == LocalDate.class) return LocalDate.parse(v);
            if (type == LocalDateTime.class) return LocalDateTime.parse(v);
            if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, v);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new IllegalArgumentException("Valeur invalide pour '" + name + "' : \"" + raw
                    + "\" (type attendu : " + type.getSimpleName() + ")");
        }
        throw new IllegalArgumentException("Type non supporté pour '" + name + "' : " + type.getName());
    }

    private static Boolean parseBoolean(String v) {
        // une case à cocher cochée envoie "on"
        return "true".equalsIgnoreCase(v) || "on".equalsIgnoreCase(v)
                || "1".equals(v) || "yes".equalsIgnoreCase(v);
    }

    /** Valeur par défaut quand le paramètre est absent ou vide : 0/false pour les primitifs, null sinon. */
    public static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return Boolean.FALSE;
        if (type == char.class) return Character.valueOf('\0');
        if (type == long.class) return Long.valueOf(0L);
        if (type == double.class) return Double.valueOf(0d);
        if (type == float.class) return Float.valueOf(0f);
        if (type == short.class) return Short.valueOf((short) 0);
        if (type == byte.class) return Byte.valueOf((byte) 0);
        return Integer.valueOf(0);
    }
}
