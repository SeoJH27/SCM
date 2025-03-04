package com.scm.sch_cafeteria_manager.ui.admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.scm.sch_cafeteria_manager.databinding.FragmentCameraBinding
import java.io.File
import java.util.Objects.isNull

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private val reqContext = requireContext()
    private var photoTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        photoTime = takePhoto()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()
        setSaveBtn()
    }

    // 저장 버튼
    private fun setSaveBtn() {
        binding.btnPhotoSave.setOnClickListener {
            if (isNull(photoTime)) {
                Toast.makeText(reqContext, "사진을 찍지 않았습니다.", Toast.LENGTH_LONG).show()
            } else {
                // TODO: 저장된 사진을 서버로 보냄
                Toast.makeText(reqContext, "사진 전송 완료", Toast.LENGTH_LONG).show()
                backToHome()
            }
        }
    }

    // 카메라를 세팅하여 PreviewView에 실행
    fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(reqContext)

        cameraProviderFuture.addListener({
            // 카메라의 수명 주기를 수명 주기 소유자에게 바인딩
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewCamera.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraHelper", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(reqContext))
    }

    // 찍은 사진 저장
    fun takePhoto(): String {
        val time: String = System.currentTimeMillis().toString()
        val file = File(reqContext.externalCacheDirs.firstOrNull(), "$time.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(reqContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults?) {
                    Toast.makeText(reqContext, "사진 저장됨", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraHelper", "사진저장실패", exception)
                }
            })

        // 저장된 사진의 이름 리턴
        return time
    }
    
    private fun backToHome(){
        findNavController().navigate(CameraFragmentDirections.toAdminFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}