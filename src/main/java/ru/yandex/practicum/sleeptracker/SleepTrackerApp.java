package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepAnalyzer;
import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepLogFileLoader;
import ru.yandex.practicum.sleeptracker.UserExceptions.EmptyListOfSleepSessions;
import ru.yandex.practicum.sleeptracker.enums.SleepCondition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class SleepTrackerApp {

    public static void main(String[] args) {
        Path pathToSleepLog = Paths.get("src\\main\\resources\\sleep_log.txt");
        try {
            SleepAnalyzer sleepAnalyzer = new SleepAnalyzer(SleepLogFileLoader.createListOfSleepSessions(pathToSleepLog));

            addFunctions(sleepAnalyzer);

            System.out.println(sleepAnalyzer.getInforamtionAboutSleepSessions());

        } catch (IOException | EmptyListOfSleepSessions e) {
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println(e.getStackTrace());
            }
        }
    }

    public static void addFunctions(SleepAnalyzer sleepAnalyzer) {
        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий сна: %d%n", sleepSessions.size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наименьшая по продолжительности "
                + "сессия сна: %d%n", sleepSessions.stream().map(sleepSession -> Duration.between(
                        sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).min(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наибольшая по продолжительности "
                + "сессия сна: %d%n", sleepSessions.stream().map(sleepSession -> Duration.between(
                        sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).max(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> {
            List<Long> listOfMinutes = sleepSessions.stream().map(sleepSession -> Duration.between(
                    sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).toList();

            return String.format("Средняя продолжительность сессии сна: %.2f%n", listOfMinutes.stream().mapToLong(
                    Long::longValue).average().getAsDouble());
        });


        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий с плохим " +
                "состоянием сна: %d%n", sleepSessions.stream().filter(sleepSession -> sleepSession.
                getSleepCondition().equals(SleepCondition.BAD)).toList().size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий с плохим " +
                "состоянием сна: %d%n", sleepSessions.stream().filter(sleepSession -> sleepSession.
                getSleepCondition().equals(SleepCondition.BAD)).toList().size()));
    }
}