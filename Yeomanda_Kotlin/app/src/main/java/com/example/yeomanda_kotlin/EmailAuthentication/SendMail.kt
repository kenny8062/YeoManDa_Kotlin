package com.example.yeomanda_kotlin.EmailAuthentication

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import javax.mail.MessagingException
import javax.mail.SendFailedException

class SendMail : AppCompatActivity() {
    var user = "hangichan97@gmail.com" // 보내는 계정의 id
    var password = "gksrlcks1@" // 보내는 계정의 pw
    fun sendSecurityCode(context: Context?, sendTo: String?): String? {
        try {
            val gMailSender = GmailSender(user, password)
            gMailSender.sendMail(
                "Yeomanda 이메일 인증입니다.", "인증코드는 : " + gMailSender.emailCode + " 입니다.",
                sendTo!!
            )
            Toast.makeText(context, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show()
            return gMailSender.emailCode
        } catch (e: SendFailedException) {
            Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: MessagingException) {
            Toast.makeText(context, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
