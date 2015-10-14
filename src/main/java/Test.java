import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
//        HttpApiSimple.testApiRequest();;
        testInstance();
    }

    private static void testInstance() {
        // This can be used in any Activity, etc.
        HttpApi api = HttpApi.getInstance();

        // Add headers to be added to every api request
        api.addHeader("Authorization", "MyToken123");

        // Prepare the HTTP request & asynchronously execute HTTP request
        Call<HttpApi.HttpBinResponse> call = api.getService().postWithJson(new HttpApi.LoginData("username", "secret"));
        call.enqueue(new Callback<HttpApi.HttpBinResponse>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            public void onResponse(Response<HttpApi.HttpBinResponse> response, Retrofit retrofit) {
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
                HttpApi.HttpBinResponse decodedResponse = response.body();
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
            public void onFailure(Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());
            }
        });
    }
}
