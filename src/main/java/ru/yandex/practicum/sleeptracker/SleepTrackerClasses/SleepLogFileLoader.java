package ru.yandex.practicum.sleeptracker.SleepTrackerClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class SleepLogFileLoader {

    public static List<SleepSession> createListOfSleepSessions(Path pathToLogFile) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(pathToLogFile.toFile(), StandardCharsets.UTF_8))) {

            List<String> listOfSleepingStringSessions = new ArrayList<>(br.lines().toList());

            return listOfSleepingStringSessions.stream().map(SleepSession::new).toList();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
