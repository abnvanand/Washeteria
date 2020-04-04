package github.abnvanand.washeteria.data;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.data.model.Machine;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppRepository {
    private static AppRepository instance;
    public MutableLiveData<List<Machine>> machines;

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
        machines = new MutableLiveData<>();
    }


    public void getMachinesByLocationId(String locationId) {
        Timber.d("getMachinesByLocationId locationId: %s", locationId);

        // Get cached results from local db
        List<Machine> queryResults = mDb.machineDao().getAllByLocationId(locationId).getValue();
        machines.postValue(queryResults);
        Timber.d("Local results: %s", queryResults);

        // Call REST API to get most recent list of machines of selected location
        refreshMachineList(locationId);
    }

    private void refreshMachineList(final String locationId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                WebService webService = RetrofitSingleton.getRetrofitInstance()
                        .create(WebService.class);


                Call<List<Machine>> call = webService.getMachines(locationId);
                call.enqueue(new Callback<List<Machine>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Machine>> call,
                                           @NotNull final Response<List<Machine>> response) {


                        // Update local db
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                List<Machine> body = response.body();
                                if (body == null)
                                    return;

                                Timber.d("Remote results: %s", body.toString());
                                Timber.d("Inserting to db: %s", body);
                                machines.postValue(body);
                                mDb.machineDao().insertAll(body);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Machine>> call,
                                          @NotNull Throwable t) {
                        Timber.d(t.getLocalizedMessage());
                    }
                });
            }
        });
    }
}
