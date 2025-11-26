package ru.yandex.practicum.sleeptracker.UserExceptions;

public class EmptyListOfSleepSessions extends Exception {
    public EmptyListOfSleepSessions(String message) {
        super(message);
    }
}
