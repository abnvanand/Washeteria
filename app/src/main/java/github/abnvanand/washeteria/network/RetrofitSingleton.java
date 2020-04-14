package github.abnvanand.washeteria.network;

import github.abnvanand.washeteria.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    private static Retrofit instance;
    private static Retrofit authInstance;

    private RetrofitSingleton() {
    }

    // FIXME: merge authorizedInstance and instance into 1
    public static Retrofit getAuthorizedInstance(String token) {
        if (authInstance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Add auth interceptor if token exists
            addAuthInterceptor(token, httpClient);

            // Add logging as last interceptor
            addLogginInterceptor(httpClient);


            authInstance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return authInstance;
    }

    public static Retrofit getRetrofitInstance() {
        if (instance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // Add logging as last interceptor
            addLogginInterceptor(httpClient);


            instance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return instance;
    }

    private static void addAuthInterceptor(String token,
                                           OkHttpClient.Builder httpClient) {
        httpClient.addInterceptor(new AuthInterceptor(token));
    }

    private static void addLogginInterceptor(OkHttpClient.Builder httpClient) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // Set desired log level NONE, BASIC, HEADERS or body
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // add your other interceptors
        httpClient.addInterceptor(loggingInterceptor);
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
        authInstance = null;
    }
}
