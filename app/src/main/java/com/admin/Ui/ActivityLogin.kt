package com.admin.Ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.admin.Data.SharedPrefManager
import com.admin.Models.Admin
import com.admin.Models.UserViewModel
import com.admin.Utils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityLoginBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var dialog: Dialog
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var utils:Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
utils= Utils(this@ActivityLogin)
        sharedPrefManager= SharedPrefManager(this@ActivityLogin)
        binding.btnSignIn.setOnClickListener {
            val enteredEmail = binding.etEmail.editText?.text.toString().trim()
    getUserEmailFromFirestore(enteredEmail)

        }
    }

    private fun getUserEmailFromFirestore(enteredEmail: String) {
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.getAdmin(enteredEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    utils.endLoadingAnimation()
                    val querySnapshot: QuerySnapshot? = task.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val admin: Admin? = querySnapshot.documents[0].toObject(Admin::class.java)
                        showDialogPin(admin)
                    } else {
                        utils.endLoadingAnimation()
                        binding.etEmail.error = "Invalid Email"
                    }
                } else {
                    utils.endLoadingAnimation()
                    binding.etEmail.error = "Invalid Email"
                }
            }
        }
    }
    fun showDialogPin(user:Admin?) {

        dialog = Dialog (this@ActivityLogin)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_set_pin)
        val etPin1 = dialog.findViewById<EditText>(R.id.etPin1)
        val etPin2 = dialog.findViewById<EditText>(R.id.etPin2)
        val etPin3 = dialog.findViewById<EditText>(R.id.etPin3)
        val etPin4 = dialog.findViewById<EditText>(R.id.etPin4)
        val etPin5 = dialog.findViewById<EditText>(R.id.etPin5)
        val etPin6 = dialog.findViewById<EditText>(R.id.etPin6)
        val tvClearAll = dialog.findViewById<TextView>(R.id.tvClearAll)
        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
        val btnSetPin = dialog.findViewById<Button>(R.id.btnSetPin)

        tvHeader.setText("Enter your Pin to Login !")
        btnSetPin.setText("Login")
        etPin1.requestFocus();
        utils.moveFocus( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))

        tvClearAll.setOnClickListener{
            utils.clearAll( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))
            etPin1.requestFocus();

        }
        btnSetPin.setOnClickListener {
            if(!utils.checkEmpty( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))){
                var pin : String =  utils.getPIN( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))
                loginUser(user,pin)
            }
            else Toast.makeText(this@ActivityLogin, "Please enter 6 Digits Pin!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
    private fun loginUser(user:Admin?,pin:String){

utils.startLoadingAnimation()
                    if (user != null) {
                        if(user.password.equals(pin)){
                            sharedPrefManager.saveAdminLogin(true)
                            utils.endLoadingAnimation()
                            if(!sharedPrefManager.isCeleBration())
                            {
                                startActivity(Intent(this@ActivityLogin,ActivityCelebration::class.java))
                          finish()
                            }
                            else
                            {
                                sharedPrefManager.saveAdmin(user)
                                Toast.makeText(this@ActivityLogin, "Login Succesfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@ActivityLogin,MainActivity::class.java))
                                finish()
                            }

                                }
                        else
                        {
                            utils.endLoadingAnimation()
                            Toast.makeText(this@ActivityLogin, "Incorrect Password", Toast.LENGTH_SHORT).show()

                            }



                        }


                    }



}
