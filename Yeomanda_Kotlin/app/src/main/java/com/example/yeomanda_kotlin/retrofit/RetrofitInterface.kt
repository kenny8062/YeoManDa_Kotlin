package com.example.yeomanda_kotlin.retrofit

import com.example.yeomanda_kotlin.dtos.CreateBoardDto
import com.example.yeomanda_kotlin.dtos.LocationDto
import com.example.yeomanda_kotlin.retrofit.responsedto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.Field

import retrofit2.http.FormUrlEncoded

import retrofit2.http.GET

import retrofit2.http.Header

import retrofit2.http.Multipart

import retrofit2.http.POST

import retrofit2.http.Part

import retrofit2.http.Path


interface RetrofitInterface {

    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("email") email: String,
              @Field("password") password : String,
              @Field("fcm_token") fcm_token : String) : Call<LoginResponseDto>


    //다른 사람 프로필 보기
    @FormUrlEncoded
    @POST("/markup/userDetail")
    fun showProfile(
        @Header("Authorization") userToken: String,
        @Field("email") email: String
    ): Call<ProfileResponseDto>

    //회원가입
    @Multipart
    @POST("/user/signup")
    fun uploadJoin(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("sex") sex: RequestBody,
        @Part("birth") birth: RequestBody,
        @Part totalSelfImage: Array<MultipartBody.Part?>
    ): Call<WithoutDataResponseDto>

    //내 위치 서버로 전송
    @POST("travelers/showTravelers")
    fun sendLocation(
        @Body locationDto: LocationDto
    ): Call<LocationResponseDto>




    //내 프로필 가져오기
    @GET("/menuBar/getProfile")
    fun getMyProfile(
        @Header("Authorization") userToken: String
    ): Call<ProfileResponseDto>

    //내 프로필 수정
    @Multipart
    @POST("menuBar/updateProfile")
    fun updateMyProfile(
        @Header("Authorization") userToken: String,
        @Part("email") email: RequestBody,
        @Part uri: ArrayList<MultipartBody.Part>,
        @Part totalSelfImage: ArrayList<MultipartBody.Part>
    ): Call<WithoutDataResponseDto>


    //여행 계획 추가
    @POST("travelers/registerPlan")
    fun createBoard(
        @Body createBoardDto: java.util.ArrayList<CreateBoardDto>
    ): Call<CreateBoardResponseDto>

    //여행 취소
    @GET("menuBar/finishTravel")
    fun deleteBoard(
        @Header("Authorization") userToken: String
    ): Call<WithoutDataResponseDto>

    //즐겨찾기 추가
    @GET("markup/favorite/{team_no}")
    fun postFavoriteTeam(
        @Header("Authorization") userToken: String,
        @Path("team_no") team_no: Int
    ): Call<WithoutDataResponseDto>

    //즐겨찾기 삭제
    @GET("menuBar/deleteFavorite/{team_no}")
    fun deleteFavoriteTeam(
        @Header("Authorization") userToken: String,
        @Path("team_no") team_no: Int
    ): Call<WithoutDataResponseDto>

    //즐겨찾기 팀 리스트 보기
    @GET("menuBar/showFavoriteTeamName")
    fun showMyFavoriteTeam(
        @Header("Authorization") userToken: String
    ): Call<MyFavoriteListResponseDto>

    //즐겨찾기 팀 상세 정보
    @GET("menuBar/showFavoritesDetail/{teamName}")
    fun showMyFavriteTeamProfile(
        @Header("Authorization") userToken: String,
        @Path("teamName") teamName: String
    ): Call<MyFavoriteTeamProfileResponseDto>

    //채팅방 생성성
    @GET("chatting/InToChatRoom/{otherTeamNum}")
    fun markerToChat(
        @Header("Authorization") userToken: String,
        @Path("otherTeamNum") otherTeamNum: String
    ): Call<ChatRoomResponseDto>

    //채팅 리스트 가져오기
    @GET("chatting/getAllMyChatList")
    fun getChatList(
        @Header("Authorization") userToken: String
    ): Call<ChatListResponseDto>

    //기존에 있던 채팅기록 가져오기
    @FormUrlEncoded
    @POST("chatting/getAllMyChats")
    fun getAllMyChats(
        @Header("Authorization") userToken: String,
        @Field("chatRoomId") chatRoomId: String
    ): Call<AllMyChatsResponseDto>

}