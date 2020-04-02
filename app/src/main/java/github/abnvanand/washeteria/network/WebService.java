package github.abnvanand.washeteria.network;

import java.util.List;

import github.abnvanand.washeteria.data.model.Location;
import github.abnvanand.washeteria.data.model.Machine;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WebService {
    @GET("/locations")
    Call<List<Location>> getLocations();

    @GET("/location/{locationId}/machines")
    Call<List<Machine>> getMachines(@Path("locationId") String locationId);
}
