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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterAdmin
import com.admin.Adapter.AdapterUser
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.Admin
import com.admin.Models.ModelUser
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Ui.ActivityManageVideo
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentAdminManagmentBinding
import com.urduboltv.admin.databinding.FragmentUserBinding
import com.urduboltv.admin.databinding.FragmentUserManagementBinding
import kotlinx.coroutines.launch

class FragmentAdminManagment : Fragment(),AdapterAdmin.OnItemClickListener {

    private var _binding: FragmentAdminManagmentBinding? = null

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
        _binding = FragmentAdminManagmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
        constants=Constants()
        sharedPrefManager=SharedPrefManager(mContext)


        setAdminAdapter()


        binding.floatingaction.setOnClickListener {
            showChoiceDialog()
        }

        return root
    }

    @SuppressLint("SuspiciousIndentation")
    private  fun showChoiceDialog()
    {


        val builder = AlertDialog.Builder(mContext)

        builder.setTitle("Select Option")
            .setPositiveButton("Add Admin") { dialog, which ->
                showDialogAddAdmin()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()

            }


        val dialog = builder.create()
        dialog.show()
    }

private fun showDialogAddAdmin() {
            val dialog = Dialog(mContext, R.style.FullWidthDialog)
            dialog.setContentView(R.layout.dialog_admin)

            val editTextAdminName = dialog.findViewById<EditText>(R.id.etAdminName)
            val editTextEmail = dialog.findViewById<EditText>(R.id.editTextEmail)
            val editTextPassword = dialog.findViewById<EditText>(R.id.editTextPassword)
            val spinnerRole = dialog.findViewById<Spinner>(R.id.spinnerRole)
            val btnAddAdmin = dialog.findViewById<Button>(R.id.btnAddAdmin)

            // Set up the spinner adapter with roles (Owner or Editor)
            val roles = arrayOf("Owner", "Editor")
            val adapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, roles)
            spinnerRole.adapter = adapter

            // Set text color for the spinner
            val textColorWhite = mContext.resources.getColor(android.R.color.white)
            spinnerRole.setSelection(0, true) // Set initial selection to ensure text color change
            val spinnerAdapter = spinnerRole.adapter as ArrayAdapter<*>
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRole.adapter = spinnerAdapter
            spinnerRole.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    (view as? TextView)?.setTextColor(textColorWhite)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })

            btnAddAdmin.setOnClickListener {
                val adminName = editTextAdminName.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val selectedRole = spinnerRole.selectedItem.toString()

                if(adminName.isEmpty()|| email.isEmpty()|| password.isEmpty()|| selectedRole.isEmpty())
                {
                    Toast.makeText(mContext, "Please enter All fields", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if(selectedRole.equals("Owner"))
                    {

                        val builder = AlertDialog.Builder(mContext)

                        builder.setTitle("Alert!!")
                            .setMessage("If you make this person owner he will be able to remove you from admin post and add other admins.Are you agree to make him/her admin as owner?")
                            .setPositiveButton("Yes,I agree") { dialog, which ->
                                addAdmin(Admin(adminName,email,password,"",selectedRole))

                            }
                            .setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }

                        val dialog = builder.create()
                        dialog.show()
                    }
                    else
                    {
                        addAdmin(Admin(adminName,email,password,"",selectedRole))
                    }


                }

                dialog.dismiss()
            }

            dialog.show()
        }

        private fun addAdmin(modelUser: Admin)
        {
            lifecycleScope.launch {
                utils.startLoadingAnimation()
                userViewModel.addAdmin(modelUser)
                    .observe(viewLifecycleOwner)
                    {
                            task->
                        if(task)
                        {
                            utils.endLoadingAnimation()
                            setAdminAdapter()
                            Toast.makeText(mContext, "Admin Account Created Successfuly", Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            utils.endLoadingAnimation()
                            setAdminAdapter()
                            Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }















     fun setAdminAdapter() {
            utils.startLoadingAnimation()
            var list= ArrayList<Admin> ()
            lifecycleScope.launch {
                userViewModel.getAdminList()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            utils.endLoadingAnimation()

                            if (task.result.size() > 0) {

                                for (document in task.result)
                                    list.add(
                                        document.toObject(
                                            Admin::class.java
                                        )
                                    )
                                binding.rvusers.layoutManager = LinearLayoutManager(mContext)
                                binding.rvusers.adapter= AdapterAdmin(mContext,list,this@FragmentAdminManagment)

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

   override fun onAdminupdateclick(modelUser: Admin) {

       if(modelUser.email.equals("theRajaMahar@gmail.com"))
       {
           Toast.makeText(mContext, "Sorry!thisAdmin can't  update", Toast.LENGTH_SHORT).show()
       }
       else {


           // Show a dialog to display and potentially edit the admin details
           val dialog = Dialog(mContext, R.style.FullWidthDialog)
           dialog.setContentView(R.layout.dialog_admin) // Replace with your dialog layout

           // Find views in the dialog layout
           val etAdminName = dialog.findViewById<EditText>(R.id.etAdminName)
           val editTextEmail = dialog.findViewById<EditText>(R.id.editTextEmail)
           val editTextPassword = dialog.findViewById<EditText>(R.id.editTextPassword)
           val spinnerRole = dialog.findViewById<Spinner>(R.id.spinnerRole)
           val btnUpdate =
               dialog.findViewById<Button>(R.id.btnAddAdmin) // Update button ID corrected
           val roles = arrayOf("Owner", "Editor")
           val adapter =
               ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, roles)
           spinnerRole.adapter = adapter

           // Set text color for the spinner
           val textColorWhite = mContext.resources.getColor(android.R.color.white)
           spinnerRole.setSelection(0, true) // Set initial selection to ensure text color change
           val spinnerAdapter = spinnerRole.adapter as ArrayAdapter<*>
           spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
           spinnerRole.adapter = spinnerAdapter
           spinnerRole.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
               override fun onItemSelected(
                   parent: AdapterView<*>,
                   view: View?,
                   position: Int,
                   id: Long
               ) {
                   (view as? TextView)?.setTextColor(textColorWhite)
               }

               override fun onNothingSelected(parent: AdapterView<*>?) {}
           })
           // Populate dialog fields with admin details
           etAdminName.setText(modelUser.name)
           editTextEmail.setText(modelUser.email)
           editTextPassword.setText(modelUser.password)


           btnUpdate.setOnClickListener {
               // Get updated details from the dialog fields
               val updatedName = etAdminName.text.toString()
               val updatedEmail = editTextEmail.text.toString()
               val updatedPassword = editTextPassword.text.toString()
               val updatedRole = spinnerRole.selectedItem.toString()

               // Update modelUser with the modified details
               modelUser.name = updatedName
               modelUser.email = updatedEmail
               modelUser.password = updatedPassword
               modelUser.role = updatedRole

               // Call the function to update admin
               updateAdmin(modelUser, dialog)
           }

           // Show the dialog
           dialog.show()
       }
        }

        // Function to update the admin
        private fun updateAdmin(modelUser: Admin, dialog: Dialog) {


            if(modelUser.email.equals("theRajaMahar@gmail.com"))
            {
                Toast.makeText(mContext, "You can't update this Admin!!", Toast.LENGTH_SHORT).show()
            }
            else
            {


            lifecycleScope.launch {
                utils.startLoadingAnimation()
                userViewModel.updateAdmin(modelUser)
                    .observe(this@FragmentAdminManagment) { task ->
                        dialog.dismiss() // Dismiss the dialog before showing the result
                        if (task) {
                            utils.endLoadingAnimation()
                            setAdminAdapter()
                            Toast.makeText(mContext, "Admin Data Updated Successfully!!", Toast.LENGTH_SHORT).show()
                        } else {
                            setAdminAdapter()
                            utils.endLoadingAnimation()
                            Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            }
        }


        override fun onAdminDeleteClick(modelUser: Admin) {

            if(modelUser.email.equals("theRajaMahar@gmail.com"))
            {
                Toast.makeText(mContext, "You can't delete this Admin!!", Toast.LENGTH_SHORT).show()
            }
            else {
                lifecycleScope.launch {
                    utils.startLoadingAnimation()
                    userViewModel.deletAdmin(modelUser)
                        .observe(this@FragmentAdminManagment)
                        { task ->
                            if (task) {
                                utils.endLoadingAnimation()
                                Toast.makeText(
                                    mContext,
                                    "Admin deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setAdminAdapter()
                            } else {
                                utils.endLoadingAnimation()
                                Toast.makeText(
                                    mContext,
                                    constants.SOMETHING_WENT_WRONG_MESSAGE,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        override fun onAdminitemclick(modelUser: Admin) {
            var dialogFA=Dialog(requireContext())
            dialogFA.setContentView(R.layout.dialogdetail)
            dialogFA.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogFA.setCancelable(true)
            dialogFA.findViewById<TextView>(R.id.tvRole).text =modelUser.role
            dialogFA.findViewById<TextView>(R.id.tvFname).text = modelUser.name
            dialogFA.findViewById<TextView>(R.id.tvCnic).text =modelUser.email
            dialogFA.findViewById<TextView>(R.id.tvaddress).text = modelUser.password

            dialogFA.show()
        }
}