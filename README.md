# STACK & SKILL
<br><br>


> ## Android
Kotlin 공부를 위해 Java 버전으로 짜놓은 프로젝트를 Kotlin언어로 변경한 버전이다.
- Java Version Link <br>
https://github.com/kenny8062/yeomanda
<br><br>
***
<br>


# Retrofit
<br>

> ## Request Method
> 
@Get, @Post 사용
- Get은 Read, 정보 조회용도
  ``` kotlin
    @GET("/menuBar/getProfile")
    fun getMyProfile(
        @Header("Authorization") userToken: String
    ): Call<ProfileResponseDto>
  ```
- Post는 BODY에 전송할 데이터를 담아서 서버에 생성
- Dto를 만들어서 Body로 보낼 수 있지만 Dto없이 @FormUrlEncoded와 @Field를 사용하여 보낼 수도 있음
  ```kotlin
  
    @POST("/user/login")
    fun login(@Body loginDto : LoginDto) : Call<LoginResponseDto>
              
    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("email") email: String,
              @Field("password") password : String,
              @Field("fcm_token") fcm_token : String) : Call<LoginResponseDto>

  ```
- 추가로 MultiPart는 회원가입시 사진(파일)과 회원정보를 한번에 보낼 때 @Post와 함께 사용 
    ```kotlin
    @Multipart
    @POST("menuBar/updateProfile")
    fun updateMyProfile(
        @Header("Authorization") userToken: String,
        @Part("email") email: RequestBody,
        @Part uri: ArrayList<MultipartBody.Part>,
        @Part totalSelfImage: ArrayList<MultipartBody.Part>
    ): Call<WithoutDataResponseDto>
    ```
    
<br><br>
> ## execute vs enqueue
- execute
동기처리시 사용
단, 메인쓰레드에서 네트워크 연결을 제한하므로 새로운 Thread를 생성 하여 사용 가능

- enqueue
비동기처리시 사용(자동으로 백그라운드 쓰레드로 동작)

<br><br>
> Retrofit ISSUE

enqueue는 execute와 달리 함수 실행시 바로 백그라운드로 넘겨서 작업을 하기 때문에 새로운 thread를 생성 할 필요가 없다.
하지만 enqueue를 사용할 경우 성공적으로 리스폰스를 받았을때 실행되는 OnResponse 함수 안에서 작업을 안하면
execute때처럼 NullException이 일어나기 때문에 OnResponse 함수 안에서 모든 작업을 해야 했다.
enqueue로 코드를 바꿈으로서 Activity가 코드가 더 길어져서 더 무거워진것이 보였다.
이는 나중에 유지보수와 수정을 할 때 불편함을 겪을 수 있다.
그래서 다음번에는 MVVM 구조를 공부 한 뒤 MVVM 구조로 다시 개발을 해 볼 예정이다.
- Java and execute를 사용한 Link
https://github.com/kenny8062/yeomanda


<br><br>
***
<br>

> ## Firebase 클라우드 메시징
- In-App 채팅시 상대에게 알림을 보내기 위해 사용
- Firebase 토큰은 기기마다 혹은 주기적으로 변경되기 때문에 로그인시 서버로 Token값 갱신
``` kotlin
      FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("Tag",it.result)
            fcmToken=it.result }
```

<br><br>
***
<br>

> ## 이메일인증
- javax.mail Api 사용
- Math.random() 함수를 이용하여 랜덤한 인증코드 생성
- https://github.com/kenny8062/YeoManDa_Kotlin/blob/main/Yeomanda_Kotlin/app/src/main/java/com/example/yeomanda_kotlin/emailauthentication/GmailSender.kt

<br><br>
***
<br>

> ## socketio
- 채팅을 위한 서버와 클라이언트의 양방향 실시간 통신
- mSocket.on(“EVENT_NAME”,mListener) 서버에서 보낸 이벤트 듣기
- mSocket.emit("chatRoom", roomInfo); 데이터 담아서 서버로 이벤트 발생 시키기
- mSocket.emit("chatRoom"); 데이터 없이 서버로 이벤트 발생 시키기
``` java
        //Socket연결
          try {
                 mSocket = IO.socket("URL")
                 mSocket.connect()
                 Log.d("connect", "ok")
          } catch (e: URISyntaxException) {
                 Log.e("ChatSocketError", e.reason)
                 e.printStackTrace()
          }
        //mSocket.on 으로 서버에서 보낸 이벤트 듣기
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        
        // 서버에서 Socket.EVENT_CONNECT 이벤트를 Android로 보낼 시 동작
        //mSocket.emit으로 서버로 이벤트 전송
        var onConnect = Emitter.Listener {
        mSocket.emit("chatRoom", roomInfo)
        }
        
```

>version issue
- nodejs - 4.2.0
- android - 2.0.0
<br>
