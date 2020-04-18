package github.abnvanand.washeteria.network;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class UnauthorizedResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            Timber.e("Received 401 HTTP_UNAUTHORIZED");
            // TODO: clear sharedprefs and AuthInstance
        }
        return response;
    }
}
