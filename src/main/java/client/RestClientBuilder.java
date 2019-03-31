package client;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RestClientBuilder {
    public static <S> S createClient(RestPartnerServiceConfig config, Class<S> clientClass) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(config.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .connectTimeout(config.getConnectionTimeoutSeconds(), TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(clientClass);
        }
}


