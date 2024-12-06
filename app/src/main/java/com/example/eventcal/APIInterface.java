package com.example.eventcal;

import com.example.eventcal.pojo.CreateGroup;
import com.example.eventcal.pojo.CreateSchedule;
import com.example.eventcal.pojo.CreateUser;
import com.example.eventcal.pojo.EventList;
import com.example.eventcal.pojo.GroupList;
import com.example.eventcal.pojo.JoinGroup;
import com.example.eventcal.pojo.MultipleResource;
import com.example.eventcal.pojo.SaveEvent;
import com.example.eventcal.pojo.LoginUser;
import com.example.eventcal.pojo.Schedule;
import com.example.eventcal.pojo.ServerGroup;
import com.example.eventcal.pojo.UserList;
import com.example.eventcal.pojo.ServerEvent;

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

    @POST("user/login/")
    Call<LoginUser> login(@Body LoginUser loginUser);

    @POST("user/create/")
    Call<CreateUser> createUser(@Body CreateUser createUser);

    @POST("user/createschedule/")
    Call<CreateSchedule> createSchedule(@Body CreateSchedule createSchedule);

    @GET("groups/getschedule?")
    Call<Schedule> getSchedule(@Query("group") String groupId);

    @POST("events/create/")
    Call<ServerEvent> createEvent(@Body ServerEvent createServerEvent);

    @POST("groups/create/")
    Call<ServerGroup> createGroup(@Body ServerGroup group);

    @POST("groups/join/")
    Call<JoinGroup> joinGroup(@Body JoinGroup group);

    @GET("users")
    Call<UserList> doGetUserList();

    @GET("events/getall?")
    Call<EventList> doGetEventList(@Query("date") String date);

    @GET("groups/getGroups")
    Call<GroupList> doGetAllGroups();

    @GET("events/getusers")
    Call<EventList> doGetUsersEvents(@Query("user") String userId);

    @GET("groups/getGroups?")
    Call<GroupList> doGetUsersGroups(@Query("user") String userId);

    @POST("events/saveEvent/")
    Call<SaveEvent> saveEvent(@Body SaveEvent saveEvent);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}
