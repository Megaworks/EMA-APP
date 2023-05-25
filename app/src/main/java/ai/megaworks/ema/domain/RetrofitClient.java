package ai.megaworks.ema.domain;

import com.google.gson.Gson;

import ai.megaworks.ema.Global;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient INSTANCE = null;

    private static IEmaService iEmaService;

    private RetrofitClient() {
        //로그를 보기 위한 Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        //retrofit 설정
        Gson gson = new Gson().newBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client) //로그 기능 추가
                .baseUrl(Global.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iEmaService = retrofit.create(IEmaService.class);
    }

    public static RetrofitClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitClient();
        }
        return INSTANCE;
    }

    public static IEmaService getRetrofitInterface() {
        return iEmaService;
    }
}
