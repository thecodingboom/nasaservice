package api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface RoverService {
    @GET("/mars-photos/api/v1/rovers/curiosity/photos")
    Call<Photos> create(
            @Query("earth_date") String earthDate,
            @Query("api_key") String apiKey
    );
}
