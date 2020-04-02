package github.abnvanand.washeteria.network;

import github.abnvanand.washeteria.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    private static Retrofit instance;

    private RetrofitSingleton() {
    }

    public static Retrofit getRetrofitInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return instance;
    }
}
