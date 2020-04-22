package github.abnvanand.washeteria.utils;

import java.io.IOException;

import github.abnvanand.washeteria.models.pojo.APIError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static APIError parseError(Response<?> response, Converter<ResponseBody, APIError> converter) {
        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError(CustomCodes.PARSE_EXCEPTION, response.code(), response.message());
        }

        return error;
    }

    public static class CustomCodes {

        public static final String PARSE_EXCEPTION = "PARSE_EXCEPTION";
        public static final String NETWORK_ERROR = "NETWORK_ERROR";
    }
}
