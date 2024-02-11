import org.junit.jupiter.api.Test;
import utils.HabrCareerDateTimeParser;

import java.time.*;
import java.time.format.DateTimeParseException;
import static org.assertj.core.api.Assertions.*;

class HabrCareerParseTest {
    public HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();

    @Test
    public void whenCorrectFormat() {
        String date = "1994-02-03T13:11:11+03:00";
        LocalDateTime localDateTime = LocalDateTime.of(1994, 02, 03, 13, 11, 11);
        assertThat(timeParser.parse(date)).isEqualTo(localDateTime);
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