package ru.yandex.practicum.sleeptracker.SleepTrackerClasses;

import ru.yandex.practicum.sleeptracker.UserExceptions.EmptyListOfSleepSessions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SleepAnalyzer {
    private List<SleepSession> listOfSleepingSessions;
    private final  List<Function<List<SleepSession>, String>> listOfFunctions;

    public SleepAnalyzer(List<SleepSession> listOfSleepingSessions) {
        this.listOfSleepingSessions = listOfSleepingSessions;
        listOfFunctions = new ArrayList<>();
    }

    public SleepAnalyzer() {
        listOfFunctions = new ArrayList<>();
    }

    public void setNewListOfSleepingSessions (List<SleepSession> listOfSleepingSessions){
        this.listOfSleepingSessions = listOfSleepingSessions;
    }

    public void addNewFunction(Function<List<SleepSession>, String> newFunction) {
        listOfFunctions.add(newFunction);
    }

    public List<String> getInforamtionAboutSleepSessions() throws EmptyListOfSleepSessions {
        if(listOfSleepingSessions.isEmpty()) {
            throw new EmptyListOfSleepSessions("Лист сессий сна пуст");
        }
         return listOfFunctions.stream().map(function -> function.apply(listOfSleepingSessions))
                 .toList();
    }
}
