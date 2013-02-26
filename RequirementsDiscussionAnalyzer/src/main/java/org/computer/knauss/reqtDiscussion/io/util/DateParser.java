package org.computer.knauss.reqtDiscussion.io.util;

import java.sql.Date;
import java.sql.Time;

public abstract class DateParser {

	private static DateParser INSTANCE;
	/**
	 * Expect something like 2013-01-16 16:29:23.437
	 */
	static final DateParser PSQL_PARSER = new DateParser() {
		private String split = " ";

		@Override
		public Date parseDate(String dateString) {
			String[] splitDateString = dateString.trim().split(split);
			Date day = Date.valueOf(splitDateString[0]);
			// Date day = Date.valueOf(splitDateString[0]);
			int indexOfTimeMillis = splitDateString[1].indexOf('.');
			Date date = null;
			if (indexOfTimeMillis > 0) {
				Time time = Time.valueOf(splitDateString[1].substring(0,
						indexOfTimeMillis));
				date = new Date(day.getTime()
						+ time.getTime()
						+ Integer.valueOf((splitDateString[1]
								.substring(indexOfTimeMillis + 1))));
			} else {
				Time time = Time.valueOf(splitDateString[1]);
				date = new Date(day.getTime() + time.getTime());
			}
			return date;
		}
	};
	static final DateParser JAZZ_PARSER = new DateParser() {
		private String split = "T";

		@Override
		public Date parseDate(String dateString) {
			dateString = dateString.substring(0, dateString.length() - 1);

			String[] splitDateString = dateString.trim().split(split);
			Date day = Date.valueOf(splitDateString[0]);
			// Date day = Date.valueOf(splitDateString[0]);
			int indexOfTimeMillis = splitDateString[1].indexOf('.');
			Date date = null;
			if (indexOfTimeMillis > 0) {
				Time time = Time.valueOf(splitDateString[1].substring(0,
						indexOfTimeMillis));
				date = new Date(day.getTime()
						+ time.getTime()
						+ Integer.valueOf((splitDateString[1]
								.substring(indexOfTimeMillis + 1))));
			} else {
				Time time = Time.valueOf(splitDateString[1]);
				date = new Date(day.getTime() + time.getTime());
			}
			return date;
		}
	};
	static final DateParser JIRA_PARSER = new DateParser() {

		private String[] MONTHS = new String[] { "Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		@Override
		public Date parseDate(String str) {
			String[] splitDateString = str.trim().split(" ");
			// The first token contains the weekday. We ignore that.
			// The second token contains the day.
			// int day = Integer.parseInt(splitDateString[1]);
			int month = -1;
			for (int i = 0; i < MONTHS.length; i++) {
				if (MONTHS[i].equals(splitDateString[2]))
					month = i;
			}
			// int year = Integer.parseInt(splitDateString[3]);

			String monthString = "";
			if (month < 9)
				monthString = 0 + String.valueOf(month + 1);
			else
				monthString = String.valueOf(month + 1);

			String dayString = "";
			if (splitDateString[1].length() == 1) {
				dayString = "0" + splitDateString[1];
			} else
				dayString = splitDateString[1];

			String dateString = splitDateString[3] + "-" + monthString + "-"
					+ dayString;
			// System.out.println(dateString);

			Date ret = Date.valueOf(dateString);

			Time t = Time.valueOf(splitDateString[4]);
			// System.out.println(ret);
			return new Date(ret.getTime() + t.getTime());
		}

	};

	static final DateParser[] ALL_PARSERS = new DateParser[] { PSQL_PARSER,
			JAZZ_PARSER, JIRA_PARSER };

	private DateParser() {

	}

	public static DateParser getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DateParser() {

				@Override
				public Date parseDate(String str) {
					for (DateParser dp : ALL_PARSERS) {
						try {
							return dp.parseDate(str);
						} catch (Throwable t) {

						}

					}
					System.out.println("Could not parse '" + str + "'");
					return null;
				}
			};
		return INSTANCE;
	}

	public abstract Date parseDate(String str);
}
