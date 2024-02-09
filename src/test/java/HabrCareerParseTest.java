import org.junit.jupiter.api.Test;
import utils.HabrCareerDateTimeParser;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import static org.assertj.core.api.Assertions.*;

class HabrCareerParseTest {
    public HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();

    @Test
    public void whenCorrectFormat() {
        String date = "1994-02-03T13:11:11";
        LocalDateTime date2 = LocalDateTime.of(1994, 02, 03, 13, 11, 11);
        assertThat(timeParser.parse(date)).isEqualTo(date2);
    }

    @Test
    public void whenNotCorrectFormat() {
        String date = "02-03-1994T13:11:11";
        assertThatThrownBy(() -> timeParser.parse(date)).isInstanceOf(DateTimeParseException.class);
    }

    @Test
    public void whenMonthIsOutOfScope() {
        String date = "1994-14-03T13:11:11";
        assertThatThrownBy(() -> timeParser.parse(date)).isInstanceOf(DateTimeParseException.class);
    }
}