<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
	<Appenders>
		<Console name="Console">
			<PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n" />
		</Console>
		<File name="nba-import" fileName="${sys:nba.import.log.file}">
			<PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n" />
		</File>
	</Appenders>
	<Loggers>
		<Logger name="nl.naturalis" level="INFO" additivity="false">
			<AppenderRef ref="nba-import" />
		</Logger>
		<Root level="ERROR">
			<AppenderRef ref="Console" />
		</Root>
	</Loggers>
</Configuration>