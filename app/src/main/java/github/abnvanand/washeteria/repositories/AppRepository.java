package github.abnvanand.washeteria.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppRepository {
    private static AppRepository instance;
    //    public MutableLiveData<List<Machine>> machines;
    private MutableLiveData<List<Machine>> machineListObservable = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationListObservable = new MutableLiveData<>();


    private AppDatabase mDb;

    /**
     * All room db operations must be executed in a background thread
     **/
    // TODO: make sure you are not executing
    //  multiple db operations at the same time
    //  use the same executor for all db operations
    //  to ensure that they will be queued one after another
    private Executor executor = Executors.newSingleThreadExecutor();


    public static AppRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppRepository(context);
        }
        return instance;
    }

    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
    }


    public void fetchMachinesByLocation(String locationId) {
        loadMachinesByLocationIdFromDb(locationId);
        getMachinesByLocationidFromWeb(locationId);
    }

    private void getMachinesByLocationidFromWeb(String locationId) {
        // Call REST API to get most recent list of machines of selected location
        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);

        webService.getMachines(locationId)
                .enqueue(new Callback<List<Machine>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Machine>> call,
                                           @NotNull Response<List<Machine>> response) {
                        if (!response.isSuccessful()) {
                            showError(response.message());
                            return;
                        }

                        if (response.body() != null) {
                            addMachinesToDb(response.body(), locationId);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Machine>> call,
                                          @NotNull Throwable t) {
                        Timber.d(t.getLocalizedMessage());
                    }
                });

    }

    private void addMachinesToDb(List<Machine> body, String locationId) {
        executor.execute(() -> {
            mDb.machineDao().insertAll(body);
            loadMachinesByLocationIdFromDb(locationId);
        });
    }

    private void loadMachinesByLocationIdFromDb(String locationId) {
        executor.execute(() -> {
            List<Machine> queryResults = mDb.machineDao().getAllByLocationId(locationId);
            machineListObservable.postValue(queryResults);
        });
    }

    public MutableLiveData<List<Machine>> getMachineListObservable() {
        return machineListObservable;
    }

    public MutableLiveData<List<Location>> getLocationListObservable() {
        return locationListObservable;
    }

    public void fetchLocations() {
        loadLocationsFromDb();
        fetchLocationsFromWeb();
    }

    private void fetchLocationsFromWeb() {
        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);
        webService.getLocations()
                .enqueue(new Callback<List<Location>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Location>> call,
                                           @NotNull Response<List<Location>> response) {
                        if (!response.isSuccessful()) {
                            showError(response.message());
                            return;
                        }

                        if (response.body() != null) {
                            addLocationsToDb(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Location>> call,
                                          @NotNull Throwable t) {
                        Timber.e(t.getLocalizedMessage());
                    }
                });
    }

    private void showError(String message) {
        Timber.e(message);
    }

    private void addLocationsToDb(List<Location> body) {
        executor.execute(() -> {
            mDb.locationDao().insertAll(body);
            loadLocationsFromDb();
        });
    }

    private void loadLocationsFromDb() {
        executor.execute(() -> {
            List<Location> queryResults = mDb.locationDao().getAll();
            locationListObservable.postValue(queryResults);
        });
    }
}
