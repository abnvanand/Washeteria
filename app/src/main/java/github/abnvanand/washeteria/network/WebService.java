package github.abnvanand.washeteria.network;

import java.util.List;

import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.LoginRequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {
    @POST("/getToken")
    Call<LoggedInUser> login(@Body LoginRequestBody loginRequestBody);

    @GET("/locations")
    Call<List<Location>> getLocations();

    @GET("/locations/{locationId}/machines")
    Call<List<Machine>> getMachines(@Path("locationId") String locationId);

    @GET("/events")
    Call<List<Event>> getEvents(@Query("modifiedAfter") Long modifiedAfter);

}
