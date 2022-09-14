package com.atakmap.android.plugintemplate.plugin;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyAPI {
   // https://run.mocky.io/v3/b2412242-52d3-4684-af74-0634d8ff04ae

    @GET("v3/b2412242-52d3-4684-af74-0634d8ff04ae")
    Call<DataModel>getData();

}
