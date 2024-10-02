package com.example.eventcal;

import com.example.eventcal.pojo.MultipleResource;
import com.example.eventcal.pojo.TestUser;
import com.example.eventcal.pojo.User;
import com.example.eventcal.pojo.UserList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("")
    Call<MultipleResource> doGetListResources();

    @POST("user/create/")
    Call<User> createUser(@Body User user);

    @POST("api/users/")
    Call<TestUser> createTestUser(@Body TestUser user);

    @GET("users")
    Call<UserList> doGetUserList();

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}
