import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpApi {
    public static final String API_URL = "http://httpbin.org";

    private static HttpApi instance = null;
    private Map<String, String> headers = new HashMap<String, String>();
    private HttpBinService service;

    /**
     * Generic HttpBin.org Response Container
     */
    public static class HttpBinResponse {
        // the request url
        String url;

        // the requester ip
        String origin;

        // all headers that have been sent
        Map headers;

        // url arguments
        Map args;

        // post form parameters
        Map form;

        // post body json
        Map json;
    }

    /**
     * Exemplary login data sent as JSON
     */
    public static class LoginData {
        String username;
        String password;

        public LoginData(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    /**
     * HttpBin.org service definition
     */
    public interface HttpBinService {
        @GET("/get")
        Call<HttpBinResponse> get();

        // request /get?testArg=...
        @GET("/get")
        Call<HttpBinResponse> getWithArg(
                @Query("testArg") String arg
        );

        // POST form encoded with form field params
        @FormUrlEncoded
        @POST("/post")
        Call<HttpBinResponse> postWithFormParams(
                @Field("field1") String field1
        );

        // POST form encoded with form field params
        @POST("/post")
        Call<HttpBinResponse> postWithJson(
                @Body LoginData loginData
        );
    }

    /**
     * Private constructor
     */
    private HttpApi() {
        // Http interceptor to add custom headers to every request
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(new Interceptor() {
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();

                System.out.println("Adding headers:" + headers);
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }

                return chain.proceed(builder.build());
            }
        });

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        service = retrofit.create(HttpBinService.class);
    }

    /**
     * Get the HttpApi singleton instance
     */
    public static HttpApi getInstance() {
        if(instance == null) {
            instance = new HttpApi();
        }
        return instance;
    }

    /**
     * Get the API service to execute calls with
     */
    public HttpBinService getService() {
        return service;
    }

    /**
     * Add a header which is added to every API request
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Add multiple headers
     */
    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * Remove a header
     */
    public void removeHeader(String key) {
        headers.remove(key);
    }

    /**
     * Remove all headers
     */
    public void clearHeaders() {
        headers.clear();
    }

    /**
     * Get all headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
