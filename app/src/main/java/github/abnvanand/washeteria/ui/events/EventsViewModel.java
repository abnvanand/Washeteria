package github.abnvanand.washeteria.ui.events;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.repositories.AppRepository;

public class EventsViewModel extends AndroidViewModel {
    private AppRepository mRepository;
    private MediatorLiveData<List<Event>> eventsByLocationObservable = new MediatorLiveData<>();

    public EventsViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());

        // Register an observer
        eventsByLocationObservable.addSource(mRepository.getEventsByLocationObservable(),
                events -> eventsByLocationObservable.setValue(events));

    }

    // Used to trigger initial loading of events
    // TODO: Create a EventsViewModelFactory which would
    //  allow passing location Id in constructor of EventsViewModel
    public void getData(String locationId) {
        mRepository.fetchEventsByLocation(locationId);
    }

    public MediatorLiveData<List<Event>> getEventsByLocationObservable() {
        return eventsByLocationObservable;
    }
}
