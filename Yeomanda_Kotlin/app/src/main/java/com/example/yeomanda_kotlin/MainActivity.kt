package com.example.yeomanda_kotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.yeomanda_kotlin.dtos.LocationDto
import com.example.yeomanda_kotlin.dtos.TeamInfoDto
import com.example.yeomanda_kotlin.retrofit.responsedto.ChatRoomResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.LocationResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.ProfileResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.WithoutDataResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import com.example.yeomanda_kotlin.chatpkg.ChatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //lateinit var currentMarker: Marker
    lateinit var context: Context
    var mMap: GoogleMap? = null
    val TAG = "googlemap_example"
    val GPS_ENABLE_REQUEST_CODE = 2001
    val UPDATE_INTERVAL_MS: Int = 1000 // 1초

    val FASTEST_UPDATE_INTERVAL_MS: Int = 500 // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    val PERMISSIONS_REQUEST_CODE = 100
    var needRequest = false

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var teamNumCount = 0
    lateinit var myToken: String
    lateinit var myEmail: String
    var hasPlanned = false

    var mLayout: View? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var locationRequest: LocationRequest
    lateinit var location: Location
    var locationResponseDto: LocationResponseDto? = null

    lateinit var mCurrentLocatiion: Location
    var currentPosition: LatLng? = null

    lateinit var adapter: ArrayAdapter<*>
    var sameLocationTeams = ArrayList<ArrayList<TeamInfoDto>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        myToken = intent.getStringExtra("token").toString()
        myEmail = intent.getStringExtra("email").toString()
        hasPlanned = intent.getBooleanExtra("hasPlanned", false)
        init()
    }

    fun init() {
        val createBoardBtn = findViewById<Button>(R.id.createBoardBtn)
        val menuBtn = findViewById<ImageView>(R.id.menuIcon)
        val favoriteTeam = findViewById<TextView>(R.id.favoriteTeam)
        val profileRetouch = findViewById<TextView>(R.id.profileRetouch)
        val cancelTravel = findViewById<TextView>(R.id.cancelTravel)
        val chatRoom = findViewById<TextView>(R.id.chatRoom)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawerView = findViewById<View>(R.id.drawer)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        mLayout = findViewById(R.id.layout_main)


        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS.toLong())
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS.toLong())


        val builder = LocationSettingsRequest.Builder()

        builder.addLocationRequest(locationRequest)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(drawerView)
        }

        createBoardBtn.setOnClickListener {
            val intent = Intent(applicationContext, CreateBoard::class.java)
            intent.putExtra("lat", location.latitude)
            intent.putExtra("lon", location.longitude)
            startActivity(intent)
        }


        favoriteTeam.setOnClickListener {

            val favoriteIntent = Intent(applicationContext, MyFavoriteList::class.java)
            favoriteIntent.putExtra("token", myToken)
            startActivity(favoriteIntent)

        }


        cancelTravel.setOnClickListener {
            /*
            val builder = AlertDialog.Builder(context)
            builder.setMessage("게시판 등록을 취소하시겠습니까?")
            builder.setPositiveButton("확인") { dialog, id -> // User clicked OK button
                retrofitClient = RetrofitClient()
                val withoutDataResponseDto: WithoutDataResponseDto =
                    retrofitClient.deleteBoard(myToken)
                while (withoutDataResponseDto == null) {
                    Log.d("error", " withoutDataResponseDto is null")
                }
                if (withoutDataResponseDto.getSuccess()) {
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
                Toast.makeText(
                    applicationContext,
                    withoutDataResponseDto.getMessage(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.setNegativeButton(
                "취소"
            ) { dialog, id ->
                // User cancelled the dialog
            }
            val dialog = builder.create()
            dialog.show()
             */
        }

        profileRetouch.setOnClickListener {
            /*
            val intent = Intent(applicationContext, MyProfile::class.java)
            intent.putExtra("token", myToken)
            intent.putExtra("email", myEmail)
            startActivity(intent)

             */
        }
        chatRoom.setOnClickListener {
            /*
            val chatIntent = Intent(applicationContext, ChatListActivity::class.java)
            chatIntent.putExtra("token", myToken)
            chatIntent.putExtra("myEmail", myEmail)
            startActivity(chatIntent)

             */
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady :")

        mMap = googleMap

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation()


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates() // 3. 위치 업데이트 시작
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(
                    mLayout!!, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("확인") { // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(
                            this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE
                        )
                    }.show()
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }



        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.setOnMarkerClickListener(this)
    }

    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations

            if (locationList.size > 0) {
                location = locationList[locationList.size - 1]
                if (currentPosition==null) {
                    currentPosition = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 15f)
                    mMap?.moveCamera(cameraUpdate)

                    //근처 TeamInfo 가져오기
                    var locationDto = LocationDto(
                        java.lang.Double.toString(location.latitude),
                        java.lang.Double.toString(location.longitude)
                    )
                    RetrofitService.retrofitInterface.sendLocation(locationDto)
                        .enqueue(object : Callback<LocationResponseDto> {
                            override fun onResponse(
                                call: Call<LocationResponseDto>,
                                response: Response<LocationResponseDto>
                            ) {
                                var teamArr: ArrayList<TeamInfoDto> = ArrayList()
                                var isOverlap = false
                                if (response.body()?.data?.isNotEmpty() == true) {
                                    for (i in 0 until response.body()?.data?.size!!) {

                                        for (j in sameLocationTeams.indices) {
                                            if (response.body()?.data?.get(i)?.locationGps.equals(
                                                    sameLocationTeams[j][0].locationGps
                                                )
                                            ) {
                                                sameLocationTeams[j].add(
                                                    response.body()?.data?.get(
                                                        i
                                                    )!!
                                                )
                                                isOverlap = true
                                                break
                                            }
                                        }

                                        if (isOverlap) {
                                            isOverlap = false
                                        } else {
                                            teamArr.add(response.body()?.data?.get(i)!!)
                                            sameLocationTeams.add(teamArr)
                                            teamArr = ArrayList()
                                        }
                                    }

                                    for (i in sameLocationTeams.indices) {
                                        var locationArr =
                                            sameLocationTeams[i][0].locationGps.split(",")
                                        val teamsGPS = LatLng(
                                            locationArr[0].toDouble(),
                                            locationArr[1].toDouble()
                                        )
                                        mMap?.addMarker(
                                            MarkerOptions()
                                                .position(teamsGPS)
                                                .title(
                                                    response.body()?.data?.get(i)?.teamNo.toString()
                                                )
                                        )?.tag =
                                            sameLocationTeams[i]
                                    }
                                }
                            }

                            override fun onFailure(call: Call<LocationResponseDto>, t: Throwable) {
                                Log.d("error", "locationResponse is null")
                            }
                        })
                }
                mCurrentLocatiion = location
            }


        }
    }


    //마커 클릭시 이벤트(게시판 AlertDialog로 띄워주기)
    override fun onMarkerClick(marker: Marker): Boolean {
        val teamInfoDto = marker.tag as ArrayList<TeamInfoDto>

        var items = ArrayList<String>()
        var boardDialogView = layoutInflater.inflate(R.layout.custom_show_travelers, null)

        var profileDialogView = layoutInflater.inflate(R.layout.activity_person_info, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(boardDialogView)

        val alertDialog = builder.create()
        alertDialog.show()


        val backTeamBtn = alertDialog.findViewById<ImageView>(R.id.backTeamBtn)
        val nextTeamBtn = alertDialog.findViewById<ImageView>(R.id.nextTeamBtn)
        val favoriteBtn = alertDialog.findViewById<ImageView>(R.id.favoriteBtn)
        val chatBtn = alertDialog.findViewById<ImageView>(R.id.chatBtn)
        val customTravelDate = alertDialog.findViewById<TextView>(R.id.customTravelDate)
        val teamName = alertDialog.findViewById<TextView>(R.id.customTeamName)

        favoriteBtn?.setOnClickListener {
            RetrofitService.retrofitInterface.postFavoriteTeam(
                myToken,
                teamInfoDto[teamNumCount].teamNo
            ).enqueue(object : Callback<WithoutDataResponseDto> {
                override fun onResponse(
                    call: Call<WithoutDataResponseDto>,
                    response: Response<WithoutDataResponseDto>
                ) {
                    Log.d("good!", response.body()?.message!!)
                }

                override fun onFailure(call: Call<WithoutDataResponseDto>, t: Throwable) {
                    Log.d("error", " withoutDataResponseDto is null")
                }

            })

        }

        chatBtn?.setOnClickListener {
            RetrofitService.retrofitInterface.markerToChat(
                myToken,
                teamInfoDto[teamNumCount].teamNo.toString()
            ).enqueue(object : Callback<ChatRoomResponseDto> {
                override fun onResponse(
                    call: Call<ChatRoomResponseDto>,
                    response: Response<ChatRoomResponseDto>
                ) {

                    if (response.body()?.success == true) {
                        val intent = Intent(applicationContext, ChatActivity::class.java)
                        intent.putExtra("roomId", response.body()?.data?.roomId)
                        intent.putExtra("token", myToken)
                        intent.putExtra("myEmail", myEmail)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "게시판을 등록해주세요", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ChatRoomResponseDto>, t: Throwable) {
                    Log.e("error", "chatRoomResponseDto is null")
                }
            })

        }

        val listView = alertDialog.findViewById<ListView>(R.id.personList)

        listView?.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(applicationContext, items[position], Toast.LENGTH_SHORT).show()

            profileDialogView = layoutInflater.inflate(R.layout.activity_person_info, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(profileDialogView)

            val alertDialog = builder.create()
            alertDialog.show()

            val personSubImage1 = alertDialog.findViewById<ImageView>(R.id.personSubImage1)
            val personMainImage = alertDialog.findViewById<ImageView>(R.id.personMainImage)
            val personSubImage2 = alertDialog.findViewById<ImageView>(R.id.personsubImage2)
            val personEmail = alertDialog.findViewById<TextView>(R.id.personEmail)
            val personSex = alertDialog.findViewById<TextView>(R.id.personSex)
            val personName = alertDialog.findViewById<TextView>(R.id.personName)
            val personBirth = alertDialog.findViewById<TextView>(R.id.personBirth)
            RetrofitService.retrofitInterface.showProfile(
                myToken,
                teamInfoDto[teamNumCount].email[position]
            ).enqueue(object : Callback<ProfileResponseDto> {
                override fun onResponse(
                    call: Call<ProfileResponseDto>,
                    response: Response<ProfileResponseDto>
                ) {

                    personEmail?.text = response.body()?.data?.email
                    personSex?.text = response.body()?.data?.sex
                    personBirth?.text = response.body()?.data?.birth
                    personName?.text = response.body()?.data?.name

                    if (personMainImage != null) {
                        Glide.with(context)
                            .load(response.body()?.data?.files!![0])
                            .into(personMainImage)
                    }


                    if (personSubImage1 != null) {
                        Glide.with(context)
                            .load(response.body()?.data?.files!![1])
                            .into(personSubImage1)
                    }
                    if (personSubImage2 != null) {
                        Glide.with(context)
                            .load(response.body()?.data?.files!![2])
                            .into(personSubImage2)
                    }


                    //이미지 크게 보기1
                    personMainImage!!.setOnClickListener {
                        val intent = Intent(applicationContext, SelectImageActivity::class.java)
                        intent.putExtra("uri", response.body()?.data?.files!![0])
                        startActivity(intent)
                    }
                    //이미지 크게 보기2
                    personSubImage1!!.setOnClickListener {
                        val intent = Intent(applicationContext, SelectImageActivity::class.java)
                        intent.putExtra("uri", response.body()?.data?.files!![1])
                        startActivity(intent)
                    }
                    //이미지 크게 보기3
                    personSubImage2!!.setOnClickListener {
                        val intent = Intent(applicationContext, SelectImageActivity::class.java)
                        intent.putExtra("uri", response.body()?.data?.files!![2])
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<ProfileResponseDto>, t: Throwable) {
                    Log.d("error", "ProfileResponseDto is null")
                }
            })
        }
        teamNumCount = 0
        //같은 위치에 한 팀만 있을 경우
        if (teamInfoDto.size == 1) {
            items.addAll(teamInfoDto[teamNumCount].nameList)
            customTravelDate?.text = teamInfoDto[teamNumCount].travelDate
            teamName?.text = teamInfoDto[teamNumCount].teamName
            var adapter = ArrayAdapter(applicationContext, R.layout.single_listview_item, items)
            listView?.adapter = adapter
        }
        //같은 위치에 2개 이상의 팀이 있을 경우
        else {
            nextTeamBtn!!.visibility = View.VISIBLE
            items.addAll(teamInfoDto[teamNumCount].nameList)
            customTravelDate?.text = teamInfoDto[teamNumCount].travelDate
            teamName?.text = teamInfoDto[teamNumCount].teamName
            adapter = ArrayAdapter(applicationContext, R.layout.single_listview_item, items)
            listView?.adapter = adapter
            nextTeamBtn.setOnClickListener {
                teamNumCount++
                backTeamBtn!!.visibility = View.VISIBLE
                items.clear()
                items.addAll(teamInfoDto[teamNumCount].nameList)
                customTravelDate?.text = teamInfoDto[teamNumCount].travelDate
                teamName?.text = teamInfoDto[teamNumCount].teamName
                adapter = ArrayAdapter(
                    applicationContext,
                    R.layout.single_listview_item,
                    items
                )
                listView?.adapter = adapter
                //마지막 팀(다음버튼 없애기)
                if (teamInfoDto.size == teamNumCount + 1) {
                    nextTeamBtn.visibility = View.INVISIBLE
                }
            }

            backTeamBtn!!.setOnClickListener {
                teamNumCount--
                nextTeamBtn.visibility = View.VISIBLE
                items.clear()
                //첫번째 팀
                items.addAll(teamInfoDto[teamNumCount].nameList)
                customTravelDate?.text = teamInfoDto[teamNumCount].travelDate
                teamName?.text = teamInfoDto[teamNumCount].teamName
                adapter = ArrayAdapter(
                    applicationContext,
                    R.layout.single_listview_item,
                    items
                )
                listView?.adapter = adapter
                if (teamNumCount == 0) backTeamBtn.visibility = View.INVISIBLE
            }

        }

        return false
    }


    //현재 위치 업데이트 메소드
    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(
                TAG,
                "startLocationUpdates : call showDialogForLocationServiceSetting"
            )
            showDialogForLocationServiceSetting()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음")
                return
            }
            Log.d(
                TAG,
                "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates"
            )
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
            if (checkPermission()) mMap?.isMyLocationEnabled = true
        }
    }


    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음")
                        needRequest = true
                        return
                    }
                }
        }
    }


    //위치가 안잡힐경우 디폴트 위치값 설정
    fun setDefaultLocation() {

        //디폴트 위치, Seoul
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
        //currentMarker.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap?.moveCamera(cameraUpdate)
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates")
            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
            mMap?.isMyLocationEnabled = true
        }
    }


    override fun onStop() {
        super.onStop()
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates")
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }


}