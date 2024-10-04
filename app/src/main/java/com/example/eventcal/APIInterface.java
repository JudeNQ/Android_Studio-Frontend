package com.example.eventcal;

import com.example.eventcal.pojo.CreateUser;
import com.example.eventcal.pojo.MultipleResource;
import com.example.eventcal.pojo.TestUser;
import com.example.eventcal.pojo.LoginUser;
import com.example.eventcal.pojo.UserList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @GET("")
    Call<MultipleResource> doGetListResources();

    @POST("user/login/")
    Call<LoginUser> login(@Body LoginUser loginUser);

    @POST("user/create/")
    Call<CreateUser> create(@Body CreateUser createUser);

    @GET("users")
    Call<UserList> doGetUserList();

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}
