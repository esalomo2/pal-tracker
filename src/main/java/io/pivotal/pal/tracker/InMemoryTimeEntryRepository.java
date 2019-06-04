package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private final Map<Long, TimeEntry > hm;
    private long counter;

    public InMemoryTimeEntryRepository() {
        hm = new HashMap<>();
        counter=1;
    }

    public TimeEntry create(TimeEntry timeEntry) {

        long id = getNextId();
        TimeEntry timeEntryNew= new TimeEntry(id, timeEntry.getProjectId(),timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());

        hm.put(timeEntryNew.getId(),timeEntryNew);//put in map

        return timeEntryNew;
    }

    private long getNextId() {

        return counter++;
    }

    @Override
    public TimeEntry find(long id) {
        return hm.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(hm.containsKey(id)){

            TimeEntry timeEntryUpdate= new TimeEntry(id, timeEntry.getProjectId(),timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());

            hm.put(id,timeEntryUpdate);//repalce the value in map
            return timeEntryUpdate;}
        else
            return null;
    }

    @Override
    public void delete(long id) {
        hm.remove(id);
    }

    @Override
        public List<TimeEntry> list() {
        List<TimeEntry> dtos = new ArrayList<TimeEntry>(hm.values());
        return dtos;

    }


}
