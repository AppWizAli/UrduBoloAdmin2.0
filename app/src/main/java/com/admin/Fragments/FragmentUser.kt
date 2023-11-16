package com.admin.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterDrama
import com.admin.Adapter.AdapterUser
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelDrama
import com.admin.Models.ModelUser
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Ui.ActivityManageVideo
import com.admin.Ui.ActivitySeason
import com.admin.Ui.ActivityVideoDetail
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentHomeBinding
import com.urduboltv.admin.databinding.FragmentUserBinding
import kotlinx.coroutines.launch

class FragmentUser : Fragment(), AdapterUser.OnItemClickListener {
    private var _binding: FragmentUserBinding? = null

    private val videoViewModel: VideoViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val db = Firebase.firestore


    private lateinit var adapter: AdapterUser

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
constants=Constants()
        sharedPrefManager=SharedPrefManager(mContext)
setData()


        binding.floatingaction.setOnClickListener {
            showDialogAdduser()
        }




        return root
    }

    private fun showDialogAdduser() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_add_user)
        val email = dialog.findViewById<EditText>(R.id.userEmail)
        val password = dialog.findViewById<EditText>(R.id.userPassword)
        val name = dialog.findViewById<EditText>(R.id.userName)
        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setOnClickListener {

            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()|| name.text.toString().isEmpty()) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                var modelUser=ModelUser()
                modelUser.name=name.text.toString()
                modelUser.password=password.text.toString()
                modelUser.email=email.text.toString()

                addUser(modelUser)
            }

        }
        back.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onupdateclick(modelUser: ModelUser) {
        Toast.makeText(mContext, "update", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(modelUser: ModelUser) {
        Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show()

    }

    override fun onitemclick(modelUser: ModelUser) {
        val intent = Intent(mContext, ActivityManageVideo::class.java)
        intent.putExtra("user", modelUser.toString()) // Serialize to JSON
        mContext.startActivity(intent)
    }

    private fun addUser(modelUser: ModelUser) {
        utils.startLoadingAnimation()
    lifecycleScope.launch {
     userViewModel.addUser(modelUser).observe(viewLifecycleOwner)
     {task->
         if(task)
         {

             dialog.dismiss()
             utils.endLoadingAnimation()
             setData()
             Toast.makeText(mContext, constants.USER_ADDED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
         }
         else
         {
             dialog.dismiss()
             utils.endLoadingAnimation()
             setData()
             Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
         }
     }
    }
    }


    fun setData() {
        utils.startLoadingAnimation()
        var list= ArrayList<ModelUser> ()
        lifecycleScope.launch {
            userViewModel.getUserList()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()

                        if (task.result.size() > 0) {

                            for (document in task.result)
                                list.add(
                                    document.toObject(
                                        ModelUser::class.java
                                    )
                                )
                            binding.rvusers.layoutManager = LinearLayoutManager(mContext)
                            binding.rvusers.adapter= AdapterUser("User",list,this@FragmentUser)

                        }
                    } else Toast.makeText(
                        mContext,
                        constants.SOMETHING_WENT_WRONG_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()


                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }


        }






    }

}