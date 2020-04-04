package github.abnvanand.washeteria.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import github.abnvanand.washeteria.data.AppRepository;
import github.abnvanand.washeteria.data.model.Machine;

public class MainViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    //mediator lists because they transfer the changes of the livedata lists of the repository
    private MediatorLiveData<List<Machine>> machineListObservable = new MediatorLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());


        //subscribe to Livedata of the repository and pass it along to the view (activity - fragment etc)
        machineListObservable.addSource(mRepository.getMachineListObservable(),
                machines -> machineListObservable.setValue(machines));
    }

    public void getData(String locationId) {
        mRepository.fetchData(locationId);
    }

    public LiveData<List<Machine>> getMachinesListObservable() {
        return machineListObservable;
    }
}
