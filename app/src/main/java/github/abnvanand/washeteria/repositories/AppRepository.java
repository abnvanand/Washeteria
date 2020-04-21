package github.abnvanand.washeteria.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.Resource;
import github.abnvanand.washeteria.models.pojo.Status;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppRepository {
    private static AppRepository instance;// TODO: Make volatile maybe?
    //    public MutableLiveData<List<Machine>> machines;
    private MutableLiveData<List<Machine>> machineListObservable = new MutableLiveData<>();
    private MutableLiveData<Resource<List<Location>>> locationListObservable = new MutableLiveData<Resource<List<Location>>>();
    private MutableLiveData<List<Event>> eventsByLocationObservable = new MutableLiveData<>();
    private MutableLiveData<List<Event>> eventsByMachineObservable = new MutableLiveData<>();

    private static final String REFRESH_EVENTS_BY_LOCATION_OBSERVERS = "REFRESH_EVENTS_BY_LOC";
    private static final String REFRESH_EVENTS_BY_MACHINE_OBSERVERS = "REFRESH_EVENTS_BY_MACHINE";


    private AppDatabase mDb;
    private WebService webService;

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
        webService = RetrofitSingleton
                .getRetrofitInstance()
                .create(WebService.class);
    }


    public void fetchMachinesByLocation(String locationId) {
        loadMachinesByLocationIdFromDb(locationId);
        getMachinesByLocationidFromWeb(locationId);
    }

    private void getMachinesByLocationidFromWeb(String locationId) {
        // Call REST API to get most recent list of machines of selected location
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
                        showError(t.getLocalizedMessage());
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

    public MutableLiveData<Resource<List<Location>>> getLocationListObservable() {
        return locationListObservable;
    }

    public void fetchLocations() {
        loadLocationsFromDb(Status.LOADING);
        fetchLocationsFromWeb();
    }

    private void fetchLocationsFromWeb() {
        // Set observable status to loading
        locationListObservable.postValue(new Resource<List<Location>>().loading(null));

        webService.getLocations()
                .enqueue(new Callback<List<Location>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Location>> call,
                                           @NotNull Response<List<Location>> response) {
                        if (!response.isSuccessful()) {
                            showError(response.message());
                            locationListObservable.postValue(
                                    new Resource<List<Location>>()
                                            .error(response.message(), null));
                        } else if (response.body() != null) {
                            addLocationsToDb(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Location>> call,
                                          @NotNull Throwable t) {
                        showError(t.getLocalizedMessage());
                        locationListObservable.postValue(
                                new Resource<List<Location>>()
                                        .error(t.getLocalizedMessage(), null));
                    }
                });
    }

    private void showError(String message) {
        Timber.e(message);
    }

    private void addLocationsToDb(List<Location> body) {
        executor.execute(() -> {
            mDb.locationDao().insertAll(body);
            loadLocationsFromDb(Status.SUCCESS);
        });
    }

    private void loadLocationsFromDb(Status status) {
        executor.execute(() -> {
            List<Location> queryResults = mDb.locationDao().getAll();
            if (status == Status.LOADING)
                locationListObservable.postValue(new Resource<List<Location>>().loading(queryResults));
            else
                locationListObservable.postValue(new Resource<List<Location>>().success(queryResults));

        });
    }

    // region events

    // region events by location id
    public void fetchEventsByLocation(String locationId) {
        loadEventsByLocationFromDb(locationId);
        getEventsFromWeb(locationId);
    }

    private void getEventsFromWeb(String locationId) {
        // Fetches event based on modifiedAfter timestamp
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // TODO: Try to get lastModifiedAt from local db so that we fetch only newer events
                Long lastModifiedAt = null;
//                long lastModifiedAt = mDb.eventDao().getLastModifiedAt();
                webService.getEvents()
                        .enqueue(new Callback<List<Event>>() {
                            @Override
                            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                                if (!response.isSuccessful()) {
                                    showError(response.message());
                                    return;
                                }

                                addEventsToDb(response.body(),
                                        REFRESH_EVENTS_BY_LOCATION_OBSERVERS,
                                        locationId);
                            }

                            @Override
                            public void onFailure(Call<List<Event>> call, Throwable t) {
                                showError(t.getLocalizedMessage());
                            }
                        });
            }
        });
    }

   /* private void getEventsByLocationFromWeb(String locationId) {
        // FIXME: call getEventsByLocation()
        webService.getEvents(null)
                .enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                        if (!response.isSuccessful()) {
                            showError(response.message());
                            return;
                        }

                        if (response.body() != null) {
                            addEventsToDb(
                                    response.body(),
                                    REFRESH_EVENTS_BY_LOCATION_OBSERVERS,
                                    locationId);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable t) {
                        showError(t.getLocalizedMessage());
                    }
                });
    }*/

    private void loadEventsByLocationFromDb(String locationId) {
        executor.execute(() -> {
            List<Event> queryResults = mDb.eventDao().getAllByLocationId(locationId);
            eventsByLocationObservable.postValue(queryResults);
        });
    }

    public MutableLiveData<List<Event>> getEventsByLocationObservable() {
        return eventsByLocationObservable;
    }

    public MutableLiveData<List<Event>> getEventsByMachineObservable() {
        return eventsByMachineObservable;
    }

    // endregion events by location id


    // region events by machine id

    public void fetchEventsByMachine(String machineId) {
        loadEventsByMachineFromDb(machineId);
        getEventsByMachineFromWeb(machineId);
    }

    private void getEventsByMachineFromWeb(String machineId) {
        // Fetches event based on modifiedAfter timestamp
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // TODO: Try to get lastModifiedAt from local db so that we fetch only newer events
                Long lastModifiedAt = null;
//                long lastModifiedAt = mDb.eventDao().getLastModifiedAt();
                webService.getEvents()
                        .enqueue(new Callback<List<Event>>() {
                            @Override
                            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                                if (!response.isSuccessful()) {
                                    showError(response.message());
                                    return;
                                }

                                addEventsToDb(response.body(),
                                        REFRESH_EVENTS_BY_MACHINE_OBSERVERS,
                                        machineId);
                            }

                            @Override
                            public void onFailure(Call<List<Event>> call, Throwable t) {
                                showError(t.getLocalizedMessage());
                            }
                        });
            }
        });
    }

    private void loadEventsByMachineFromDb(String machineId) {
        executor.execute(() -> {
            List<Event> queryResults = mDb.eventDao().getNonCancelledByMachineId(machineId);
            eventsByMachineObservable.postValue(queryResults);
        });
    }

    // endregion events by machine id


    private void addEventsToDb(List<Event> body,
                               @Nullable String REFRESH_TYPE,
                               @Nullable String REFRESH_KEY) {
        executor.execute(() -> {
            mDb.eventDao().insertAll(body);

            if (REFRESH_EVENTS_BY_LOCATION_OBSERVERS.equals(REFRESH_TYPE)) {
                loadEventsByLocationFromDb(REFRESH_KEY);
            } else if (REFRESH_EVENTS_BY_MACHINE_OBSERVERS.equals(REFRESH_TYPE)) {
                loadEventsByMachineFromDb(REFRESH_KEY);
            }
        });
    }

    // endregion events

}
