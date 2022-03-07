package com.example.yeomanda_kotlin.EmailAuthentication

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.Message

import javax.mail.PasswordAuthentication

import javax.mail.Session

import javax.mail.Transport

import javax.mail.internet.InternetAddress

import javax.mail.internet.MimeMessage
import javax.mail.util.ByteArrayDataSource


class GmailSender : javax.mail.Authenticator {
    val mailhost : String ="smtp.gmail.com"
    lateinit var user : String
    lateinit var password : String
    lateinit var session : Session
    lateinit var emailCode : String

    constructor(user : String, password : String){
        this.user=user
        this.password=password
        val props = Properties()

        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = 465
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.ssl.enable"] = "true"
        props["mail.smtp.ssl.trust"] = "smtp.gmail.com"
        session=Session.getDefaultInstance(props, object : javax.mail.Authenticator(){
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user,password)
            }
        })
    }


    fun createEmailCode() : String {
        val str = arrayOf(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"
        )
        var newCode : String = ""
        for(i in 0 until 8){
            var random : Int = ((Math.random() * str.size).toInt())
            newCode +=str[random]
        }
        return newCode
    }
    override fun getPasswordAuthentication() : PasswordAuthentication{
        return PasswordAuthentication(user,password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(subject: String?, body: String, recipients: String) {
        val message = MimeMessage(session)
        val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/plain")) //본문 내용을 byte단위로 쪼개어 전달
        message.setFrom(InternetAddress(user)) //본인 이메일 설정
        message.subject = subject //해당 이메일의 본문 설정
        message.dataHandler = handler
        println(recipients)
        if (recipients.indexOf(',') > 0) message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipients)
        ) else message.setRecipient(
            Message.RecipientType.TO, InternetAddress(recipients)
        )
        Transport.send(message) //메시지 전달
        println("message sent successfully...")
    }

    class ByteArrayDataSource : DataSource {
        private var data: ByteArray
        private var type: String? = null

        constructor(data: ByteArray, type: String?) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String?) {
            this.type = type
        }

        override fun getContentType(): String {
            return type ?: "application/octet-stream"
        }

        @Throws(IOException::class)
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        @Throws(IOException::class)
        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }
    }
}