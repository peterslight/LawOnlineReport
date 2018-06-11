package com.peterstev.lawonlinereportnigeria.interfaces;

import com.peterstev.lawonlinereportnigeria.models.home.wp_json_api.LawModel;
import com.peterstev.lawonlinereportnigeria.models.login.LoginModel;
import com.peterstev.lawonlinereportnigeria.models.update.UpdateModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Peterstev on 4/14/2018.
 * for LawOnlineReport
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/wp-json/custom_login/login")
    Call<LoginModel> loginUser(@Field("username") String username, @Field("password") String password);

    @GET("/wp-json/wp/v2/pages/4?fields=id,title,slug,content")
    Call<LawModel> getLawHome();

    @GET()
    Call<String> getGroupPosts(@Url String url);

    @GET("subjects/articles/page/{page_id}/")
    Call<String> getBlogPostsApi(@Path("page_id") int pageNumber);

    @GET()
    Call<String> getPostDetails(@Url String url);

    @GET("nigerian-laws/")
    Call<String> getLawsOfNigeria(@Query("lcp_page0") int pageNumber);

    @GET("court-rules/")
    Call<String> getCourtRules(@Query("lcp_page0") int pageNumber);

    @GET()
    Call<String> getDirectory(@Url String url);

    @GET("/updatejson")
    Call<UpdateModel> checkForUpdate();
}