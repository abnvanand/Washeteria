package github.abnvanand.washeteria.network;

import java.lang.annotation.Annotation;

import github.abnvanand.washeteria.BuildConfig;
import github.abnvanand.washeteria.models.pojo.APIError;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    public static Converter<ResponseBody, APIError> authErrorConverter;
    public static Converter<ResponseBody, APIError> errorConverter;
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

            // Add http 401 response interceptor
            addUnauthorizedResponseInterceptor(httpClient);

            // Add logging as last interceptor
            addLogginInterceptor(httpClient);


            authInstance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            authErrorConverter = authInstance.responseBodyConverter(APIError.class, new Annotation[0]);
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

            errorConverter = instance.responseBodyConverter(APIError.class, new Annotation[0]);
        }

        return instance;
    }

    private static void addAuthInterceptor(String token,
                                           OkHttpClient.Builder httpClient) {
        httpClient.addInterceptor(new AuthInterceptor(token));
    }


    private static void addUnauthorizedResponseInterceptor(OkHttpClient.Builder httpClient) {
        UnauthorizedResponseInterceptor interceptor = new UnauthorizedResponseInterceptor();
        httpClient.addInterceptor(interceptor);
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
