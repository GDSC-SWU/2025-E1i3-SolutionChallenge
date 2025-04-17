package com.example.solutionchallenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuSelectDialogFragment : BottomSheetDialogFragment() {

    private val REQUEST_GALLERY = 100
    private val REQUEST_CAMERA = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_menu_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)

        btnGallery.setOnClickListener {
            dismiss()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        btnCamera.setOnClickListener {
            dismiss()
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }
}
