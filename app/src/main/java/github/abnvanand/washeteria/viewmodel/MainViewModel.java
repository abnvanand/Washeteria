package github.abnvanand.washeteria.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import github.abnvanand.washeteria.data.AppRepository;
import github.abnvanand.washeteria.data.model.Machine;

public class MainViewModel extends AndroidViewModel {

    public LiveData<List<Machine>> machines;
    private AppRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());
        machines = mRepository.machines;
    }

    public LiveData<List<Machine>> getMachinesByLocationId(String locationId) {
        mRepository.getMachinesByLocationId(locationId);
        return this.machines;
    }
}
