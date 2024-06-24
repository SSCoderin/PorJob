package com.ssc.projob.ui.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ssc.projob.R
import com.ssc.projob.databinding.FragmentGalleryBinding
import java.util.*

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var networkCount = 1 // Initial count of network fields

    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupHeaderClicks()

        // Display the Profile page by default
        displayProfilePage()

        return root
    }

    private fun setupHeaderClicks() {
        val profileHeader = binding.tvProfileHeader
        val passwordHeader = binding.tvPasswordHeader
        val logoutHeader = binding.tvLogoutHeader

        profileHeader.setOnClickListener {
            displayProfilePage()
            highlightSelectedHeader(profileHeader)
        }

        passwordHeader.setOnClickListener {
            displayPasswordPage()
            highlightSelectedHeader(passwordHeader)
        }

        logoutHeader.setOnClickListener {
            displayLogoutPage()
            highlightSelectedHeader(logoutHeader)
        }
    }

    private fun highlightSelectedHeader(selectedHeader: TextView) {
        binding.tvProfileHeader.setBackgroundResource(0)
        binding.tvPasswordHeader.setBackgroundResource(0)
        binding.tvLogoutHeader.setBackgroundResource(0)

        selectedHeader.setBackgroundResource(R.drawable.edit_text_background1)
    }

    private fun displayProfilePage() {
        binding.layoutContainer.removeAllViews()
        val profileView = layoutInflater.inflate(R.layout.profile_layout, binding.layoutContainer, false)

        setupProfileView(profileView)

        binding.layoutContainer.addView(profileView)
    }

    private fun displayPasswordPage() {
        binding.layoutContainer.removeAllViews()
        val passwordView = layoutInflater.inflate(R.layout.password_layout, binding.layoutContainer, false)

        binding.layoutContainer.addView(passwordView)
    }

    private fun displayLogoutPage() {
        binding.layoutContainer.removeAllViews()
        val logoutView = layoutInflater.inflate(R.layout.logout_layout, binding.layoutContainer, false)

        binding.layoutContainer.addView(logoutView)
    }

    private fun setupProfileView(profileView: View) {
        val btnBrowse: Button = profileView.findViewById(R.id.btnBrowse)
        val imgProfile: ImageView = profileView.findViewById(R.id.imgProfile)
        val etDateOfBirth: EditText = profileView.findViewById(R.id.etDateOfBirth)
        val spinnerGender: Spinner = profileView.findViewById(R.id.spinnerGender)
        val etAge: Spinner = profileView.findViewById(R.id.spinnerAge)
        val btnAddNumber: Button = profileView.findViewById(R.id.btnAddNumber)
        val spinnerQualification: Spinner = profileView.findViewById(R.id.spinnerQualification)
        val spinnerExperienceTime: Spinner = profileView.findViewById(R.id.spinnerExperienceTime)
        val spinnerSalaryType: Spinner = profileView.findViewById(R.id.spinnerSalaryType)
        val spinnerIndustry: Spinner = profileView.findViewById(R.id.spinnerIndustry)
        val spinnerLanguage: Spinner = profileView.findViewById(R.id.spinnerLanguage)
        val btnAddNetwork: Button = profileView.findViewById(R.id.btnAddNetwork)

        // Browse button to select and display a profile image
        btnBrowse.setOnClickListener {
            pickImageFromGallery()
        }

        // Date of Birth picker
        etDateOfBirth.setOnClickListener {
            showDatePicker(etDateOfBirth)
        }

        // Gender dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }

        // Age dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.age_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            etAge.adapter = adapter
        }

        // Specialization dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.qualification_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerQualification.adapter = adapter
        }

        // Experience Time dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.experience_time_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerExperienceTime.adapter = adapter
        }

        // Salary Type dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.salary_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSalaryType.adapter = adapter
        }

        // Industry dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.industry_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerIndustry.adapter = adapter
        }

        // Language dropdown setup
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLanguage.adapter = adapter
        }

        // Add extra phone number input field
        btnAddNumber.setOnClickListener {
            addExtraPhoneNumber(profileView)
        }

        // Add additional network fields
        btnAddNetwork.setOnClickListener {
            addNetworkFields(profileView)
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun addExtraPhoneNumber(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.extraPhoneContainer)
        val extraPhoneNumberView = layoutInflater.inflate(R.layout.extra_phone_number_layout, container, false)

        val btnRemove = extraPhoneNumberView.findViewById<Button>(R.id.btnRemove)
        btnRemove.setOnClickListener {
            container.removeView(extraPhoneNumberView)
        }

        container.addView(extraPhoneNumberView)
    }

    private fun addNetworkFields(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.networkContainer)
        val networkView = layoutInflater.inflate(R.layout.network_layout, container, false)

        // Update the network count text
        val tvNetworkCount = networkView.findViewById<TextView>(R.id.tvNetworkCount)
        tvNetworkCount.text = "Network ${networkCount++}:" // Increment the count

        val btnRemove = networkView.findViewById<Button>(R.id.btnRemoveNetwork)
        btnRemove.setOnClickListener {
            container.removeView(networkView)
        }

        container.addView(networkView)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data!!
            // Find imgProfile within profileView
            val profileView = binding.layoutContainer.getChildAt(0) // Assuming profileView is the first child
            val imgProfile: ImageView = profileView.findViewById(R.id.imgProfile)
            imgProfile.setImageURI(uri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
