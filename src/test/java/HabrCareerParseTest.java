import org.junit.jupiter.api.Test;
import utils.HabrCareerDateTimeParser;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import static org.assertj.core.api.Assertions.*;

class HabrCareerParseTest {
    public HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();

    @Test
    public void whenCorrectFormat() {
        String date = "1994-02-03T13:11:11+03:00";
        OffsetDateTime offsetDateTime = OffsetDateTime.of(1994, 02, 03, 13, 11, 11, 0, ZoneOffset.ofHoursMinutes(3, 0));
        assertThat(timeParser.parse(date)).isEqualTo(offsetDateTime);
    }

    @Test
    public void whenNotCorrectFormat() {
        String date = "02-03-1994T13:11:11+03:00";
        assertThatThrownBy(() -> timeParser.parse(date)).isInstanceOf(DateTimeParseException.class);
    }

    @Test
    public void whenMonthIsOutOfScope() {
        String date = "1994-14-03T13:11:11+03:00";
        assertThatThrownBy(() -> timeParser.parse(date)).isInstanceOf(DateTimeParseException.class);
    }
}