package com.example.solutionchallenge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class RecordStep1Fragment : Fragment() {

    private var morningUri: Uri? = null
    private var lunchUri: Uri? = null
    private var dinnerUri: Uri? = null
    private var snackUri: Uri? = null

    private var currentMealType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_record_step1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btnBreakfast).setOnClickListener {
            currentMealType = "morning"
            openGallery()
        }

        view.findViewById<Button>(R.id.btnLunch).setOnClickListener {
            currentMealType = "lunch"
            openGallery()
        }

        view.findViewById<Button>(R.id.btnDinner).setOnClickListener {
            currentMealType = "dinner"
            openGallery()
        }

        view.findViewById<Button>(R.id.btnSnack).setOnClickListener {
            currentMealType = "snack"
            openGallery()
        }

        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            val bundle = Bundle().apply {
                putString("imageUriMorning", morningUri.toString())
                putString("imageUriLunch", lunchUri.toString())
                putString("imageUriDinner", dinnerUri.toString())
                putString("imageUriSnack", snackUri.toString())
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecordStep2Fragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        checkIfAllSelected()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return

            when (currentMealType) {
                "morning" -> {
                    morningUri = uri
                    requireView().findViewById<ImageView>(R.id.imageView1).setImageURI(uri)
                }
                "lunch" -> {
                    lunchUri = uri
                    requireView().findViewById<ImageView>(R.id.imageView2).setImageURI(uri)
                }
                "dinner" -> {
                    dinnerUri = uri
                    requireView().findViewById<ImageView>(R.id.imageView3).setImageURI(uri)
                }
                "snack" -> {
                    snackUri = uri
                    requireView().findViewById<ImageView>(R.id.imageView4).setImageURI(uri)
                }
            }

            checkIfAllSelected()
        }
    }

    private fun checkIfAllSelected() {
        val nextButton = requireView().findViewById<Button>(R.id.nextButton)
        nextButton.isEnabled = morningUri != null && lunchUri != null && dinnerUri != null && snackUri != null
    }
}
