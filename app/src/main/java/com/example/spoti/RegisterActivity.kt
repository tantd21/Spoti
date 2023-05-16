package com.example.spoti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var birthday: EditText
    private lateinit var register: Button
    private lateinit var login: Button
    private lateinit var dbRef:DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        name = findViewById(R.id.dkname)
        email = findViewById(R.id.dkemail)
        password = findViewById(R.id.dkpassword)
        confirmPassword = findViewById(R.id.cfpassword)
        birthday = findViewById(R.id.date)
        register = findViewById(R.id.btndkregister)
        login = findViewById(R.id.btndklogin)

        register.setOnClickListener {
            val name = name.text.toString().trim()
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            val confirmPassword = confirmPassword.text.toString().trim()
            val birthday = birthday.text.toString()



            // Kiểm tra xem tất cả các trường đã được điền vào hay chưa
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthday.isEmpty()) {
                // Hiển thị thông báo lỗi
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra xem mật khẩu và xác nhận mật khẩu có giống nhau không
            if (password != confirmPassword) {
                // Hiển thị thông báo lỗi
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không giống nhau!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Tạo người dùng mới trên Firebase Authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Người dùng đã được tạo thành công
                        val user = task.result?.user
                        // Lưu thông tin người dùng vào Firebase Realtime Database
                        saveUserDataToFirebaseDatabase(user?.uid, email, name, birthday, password )

                        // Chuyển đến màn hình đăng nhập
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        // Đăng ký thất bại
                        val exception = task.exception
                        // Hiển thị thông báo lỗi
                        Toast.makeText(this, "Đăng ký thất bại: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        birthday.setOnClickListener { showDatePickerDialog() }
    }


    private fun saveUserDataToFirebaseDatabase(userID: String?, email:String,name: String, birthday: String,password:String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID ?: "")
       val userID = databaseReference.push().key
        val user = User(userID, email, name, birthday, password)
        databaseReference.setValue(user)



    }







    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(
                day,
                month,
                year
            )
        } //Variables en el orden que queremos recibir
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val date = "$day/${month + 1}/$year"
        birthday.setText(date)
    }
}