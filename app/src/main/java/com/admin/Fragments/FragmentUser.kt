package com.admin.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.admin.Adapter.AdapterUser
import com.admin.Adapter.UserViewPagerAdapter
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelUser
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.databinding.FragmentUserBinding

class FragmentUser : Fragment() {
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
/*        setUserAdapter()


        binding.floatingaction.setOnClickListener {
           showChoiceDialog()
        }*/
/*

binding.admin.setOnClickListener(
)
{
    setAdminAdapter()
}
binding.user.setOnClickListener(
)
{
    setUserAdapter()
}
*/

        setupViewPager()
        setupTabLayout()

        return root
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if(position==0) tab.text ="User"
            else if(position==1) tab.text="Admin"
            else if(position==2) tab.text="Groups"
        }.attach()
    }

    private fun setupViewPager() {
        val adapter = UserViewPagerAdapter(requireActivity(), 3)
        binding.viewPager.adapter = adapter
    }





/*


private  fun showChoiceDialog()
{


    val builder = AlertDialog.Builder(mContext)
if(sharedPrefManager.getAdmin().role.equals("Owner"))
{
    builder.setTitle("Select Option")
        .setPositiveButton("User") { dialog, which ->
            showDialogAdduser()
        }
        .setNegativeButton("Admin") { dialog, which ->
            showDialogAddAdmin()
        }
}
    else
{
    builder.setTitle("Select Option")
        .setPositiveButton("User") { dialog, which ->
            showDialogAdduser()
        }
        .setNegativeButton("Cancel") { dialog, which ->
        dialog.dismiss()
        }
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
        // Show a dialog to display and potentially edit the admin details
        val dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_add_user) // Replace with your dialog layout

        // Find views in the dialog layout
        val etAdminName = dialog.findViewById<EditText>(R.id.userName)
        val editTextEmail = dialog.findViewById<EditText>(R.id.userEmail)
        val editTextPassword = dialog.findViewById<EditText>(R.id.userPassword)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnNext) // Update button ID corrected

        // Populate dialog fields with admin details
        etAdminName.setText(modelUser.name)
        editTextEmail.setText(modelUser.email)
        editTextPassword.setText(modelUser.password)


        btnUpdate.setOnClickListener {
            // Get updated details from the dialog fields
            val updatedName = etAdminName.text.toString()
            val updatedEmail = editTextEmail.text.toString()
            val updatedPassword = editTextPassword.text.toString()

            // Update modelUser with the modified details
            modelUser.name = updatedName
            modelUser.email = updatedEmail
            modelUser.password = updatedPassword

            // Call the function to update admin
            updateUser(modelUser, dialog)
        }

        // Show the dialog
        dialog.show()
    }

    // Function to update the admin
    private fun updateUser(modelUser: ModelUser, dialog: Dialog) {
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.updateUser(modelUser)
                .observe(this@FragmentUser) { task ->
                    dialog.dismiss() // Dismiss the dialog before showing the result
                    if (task) {
                        utils.endLoadingAnimation()
                        setUserAdapter()
                        Toast.makeText(mContext, "User Data Updated Successfully!!", Toast.LENGTH_SHORT).show()
                    } else {
                        setUserAdapter()
                        utils.endLoadingAnimation()
                        Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }














    override fun onDeleteClick(modelUser: ModelUser) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete?")
            .setPositiveButton("Yes") { _, _ ->

                lifecycleScope.launch {
                    utils.startLoadingAnimation()
                    userViewModel.deleteUser(modelUser)
                        .observe(this@FragmentUser)
                        { task->
                            if(task)
                            {
                                utils.endLoadingAnimation()
                                Toast.makeText(mContext, "User deleted Successfully", Toast.LENGTH_SHORT).show()
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



    override fun onitemclick(modelUser: ModelUser) {
        val intent = Intent(mContext, ActivityManageVideo::class.java)
        intent.putExtra("user", modelUser.toString()) // Serialize to JSON
        mContext.startActivity(intent)
    }

    override fun onViewClick(modelUser: ModelUser) {
        var dialogFA=Dialog(requireContext())
        dialogFA.setContentView(R.layout.dialogdetail)
        dialogFA.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFA.setCancelable(true)
        dialogFA.findViewById<TextView>(R.id.tvRole).visibility=View.GONE
        dialogFA.findViewById<TextView>(R.id.startb).visibility=View.GONE
        dialogFA.findViewById<TextView>(R.id.closeb).visibility=View.GONE
        dialogFA.findViewById<TextView>(R.id.title).text="Here is the information of User!!"
        dialogFA.findViewById<TextView>(R.id.choice).text="User"
        dialogFA.findViewById<TextView>(R.id.tvFname).text = modelUser.name
        dialogFA.findViewById<TextView>(R.id.tvCnic).text =modelUser.email
        dialogFA.findViewById<TextView>(R.id.tvaddress).text = modelUser.password

        dialogFA.show()
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

        var list= ArrayList<ModelUser> ()
        lifecycleScope.launch {
            utils.startLoadingAnimation()
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


                        }
                        binding.rvusers.layoutManager = LinearLayoutManager(mContext)
                        binding.rvusers.adapter= AdapterUser("User",list,this@FragmentUser)

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
                            binding.rvusers.adapter= AdapterAdmin(mContext,list,this@FragmentUser)

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

    // Assuming you have a function to handle item clicks in your admin list
    override fun onAdminupdateclick(modelUser: Admin) {
        // Show a dialog to display and potentially edit the admin details
        val dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_admin) // Replace with your dialog layout

        // Find views in the dialog layout
        val etAdminName = dialog.findViewById<EditText>(R.id.etAdminName)
        val editTextEmail = dialog.findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = dialog.findViewById<EditText>(R.id.editTextPassword)
        val spinnerRole = dialog.findViewById<Spinner>(R.id.spinnerRole)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnAddAdmin) // Update button ID corrected
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

    // Function to update the admin
    private fun updateAdmin(modelUser: Admin, dialog: Dialog) {
        lifecycleScope.launch {
            utils.startLoadingAnimation()
            userViewModel.updateAdmin(modelUser)
                .observe(this@FragmentUser) { task ->
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


    override fun onAdminDeleteClick(modelUser: Admin) {
        lifecycleScope.launch {
            utils.startLoadingAnimation()
userViewModel.deletAdmin(modelUser)
    .observe(this@FragmentUser)
    {
        task->
        if(task)
        {
            utils.endLoadingAnimation()
            Toast.makeText(mContext, "Admin deleted Successfully", Toast.LENGTH_SHORT).show()
            setAdminAdapter()
        }
        else
        {
            utils.endLoadingAnimation()
            Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
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
*/

}