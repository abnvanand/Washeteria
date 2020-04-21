package github.abnvanand.washeteria.network;

import java.util.List;

import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.AssistedEventRequest;
import github.abnvanand.washeteria.models.pojo.EventCreateBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface WebService {
    @GET("auth/token")
    Call<LoggedInUser> login(@Header("userName") String username, @Header("password") String password);

    @GET("locations")
    Call<List<Location>> getLocations();

    @GET("machines/location/{locationId}")
    Call<List<Machine>> getMachines(@Path("locationId") String locationId);

    @GET("events")
    Call<List<Event>> getEvents();

    @POST("events")
    Call<Event> createEvent(@Body EventCreateBody eventToCreate);

    @PUT("events")
    Call<Event> cancelEvent(@Body Event eventToCancel);

    @GET("events/modified/{modifiedAfter}")
    Call<List<Event>> getEventsAfter(@Path("modifiedAfter") String modifiedAtfer);
//    Call<List<Event>> getEventsAfter(@Query("modifiedAfter") Long modifiedAfter);

    @POST("events/assisted")
    Call<Event> createAssistedEvent(@Body AssistedEventRequest assistedEventRequest);

}
