package github.abnvanand.washeteria.network;

import java.util.List;

import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.Machine;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebService {
    @POST("/login")
    Call<LoggedInUser> login();

    @GET("/locations")
    Call<List<Location>> getLocations();

    @GET("/location/{locationId}/machines")
    Call<List<Machine>> getMachines(@Path("locationId") String locationId);
}
