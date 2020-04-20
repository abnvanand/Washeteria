package github.abnvanand.washeteria.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.Resource;
import github.abnvanand.washeteria.repositories.AppRepository;

public class MainViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    //mediator lists because they transfer the changes of the livedata lists of the repository
    private MediatorLiveData<List<Machine>> machineListObservable = new MediatorLiveData<>();

    private MediatorLiveData<Resource<List<Location>>> locationListObservable = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());

        mRepository.fetchLocations();

        //subscribe to Livedata of the repository and pass it along to the view (activity - fragment etc)
        locationListObservable.addSource(mRepository.getLocationListObservable(),
                new Observer<Resource<List<Location>>>() {
                    @Override
                    public void onChanged(Resource<List<Location>> locations) {
                        locationListObservable.setValue(locations);
                    }
                });

        machineListObservable.addSource(mRepository.getMachineListObservable(),
                machines -> machineListObservable.setValue(machines));
    }

    public void refreshLocations() {
        mRepository.fetchLocations();
    }

    public void getData(String locationId) {
        mRepository.fetchMachinesByLocation(locationId);
    }

    public LiveData<List<Machine>> getMachinesListObservable() {
        return machineListObservable;
    }

    public LiveData<Resource<List<Location>>> getLocationListObservable() {
        return locationListObservable;
    }
}
