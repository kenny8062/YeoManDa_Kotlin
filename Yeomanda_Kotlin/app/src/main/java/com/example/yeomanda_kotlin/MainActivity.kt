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
import com.example.yeomanda_kotlin.chatpkg.ChatListActivity
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
    val UPDATE_INTERVAL_MS: Int = 1000 // 1???

    val FASTEST_UPDATE_INTERVAL_MS: Int = 500 // 0.5???

    // onRequestPermissionsResult?????? ????????? ???????????? ActivityCompat.requestPermissions??? ????????? ????????? ????????? ???????????? ?????? ???????????????.
    val PERMISSIONS_REQUEST_CODE = 100
    var needRequest = false

    // ?????? ???????????? ?????? ????????? ???????????? ???????????????.
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

            val builder = AlertDialog.Builder(context)
            builder.setMessage("????????? ????????? ?????????????????????????")
            builder.setPositiveButton("??????") { dialog, id -> // User clicked OK button
                RetrofitService.retrofitInterface.deleteBoard(myToken).enqueue(object : Callback<WithoutDataResponseDto>{
                    override fun onResponse(
                        call: Call<WithoutDataResponseDto>,
                        response: Response<WithoutDataResponseDto>
                    ) {

                        if (response.body()?.success == true) {
                            val intent = intent
                            finish()
                            startActivity(intent)
                        }
                        Toast.makeText(
                            applicationContext,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(call: Call<WithoutDataResponseDto>, t: Throwable) {
                        Log.d("error", t.message.toString())
                    }

                })
            }
            builder.setNegativeButton(
                "??????"
            ) { dialog, id ->
                // User cancelled the dialog
            }
            val dialog = builder.create()
            dialog.show()

        }

        profileRetouch.setOnClickListener {

            val intent = Intent(applicationContext, MyProfile::class.java)
            intent.putExtra("token", myToken)
            intent.putExtra("email", myEmail)
            startActivity(intent)
        }
        chatRoom.setOnClickListener {

            val chatIntent = Intent(applicationContext, ChatListActivity::class.java)
            chatIntent.putExtra("token", myToken)
            chatIntent.putExtra("myEmail", myEmail)
            startActivity(chatIntent)


        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady :")

        mMap = googleMap


        //????????? ????????? ?????? ??????????????? GPS ?????? ?????? ???????????? ???????????????
        //????????? ??????????????? ????????? ??????
        setDefaultLocation()


        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
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

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)
            startLocationUpdates() // 3. ?????? ???????????? ??????
        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Snackbar.make(
                    mLayout!!, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("??????") { // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                        ActivityCompat.requestPermissions(
                            this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE
                        )
                    }.show()
            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
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

                    //?????? TeamInfo ????????????
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


    //?????? ????????? ?????????(????????? AlertDialog??? ????????????)
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
                        Toast.makeText(applicationContext, "???????????? ??????????????????", Toast.LENGTH_LONG).show()
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


                    //????????? ?????? ??????1
                    personMainImage!!.setOnClickListener {
                        val intent = Intent(applicationContext, SelectImageActivity::class.java)
                        intent.putExtra("uri", response.body()?.data?.files!![0])
                        startActivity(intent)
                    }
                    //????????? ?????? ??????2
                    personSubImage1!!.setOnClickListener {
                        val intent = Intent(applicationContext, SelectImageActivity::class.java)
                        intent.putExtra("uri", response.body()?.data?.files!![1])
                        startActivity(intent)
                    }
                    //????????? ?????? ??????3
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
        //?????? ????????? ??? ?????? ?????? ??????
        if (teamInfoDto.size == 1) {
            items.addAll(teamInfoDto[teamNumCount].nameList)
            customTravelDate?.text = teamInfoDto[teamNumCount].travelDate
            teamName?.text = teamInfoDto[teamNumCount].teamName
            var adapter = ArrayAdapter(applicationContext, R.layout.single_listview_item, items)
            listView?.adapter = adapter
        }
        //?????? ????????? 2??? ????????? ?????? ?????? ??????
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
                //????????? ???(???????????? ?????????)
                if (teamInfoDto.size == teamNumCount + 1) {
                    nextTeamBtn.visibility = View.INVISIBLE
                }
            }

            backTeamBtn!!.setOnClickListener {
                teamNumCount--
                nextTeamBtn.visibility = View.VISIBLE
                items.clear()
                //????????? ???
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


    //?????? ?????? ???????????? ?????????
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
                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????")
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

    //??????????????? ????????? ????????? ????????? ?????? ????????????
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


    //??????????????? GPS ???????????? ?????? ????????????
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("?????? ????????? ????????????")
        builder.setMessage(
            """
            ?????? ???????????? ???????????? ?????? ???????????? ???????????????.
            ?????? ????????? ???????????????????
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("??????") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
            "??????"
        ) { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????")
                        needRequest = true
                        return
                    }
                }
        }
    }


    //????????? ??????????????? ????????? ????????? ??????
    fun setDefaultLocation() {

        //????????? ??????, Seoul
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "???????????? ????????? ??? ??????"
        val markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????"
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