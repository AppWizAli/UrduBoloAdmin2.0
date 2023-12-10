package com.admin.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.Adapter.AdapterGroup
import com.admin.Adapter.AdapterUser
import com.admin.Adapter.AdapterUserStatus
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelGroup
import com.admin.Models.ModelUser
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Ui.ActivityGroupMembers
import com.admin.Ui.ActivityManageVideo
import com.admin.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentGroupBinding
import kotlinx.coroutines.launch

class FragmentGroup : Fragment(), AdapterGroup.OnItemClickListener ,AdapterUserStatus.OnItemClickListener{

    private var _binding: FragmentGroupBinding? = null

    private val videoViewModel: VideoViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val db = Firebase.firestore


    private lateinit var adapter: AdapterUser
    private lateinit var modelGroup: ModelGroup

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
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
        constants= Constants()
        sharedPrefManager= SharedPrefManager(mContext)

modelGroup= ModelGroup()
        setUserAdapter()


        binding.floatingaction.setOnClickListener {
    showDialogAddGroup()
        }

        return root
    }
    private fun showDialogAddGroup() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_add_group)
        val name = dialog.findViewById<EditText>(R.id.groupname)
        val description = dialog.findViewById<EditText>(R.id.description)
        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setOnClickListener {

            if (name.text.toString().isEmpty() || description.text.toString().isEmpty()) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                var modelUser= ModelGroup()
                modelUser.name=name.text.toString()
                modelUser.description=description.text.toString()

                addGroup(modelUser)
            }

        }
        back.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }










    // Function to update the admin
    private fun updateGroup(modelUser: ModelGroup, dialog: Dialog) {
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.updateGroup(modelUser)
                .observe(this@FragmentGroup) { task ->
                    dialog.dismiss() // Dismiss the dialog before showing the result
                    if (task) {
                        utils.endLoadingAnimation()
                        setUserAdapter()
                        Toast.makeText(mContext, "Group Data Updated Successfully!!", Toast.LENGTH_SHORT).show()
                    } else {
                        setUserAdapter()
                        utils.endLoadingAnimation()
                        Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }






















    private fun addGroup(modelUser: ModelGroup) {
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            userViewModel.addGroup(modelUser).observe(viewLifecycleOwner)
            {task->
                if(task)
                {

                    dialog.dismiss()
                    utils.endLoadingAnimation()
                    setUserAdapter()
                    Toast.makeText(mContext, constants.USER_ADDED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
                }
                else
                {
                    dialog.dismiss()
                    utils.endLoadingAnimation()
                    setUserAdapter()
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun setUserAdapter() {

        var list= ArrayList<ModelGroup> ()
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.getGroupList()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()
                        if (task.result.size() > 0) {

                            for (document in task.result)
                                list.add(
                                    document.toObject(
                                        ModelGroup::class.java
                                    )
                                )


                        }
                        binding.rvGroups.layoutManager = LinearLayoutManager(mContext)
                        binding.rvGroups.adapter= AdapterGroup(list,this@FragmentGroup)

                    } else
                    {
                        utils.endLoadingAnimation()
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }


        }






    }

    override fun onGroupClick(modelUser: ModelGroup) {
  startActivity(Intent(mContext,ActivityGroupMembers::class.java))
    }

    override fun onGroupdeleteClick(modelUser: ModelGroup) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this Group?")
            .setPositiveButton("Yes") { _, _ ->

                lifecycleScope.launch {
                    utils.startLoadingAnimation()
                    userViewModel.deleteGroup(modelUser)
                        .observe(this@FragmentGroup)
                        { task->
                            if(task)
                            {
                                utils.endLoadingAnimation()
                                Toast.makeText(mContext, "Group deleted Successfully", Toast.LENGTH_SHORT).show()
                                setUserAdapter()
                            }
                            else
                            {
                                utils.endLoadingAnimation()
                                Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        dialog = builder.create()
        dialog?.show()
    }

    override fun onGroupUpdateClick(modelUser: ModelGroup) {
        val dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_add_group) // Replace with your dialog layout

        // Find views in the dialog layout
        val name = dialog.findViewById<EditText>(R.id.groupname)
        val description = dialog.findViewById<EditText>(R.id.description)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnNext) // Update button ID corrected

        // Populate dialog fields with admin details
        name.setText(modelUser.name)
        description.setText(modelUser.description)


        btnUpdate.setOnClickListener {
            // Get updated details from the dialog fields
            val uname = name.text.toString()
            val udescription = description.text.toString()

            // Update modelUser with the modified details
            modelUser.name = uname
            modelUser.description = udescription

            // Call the function to update admin
            updateGroup(modelUser, dialog)
        }

        // Show the dialog
        dialog.show()
    }

    override fun onAddMemberClick(modelUser: ModelGroup) {
      showDialogAddMember(modelUser)
    }
    private fun showDialogAddMember(modelUser: ModelGroup) {
        val bottomdialog = BottomSheetDialog(mContext)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom, null)
        bottomdialog.setContentView(bottomSheet)


        var rv=bottomdialog.findViewById<RecyclerView>(R.id.recyclerViewCustomBottomSheet)

        var list= ArrayList<ModelUser> ()
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.getUserList()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()
                        if (task.result.size() > 0) {

                            for (document in task.result)
                                if( document.toObject(
                                        ModelUser::class.java
                                    ).status.equals("unassigned"))
                                {
                                    list.add(
                                        document.toObject(
                                            ModelUser::class.java
                                        )
                                    )
                                }



                        }
                     rv!!.layoutManager = LinearLayoutManager(mContext)
                       rv!!.adapter= AdapterUserStatus("User",mContext,list,this@FragmentGroup)

                    } else
                    {
                        utils.endLoadingAnimation()
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }



            bottomdialog.show()
    }








    }

    override fun onAssignUserClick(modelVideo: ModelUser) {


/*lifecycleScope.launch {
    userViewModel.updateGroup()
}*/
    }

    override fun onUnAssignUserClcik(modelVideo: ModelUser) {

    }
}