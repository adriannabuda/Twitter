package sdp.project.twitter.API;


import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sdp.project.twitter.Result;
import sdp.project.twitter.User;

public interface APIService {

    //The register call
    @FormUrlEncoded
    @POST("register")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("picture_path") String picture_path);

    //The login call
    @FormUrlEncoded
    @POST("login")
    Call<Result> loginUser(
            @Field("username") String username,
            @Field("password") String password);

    //Follow other user call
    @FormUrlEncoded
    @POST("followuser")
    Call<Result> followUser(
            @Field("userId") int userId,
            @Field("followUserId") int followUserId,
            @Field("op") int op);
}
