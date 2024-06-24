package com.ssc.projob.ui.my_resume

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ssc.projob.R

class SlideshowFragment : Fragment() {

    private lateinit var educationContainer: LinearLayout
    private lateinit var experienceContainer: LinearLayout
    private lateinit var awardContainer: LinearLayout

    private lateinit var tvDocumentName: TextView
    private lateinit var browseButton: Button
    private var educationCount = 1
    private var experienceCount = 1
    private var AwardCount = 1

    private lateinit var documentPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        educationContainer = root.findViewById(R.id.llEducationContainer)
        experienceContainer = root.findViewById(R.id.llExperienceContainer)
        awardContainer = root.findViewById(R.id.llAwardsContainer)

        tvDocumentName = root.findViewById(R.id.tvDocumentName)
        browseButton = root.findViewById(R.id.btnBrowse)

        browseButton.setOnClickListener { browseDocument() }

        val btnAddEducation: Button = root.findViewById(R.id.btnAddEducation)
        btnAddEducation.setOnClickListener { addEducationField() }

        val btnAddExperience: Button = root.findViewById(R.id.btnAddExperience)
        btnAddExperience.setOnClickListener { addExperienceField() }

        val btnAddAward: Button = root.findViewById(R.id.btnAddAward)
        btnAddAward.setOnClickListener { addAwardField() }

        documentPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri: Uri? = result.data!!.data
                uri?.let {
                    val fileName = getFileName(it)
                    tvDocumentName.text = fileName
                }
            }
        }

        return root
    }

    private fun browseDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        documentPickerLauncher.launch(intent)
    }

    private fun getFileName(uri: Uri): String {
        var result = ""
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return result
    }

    private fun addEducationField() {
        educationCount++
        val newEducationView = LayoutInflater.from(context).inflate(R.layout.education_section_layout, educationContainer, false)

        newEducationView.findViewById<TextView>(R.id.educationHeader).text = "Education $educationCount"
        newEducationView.findViewById<Button>(R.id.btnDeleteEducation).setOnClickListener {
            educationContainer.removeView(newEducationView)
        }

        educationContainer.addView(newEducationView)
    }

    private fun addExperienceField() {
        experienceCount++
        val newExperienceView = LayoutInflater.from(context).inflate(R.layout.experience_section_layout, experienceContainer, false)

        newExperienceView.findViewById<TextView>(R.id.experienceHeader).text = "Experience $experienceCount"
        newExperienceView.findViewById<Button>(R.id.btnDeleteExperience).setOnClickListener {
            experienceContainer.removeView(newExperienceView)
        }

        experienceContainer.addView(newExperienceView)
    }
    private fun addAwardField() {
        AwardCount++
        val newAwardView = LayoutInflater.from(context).inflate(R.layout.award_section_layout, awardContainer, false)

        newAwardView.findViewById<TextView>(R.id.awardHeader).text = "Award $AwardCount"
        newAwardView.findViewById<Button>(R.id.btnDeleteAward).setOnClickListener {
            awardContainer.removeView(newAwardView)
        }

        awardContainer.addView(newAwardView)
    }
}
