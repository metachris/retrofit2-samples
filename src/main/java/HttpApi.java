import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.*;

import java.io.IOException;
import java.util.Map;

public class HttpApi {

    public static final String API_URL = "http://httpbin.org";

    /**
     * Generic HttpBin.org Response Container
     */
    static class HttpBinResponse {
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
    static class LoginData {
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

    public static void testApiRequest() {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        HttpBinService service = retrofit.create(HttpBinService.class);

        // Prepare the HTTP request
        Call<HttpBinResponse> call = service.postWithJson(new LoginData("username", "secret"));

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<HttpBinResponse>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Response<HttpBinResponse> response, Retrofit retrofit) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccess()) {
                    // print response body if unsuccessful
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                // if parsing the JSON body failed, `response.body()` returns null
                HttpBinResponse decodedResponse = response.body();
                if (decodedResponse == null) return;

                // at this point the JSON body has been successfully parsed
                System.out.println("Response (contains request infos):");
                System.out.println("- url:         " + decodedResponse.url);
                System.out.println("- ip:          " + decodedResponse.origin);
                System.out.println("- headers:     " + decodedResponse.headers);
                System.out.println("- args:        " + decodedResponse.args);
                System.out.println("- form params: " + decodedResponse.form);
                System.out.println("- json params: " + decodedResponse.json);
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());
            }
        });
    }
}
