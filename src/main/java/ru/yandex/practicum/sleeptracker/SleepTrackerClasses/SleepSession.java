package ru.yandex.practicum.sleeptracker.SleepTrackerClasses;

import ru.yandex.practicum.sleeptracker.enums.SleepCondition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SleepSession {
    private SleepCondition sleepCondition;
    private LocalDateTime startSleep;
    private LocalDateTime endSleep;

    public SleepSession(String informationAboutSleep) {
        refactorStringToValues(informationAboutSleep);
    }

    private void refactorStringToValues(String informationAboutSleep) {
        String[] splitInformation = informationAboutSleep.split(";");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        this.startSleep = LocalDateTime.parse(splitInformation[0], formatter);
        this.endSleep = LocalDateTime.parse(splitInformation[1], formatter);
        ;
        this.sleepCondition = SleepCondition.valueOf(splitInformation[2]);
    }


    public LocalDateTime getEndSleep() {
        return endSleep;
    }

    public SleepCondition getSleepCondition() {
        return sleepCondition;
    }

    public LocalDateTime getStartSleep() {
        return startSleep;
    }
}
