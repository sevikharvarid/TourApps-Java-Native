package com.example.exploreasy;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("recommendation")
    Call<ResponseBody> postData(@Body RequestBodyModel requestBody);

}
