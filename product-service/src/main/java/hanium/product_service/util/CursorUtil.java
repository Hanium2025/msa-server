package hanium.product_service.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

public class CursorUtil {

    private CursorUtil() {
    }

    public static String encode(LocalDateTime ts, long id) {
        long epochMillis = ts.toInstant(ZoneOffset.UTC).toEpochMilli();
        String raw = epochMillis + "|" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Decoded decode(String cursor) {
        if (cursor == null || cursor.isBlank())
            return null;
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split("\\|", 2);
        long epoch = Long.parseLong(parts[0]);
        long id = Long.parseLong(parts[1]);
        LocalDateTime ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
        return new Decoded(ts, id);
    }

    public record Decoded(LocalDateTime ts, long id) {
    }
}
