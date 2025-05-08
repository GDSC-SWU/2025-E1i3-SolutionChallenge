package com.example.solutionchallenge

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private val CAMERA_PERMISSION_CODE = 1001
    private val REQUEST_IMAGE_PICK = 1002

    private lateinit var photoFile: File
    private var savedPhotoUri: Uri? = null

    private var cameraPurpose: String = "record"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_camera_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraPurpose = arguments?.getString("cameraPurpose") ?: "record"

        viewFinder = view.findViewById(R.id.viewFinder)
        val captureButton = view.findViewById<ImageButton>(R.id.captureButton)
        val switchCameraButton = view.findViewById<ImageButton>(R.id.switchCameraButton)
        val galleryButton = view.findViewById<ImageButton>(R.id.galleryButton)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            startCamera()
        }

        captureButton.setOnClickListener {
            takePhoto()
        }

        switchCameraButton.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK
            startCamera()
        }

        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture!!)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        photoFile = File(
            requireContext().externalMediaDirs.first(),
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedPhotoUri = Uri.fromFile(photoFile)
                    Toast.makeText(requireContext(), "📸 사진 저장 완료!", Toast.LENGTH_SHORT).show()
                    navigateToNextFragment()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "촬영 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun navigateToNextFragment() {
        val fragment = if (cameraPurpose == "menu") {
            MenuScanStep1Fragment().apply {
                arguments = Bundle().apply {
                    putString("photoUri", savedPhotoUri.toString())
                }
            }
        } else {
            RecordStep1Fragment().apply {
                arguments = Bundle().apply {
                    putString("photoUri", savedPhotoUri.toString())
                }
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                savedPhotoUri = it
                Toast.makeText(requireContext(), "🖼️ 이미지 선택됨: $it", Toast.LENGTH_SHORT).show()
                navigateToNextFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
