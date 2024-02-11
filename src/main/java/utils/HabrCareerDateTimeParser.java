package utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(parse);
        LocalDateTime localDateTime = LocalDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(localDateTime.atOffset(offsetDateTime.getOffset()));
        return  localDateTime;
    }
}
