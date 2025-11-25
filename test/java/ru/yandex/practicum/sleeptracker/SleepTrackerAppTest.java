package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepAnalyzer;
import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepLogFileLoader;
import ru.yandex.practicum.sleeptracker.SleepTrackerClasses.SleepSession;
import ru.yandex.practicum.sleeptracker.UserExceptions.EmptyListOfSleepSessions;
import ru.yandex.practicum.sleeptracker.enums.SleepCondition;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Period;
import java.util.List;

public class SleepTrackerAppTest {
    private static List<SleepSession> mainSleepLog;
    private static List<SleepSession> emptyLog;
    private static List<SleepSession> logWithoutSleep;
    private static List<SleepSession> logWithOwlType;
    private static List<SleepSession> logWithLarkType;
    private static SleepAnalyzer sleepAnalyzer;

    @BeforeAll
    public static void setup() {
        try {
            sleepAnalyzer  = new SleepAnalyzer();
            addFunctions(sleepAnalyzer);
            mainSleepLog = SleepLogFileLoader.createListOfSleepSessions(Paths.get("src\\main\\resources\\" +
                    "sleep_log.txt"));
            emptyLog = SleepLogFileLoader.createListOfSleepSessions(Paths.get("test\\" +
                    "testsLog\\test_empty_log.txt"));
            logWithoutSleep = SleepLogFileLoader.createListOfSleepSessions(Paths.get("test\\" +
                    "testsLog\\test_without_sleep_night_log.txt"));
            logWithOwlType = SleepLogFileLoader.createListOfSleepSessions(Paths.get("test\\" +
                    "testsLog\\test_owl_type_log.txt"));
            logWithLarkType = SleepLogFileLoader.createListOfSleepSessions(Paths.get("test\\" +
                    "testsLog\\test_lark_type_log.txt"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addFunctions(SleepAnalyzer sleepAnalyzer) {
        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий сна: %d",
                sleepSessions.size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наименьшая по продолжительности "
                + "сессия сна: %d", sleepSessions.stream().map(sleepSession -> Duration
                        .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).min(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Наибольшая по продолжительности "
                + "сессия сна: %d", sleepSessions.stream().map(sleepSession -> Duration
                        .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).max(Long::compare)
                .get()));

        sleepAnalyzer.addNewFunction(sleepSessions -> {
            List<Long> listOfMinutes = sleepSessions.stream().map(sleepSession -> Duration
                    .between(sleepSession.getStartSleep(), sleepSession.getEndSleep()).toMinutes()).toList();

            return String.format("Средняя продолжительность сессии сна: %.2f", listOfMinutes.stream().mapToLong(
                    Long::longValue).average().getAsDouble());
        });


        sleepAnalyzer.addNewFunction(sleepSessions -> String.format("Количество сессий с плохим "
                + "состоянием сна: %d", sleepSessions.stream().filter(sleepSession -> sleepSession
                .getSleepCondition().equals(SleepCondition.BAD)).toList().size()));

        sleepAnalyzer.addNewFunction(sleepSessions -> {

            long days = Period.between(sleepSessions.get(0).getStartSleep().toLocalDate(), sleepSessions
                    .get(sleepSessions.size() - 1).getEndSleep().toLocalDate()).getDays();

            if (sleepSessions.get(0).getStartSleep().getHour() < 12) {
                days += 1;
            }

            long sleepNights = sleepSessions.stream().filter(sleepSession -> ((sleepSession
                    .getStartSleep().getDayOfMonth() != sleepSession.getEndSleep().getDayOfMonth()) || sleepSession
                    .getStartSleep().isBefore(sleepSession.getStartSleep().withHour(6).withMinute(0)))).count();

            return String.format("Количество бессонных ночей: %d", days - sleepNights);
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

    @Test
    public void testCountSleepSessionsFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(0);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(0);

            Assertions.assertEquals("Количество сессий сна: 12", result1);
            Assertions.assertEquals("Количество сессий сна: 9", result2);

            sleepAnalyzer.setNewListOfSleepingSessions(emptyLog);
            sleepAnalyzer.getInforamtionAboutSleepSessions();

        } catch (EmptyListOfSleepSessions e) {
            Assertions.assertEquals("Лист сессий сна пуст", e.getMessage());
        }
    }

    @Test
    public void testMinDurationSleepSessionFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(1);


            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(1);

            Assertions.assertEquals("Наименьшая по продолжительности сессия сна: 45", result1);
            Assertions.assertEquals("Наименьшая по продолжительности сессия сна: 5", result2);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testMaxDurationSleepSessionFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(2);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(2);

            Assertions.assertEquals("Наибольшая по продолжительности сессия сна: 500", result1);
            Assertions.assertEquals("Наибольшая по продолжительности сессия сна: 70", result2);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testFindAverageDurationSleepSessionFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(3);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(3);

            Assertions.assertEquals("Средняя продолжительность сессии сна: 340.83", result1);
            Assertions.assertEquals("Средняя продолжительность сессии сна: 31.33", result2);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCountBadSleepSessionsFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(4);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(4);

            Assertions.assertEquals("Количество сессий с плохим состоянием сна: 2", result1);
            Assertions.assertEquals("Количество сессий с плохим состоянием сна: 9", result2);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCountNoSleepAtNightSessionsFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(5);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(5);

            Assertions.assertEquals("Количество бессонных ночей: 2", result1);
            Assertions.assertEquals("Количество бессонных ночей: 8", result2);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testFindSleepTypeFunction() {
        try {
            sleepAnalyzer.setNewListOfSleepingSessions(mainSleepLog);
            String result1 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(6);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithoutSleep);
            String result2 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(6);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithOwlType);
            String result3 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(6);

            sleepAnalyzer.setNewListOfSleepingSessions(logWithLarkType);
            String result4 = sleepAnalyzer.getInforamtionAboutSleepSessions().get(6);

            Assertions.assertEquals("Ваш тип: Голубь", result1);
            Assertions.assertEquals("Ваш тип: Голубь", result2);
            Assertions.assertEquals("Ваш тип: Сова", result3);
            Assertions.assertEquals("Ваш тип: Жаворонок", result4);
        } catch (EmptyListOfSleepSessions e) {
            System.out.println(e.getMessage());
        }
    }

}