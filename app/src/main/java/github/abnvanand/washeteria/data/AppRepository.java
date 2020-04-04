package github.abnvanand.washeteria.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.data.model.Location;
import github.abnvanand.washeteria.data.model.Machine;
import github.abnvanand.washeteria.database.AppDatabase;
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
    private MutableLiveData<List<Location>> locations = new MutableLiveData<>();


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
        Timber.d("getMachinesByLocationId locationId: %s", locationId);

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
                        if (response.isSuccessful()) {
                            addMachinesToDb(response.body(), locationId);
                        } else {
                            // TODO: Handle error codes
                            Timber.e("Error in getMachines(locationId) %s/%s",
                                    response.message(),
                                    response.code());
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
        // Update local db
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (body == null)
                    return;

                Timber.d("Remote results: %s", body.toString());
                Timber.d("Inserting to db: %s", body);
                mDb.machineDao().insertAll(body);
                loadMachinesByLocationIdFromDb(locationId);
            }
        });
    }

    private void loadMachinesByLocationIdFromDb(String locationId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Get cached results from local db
                List<Machine> queryResults = mDb.machineDao().getAllByLocationId(locationId);
                // TODO: setValue vs postValue??
                machineListObservable.postValue(queryResults);
                Timber.d("Local results: %s", queryResults);
            }
        });
    }

    public MutableLiveData<List<Machine>> getMachineListObservable() {
        return machineListObservable;
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }

    public void fetchLocations() {
        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);
        Call<List<Location>> call = webService.getLocations();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(@NotNull Call<List<Location>> call,
                                   @NotNull Response<List<Location>> response) {
                if (response.isSuccessful()) {
                    List<Location> body = response.body();
                    locations.postValue(body);
                    Timber.d("Response code: %s body:%s", response.code(), body);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Location>> call,
                                  @NotNull Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }
}
