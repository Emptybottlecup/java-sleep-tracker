package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepAnalyzer;
import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepLogFileLoader;
import ru.yandex.practicum.sleeptracker.UserExceptions.EmptyListOfSleepSessions;
import ru.yandex.practicum.sleeptracker.enums.SleepCondition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Period;
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
        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий сна: %d%n",
                sleepSessions.size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наименьшая по продолжительности "
                + "сессия сна: %d%n", sleepSessions.stream().map(sleepSession -> Duration
                .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).min(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наибольшая по продолжительности "
                + "сессия сна: %d%n", sleepSessions.stream().map(sleepSession -> Duration
                .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).max(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> {
            List<Long> listOfMinutes = sleepSessions.stream().map(sleepSession -> Duration
                    .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).toList();

            return String.format("Средняя продолжительность сессии сна: %.2f%n", listOfMinutes.stream().mapToLong(
                    Long::longValue).average().getAsDouble());
        });


        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий с плохим "
                + "состоянием сна: %d%n", sleepSessions.stream().filter(sleepSession -> sleepSession
                .getSleepCondition().equals(SleepCondition.BAD)).toList().size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> {

            long days = Period.between(sleepSessions.get(0).getStartSleep().toLocalDate(), sleepSessions.get(
                    sleepSessions.size() - 1).getEndSleep().toLocalDate()).getDays();

            if (sleepSessions.get(0).getStartSleep().getHour() < 12) {
                days += 1;
            }

            long sleepNights = sleepSessions.stream().filter(sleepSession -> ((sleepSession
                    .getStartSleep().getDayOfMonth() != sleepSession.getEndSleep().getDayOfMonth()) || sleepSession
                    .getStartSleep().isBefore(sleepSession.getStartSleep().withHour(6).withMinute(0)))).count();

            return String.format("Количество бессонных ночей: %d%n", days - sleepNights);
        });

        sleepAnalyzer.addNewFunction(sleepSessions -> {

            String firstType = "Сова";
            String secondType = "Жаворонок";
            String thirdType = "Голубь";

            List<String> sleepNightsTypes = sleepSessions.stream().filter(sleepSession -> ((sleepSession
                    .getStartSleep().getDayOfMonth() != sleepSession.getEndSleep().getDayOfMonth()) || sleepSession
                    .getStartSleep().isBefore(sleepSession.getStartSleep().withHour(6).withMinute(0))))
                    .map(sleepSession -> {
                        if ((sleepSession.getStartSleep().getDayOfMonth() == sleepSession.getEndSleep().getDayOfMonth()
                                || sleepSession.getStartSleep().isAfter(sleepSession.getStartSleep().withHour(23)
                                .withMinute(0))) && sleepSession.getEndSleep().isAfter(sleepSession.getEndSleep()
                                .withHour(9).withMinute(0))) {
                            return firstType;
                        } else if (sleepSession.getStartSleep().getDayOfMonth() != sleepSession.getEndSleep()
                                .getDayOfMonth() && (sleepSession.getStartSleep().isBefore(sleepSession.getStartSleep()
                                .withHour(22).withMinute(0)) && sleepSession.getEndSleep().isBefore(sleepSession
                                .getEndSleep().withHour(7).withMinute(0)))) {
                            return secondType;
                        } else {
                            return thirdType;
                        }
                    }).toList();

            long countFirstType = sleepNightsTypes.stream().filter(type -> type.equals(firstType)).count();
            long countSecondType = sleepNightsTypes.stream().filter(type -> type.equals(secondType)).count();
            long countThirdType = sleepNightsTypes.stream().filter(type -> type.equals(thirdType)).count();

            String returnType;

            if ((countFirstType > countSecondType) && (countFirstType > countThirdType)) {
                returnType = firstType;
            } else if ((countSecondType > countFirstType) && (countSecondType > countThirdType)) {
                returnType = secondType;
            } else {
                returnType = thirdType;
            }

            return String.format("Ваш тип: %s", returnType);
        });
    }
}