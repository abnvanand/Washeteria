package github.abnvanand.washeteria.network;

import github.abnvanand.washeteria.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    private static Retrofit instance;

    private RetrofitSingleton() {
    }

    public static Retrofit getRetrofitInstance() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Set desired log level NONE, BASIC, HEADERS or body
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // add your other interceptors
            // add logging as last interceptor
            httpClient.addInterceptor(logging);


            instance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return instance;
    }

    // To be called whenever the user login data changes
    // i.e. whenever login or logout request is made
    // Why on login
    //       reset so that future requests would obtain a new instance
    //       which would contain an auth interceptor
    // Why on logout
    //       reset so that future requests would obtain a new instance
    //       which would not contain an auth interceptor
    public static void reset() {
        instance = null;
    }
}
