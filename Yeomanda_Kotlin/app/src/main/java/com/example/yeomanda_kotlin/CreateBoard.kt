package com.example.yeomanda_kotlin

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeomanda_kotlin.dtos.CreateBoardDto
import com.example.yeomanda_kotlin.retrofit.responsedto.CreateBoardResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateBoard : AppCompatActivity() {
    var lat : Double? = null
    var lon : Double? = null
    val TYPE_TEXT_VARIATION_EMAIL_ADDRESS = 32
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        val intent = intent
        lat = intent.getDoubleExtra("lat", 0.0)
        lon = intent.getDoubleExtra("lon", 0.0)
        init()
    }
    fun init(){

        val createBtn = findViewById<Button>(R.id.createBtn)
        val startYearEdt = findViewById<EditText>(R.id.MyPlanYearStart)
        val startMonthEdt = findViewById<EditText>(R.id.MyPlanMonthStart)
        val startDayEdt = findViewById<EditText>(R.id.MyPlanDayStart)
        val endYearEdt = findViewById<EditText>(R.id.MyPlanYearEnd)
        val endMonthEdt = findViewById<EditText>(R.id.MyPlanMonthEnd)
        val endDayEdt = findViewById<EditText>(R.id.MyPlanDayEnd)
        val teamNameEdt = findViewById<EditText>(R.id.MyTeamName)
        val teamEmailEdt = findViewById<EditText>(R.id.MyTeamEmail)
        val plusBtn = findViewById<Button>(R.id.plusBtn)
        val cancleBtn = findViewById<Button>(R.id.cancelBtn)
        val ll = findViewById<LinearLayout>(R.id.ll)
        val edts = ArrayList<EditText>()
        var count = 1
        edts.add(teamEmailEdt)


        plusBtn.setOnClickListener {
            var addEdt = EditText(applicationContext)
            val p = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                teamEmailEdt.layoutParams.height
            )
            p.setMargins(0, 10, 0, 0)
            addEdt.setLayoutParams(p)
            addEdt.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            addEdt.setId(count++)
            //addEdt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_yellow_rounded_rectangle));
            edts.add(addEdt)
            ll.addView(addEdt)
        }

        cancleBtn.setOnClickListener {
            if (edts.size != 0) ll.removeView(edts[edts.size - 1])
            edts.removeAt(edts.size - 1)
            if (count > 0) count--
        }


        createBtn.setOnClickListener {
            val createBoardDtos: java.util.ArrayList<CreateBoardDto> =
                java.util.ArrayList<CreateBoardDto>()
            val myPlanDate =
                startYearEdt.text.toString() + startMonthEdt.text.toString() + startDayEdt.text.toString() + "~" + endYearEdt.text.toString() + endMonthEdt.text.toString() + endDayEdt.text.toString()
            for (i in 0 until count) {
                var createBoardDto = CreateBoardDto(lat.toString(),lon.toString(),edts[i].text.toString(),myPlanDate,teamNameEdt.text.toString())
                createBoardDtos.add(createBoardDto)
            }
            RetrofitService.retrofitInterface.createBoard(createBoardDtos).enqueue(object :
                Callback<CreateBoardResponseDto>{
                override fun onResponse(
                    call: Call<CreateBoardResponseDto>,
                    response: Response<CreateBoardResponseDto>
                ) {
                    if(response.body()?.success == true){
                        Toast.makeText(
                            applicationContext,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }else{
                        Toast.makeText(
                            applicationContext,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onFailure(call: Call<CreateBoardResponseDto>, t: Throwable) {
                    Log.d("error","CreateBoardResponse is null")
                }
            })
        }
    }
}