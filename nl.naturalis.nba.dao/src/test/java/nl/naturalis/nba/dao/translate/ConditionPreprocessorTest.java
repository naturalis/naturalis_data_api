package nl.naturalis.nba.dao.translate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;

@SuppressWarnings("static-method")
public class ConditionPreprocessorTest {

	@Test
	public void testConvertValueForDateField() throws InvalidConditionException
	{
		OffsetDateTime date = OffsetDateTime.parse("2017-02-03T10:15:30+01:00");
		System.out.println(date);
		
		LocalDateTime ldt = LocalDateTime.parse("2001-08-09T14:08:10");
		System.out.println(ldt);
		
		String str = "1986/04/08";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
		LocalDate dateTime = LocalDate.parse(str, formatter);
		System.out.println(dateTime);
		
	}

}
