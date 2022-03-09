package com.example.yeomanda_kotlin.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeomanda_kotlin.dtos.JoinDto
import com.example.yeomanda_kotlin.emailauthentication.SendMail
import com.example.yeomanda_kotlin.R

class SignUpActivity1 : AppCompatActivity() {
    var isAuth= false ; var isMan= false ; var isWoman = false
    var emailAuth : String?=null
    lateinit var nextBtn : Button
    var user_email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up1)
        init()
    }
    fun init(){
        nextBtn = findViewById<Button>(R.id.nextBtn)
        val certificationBtn = findViewById<Button>(R.id.certificationBtn)
        val okBtn = findViewById<Button>(R.id.okBtn)
        val emailEdt = findViewById<EditText>(R.id.emailEdt)
        val passwordEdt = findViewById<EditText>(R.id.passwordEdt)
        val certificationNumEdt = findViewById<EditText>(R.id.certNum)
        val linearLayout = findViewById<LinearLayout>(R.id.signUpLinearLayout)
        val mBtn = findViewById<Button>(R.id.mBtn)
        val wBtn = findViewById<Button>(R.id.wBtn)
        val birthEdt = findViewById<EditText>(R.id.birthEdt)
        val nameEdt = findViewById<EditText>(R.id.nameEdt)

        okBtn.setOnClickListener {
            certificationNumEdt.visibility = View.GONE;
            okBtn.visibility = View.GONE
            certificationBtn.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
            isAuth = true
            emailEdt.isEnabled = false
            /*
            if(emailAuth.equals(certificationNumEdt.getText().toString())){
                certificationNumEdt.setVisibility(View.GONE);
                okBtn.setVisibility(View.GONE);
                certificationBtn.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                isAuth=true;
                emailEdt.setEnabled(false);
                Toast.makeText(getApplicationContext(),"인증이 완료되었습니다.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"인증번호를 확인해주십시오.",Toast.LENGTH_LONG).show();
            }

             */
        }

        mBtn.setOnClickListener {
            if (isWoman) {
                wBtn.setBackgroundResource(R.drawable.ic_yellow_button)
                isWoman = false
            }
            mBtn.setBackgroundResource(R.drawable.ic_dark_yellow_button)
            isMan = true
        }

        wBtn.setOnClickListener {
            if (isMan) {
                mBtn.setBackgroundResource(R.drawable.ic_yellow_button)
                isMan = false
            }
            wBtn.setBackgroundResource(R.drawable.ic_dark_yellow_button)
            isWoman = true
        }

        nextBtn.setOnClickListener {
            if(passwordEdt.text.toString().length >=8){
                val joinDto = JoinDto(emailEdt.text.toString(),passwordEdt.text.toString(),
                    nameEdt.text.toString(),birthEdt.text.toString(),if (isWoman) "W" else "M")
                val intent = Intent(applicationContext, SignUpActivity2::class.java)
                intent.putExtra("joinDto", joinDto)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "비밀번호를 8자리 이상 입력하시오", Toast.LENGTH_LONG).show()
            }
        }

        //이메일 인증 버튼
        certificationBtn.setOnClickListener {
            if (isValidEmail(user_email)) {
                val sendMail = SendMail()
               emailAuth = sendMail.sendSecurityCode(applicationContext, emailEdt.text.toString())
            } else {
                Toast.makeText(applicationContext, "이메일형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        emailEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                user_email = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        passwordEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(password: Editable) {
                val text1 = password.toString()
                val text2 = birthEdt.text.toString()
                val text3 = nameEdt.text.toString()
                if (text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty()) {
                    onNextBtn()
                } else {
                    offNextBtn()
                }
            }
        })
        birthEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(birth: Editable) {
                val text1 = birth.toString()
                val text2 = passwordEdt.text.toString()
                val text3 = nameEdt.text.toString()
                if (text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty()) {
                    onNextBtn()
                } else {
                    offNextBtn()
                }
            }
        })
        nameEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(name: Editable) {
                val text1 = name.toString()
                val text2 = birthEdt.text.toString()
                val text3 = passwordEdt.text.toString()
                if (text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty()) {
                    onNextBtn()
                } else {
                    offNextBtn()
                }
            }
        })
    }


    // 다음 버튼 활성화
    private fun onNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_pale_sky_blue_rounded_rectangle)
        nextBtn.setTextColor(resources.getColor(R.color.black))
        nextBtn.isEnabled = true
    }

    // 다음 버튼 비활성화
    private fun offNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_disabled_button)
        nextBtn.setTextColor(resources.getColor(R.color.sub_gray))
        nextBtn.isEnabled = false
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}