package com.scm.sch_cafeteria_manager.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.databinding.FragmentCameraBinding
import com.scm.sch_cafeteria_manager.util.RotateBitmap
import java.io.File
import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
import com.scm.sch_cafeteria_manager.util.utilAll.weekFilePath
import java.util.Objects.isNull

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val args: CameraFragmentArgs by navArgs()

    private var imageCapture: ImageCapture? = null
    private var capturedPhotoFile: File? = null  // 저장할 파일 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack() // Fragment A로 돌아가기
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        startCamera()
        setCaptureBtn()
        setBack()
    }

    private fun setCaptureBtn() {
        Log.e("CameraFragment", "setCaptureBtn")

        with(binding) {
            // 취소 버튼 비활성화
            btnPhotoCancel.visibility = View.GONE
            btnPhotoCancel.focusable = View.NOT_FOCUSABLE
            // 저장 버튼 비활성화
            btnPhotoSave.visibility = View.GONE
            btnPhotoSave.focusable = View.NOT_FOCUSABLE
            // 촬영 버튼 활성화
            btnPhotoCapture.visibility = View.VISIBLE
            btnPhotoCapture.focusable = View.FOCUSABLE

            // 촬영 클릭 시
            btnPhotoCapture.setOnClickListener {
                // 화면 캡쳐 화면 상태로 대기
                takePhoto()
            }
        }
    }

    private fun setCancleBtn() {
        Log.e("CameraFragment", "setCancleBtn")

        with(binding) {
            // 촬영 버튼 비활성화
            btnPhotoCapture.visibility = View.GONE
            btnPhotoCapture.focusable = View.NOT_FOCUSABLE
            // 취소 버튼 활성화
            btnPhotoCancel.visibility = View.VISIBLE
            btnPhotoCancel.focusable = View.FOCUSABLE
            // 저장 버튼 활성화
            btnPhotoSave.visibility = View.VISIBLE
            btnPhotoSave.focusable = View.FOCUSABLE

            // 취소 클릭 시
            btnPhotoCancel.setOnClickListener {
                ivCamera.visibility = View.GONE
                viewCamera.visibility = View.VISIBLE
                deleteCacheFile()
                setCaptureBtn()
            }
        }
        setSaveBtn()
    }

    // 저장 버튼
    private fun setSaveBtn() {
        Log.e("CameraFragment", "setSaveBtn")
        binding.btnPhotoSave.setOnClickListener {
            // 캐시가 있으면 true
            if (isNull(isCacheFileExists())) {
                Toast.makeText(requireContext(), "사진을 찍지 않았습니다.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "사진 저장 완료", Toast.LENGTH_LONG).show()
                returnToAdminFragment()
            }
        }
    }

    // 카메라를 세팅하여 PreviewView에 실행
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // 카메라의 수명 주기를 수명 주기 소유자에게 바인딩
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewCamera.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // 빠른 촬영 모드
                .setTargetRotation(requireView().display.rotation) // 회전 설정
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
                Log.e("CameraFragment", "CameraX started successfully")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 찍은 사진 저장
    private fun takePhoto() {
        Log.e("CameraFragment", "imageCapture: $imageCapture")
        //imageCapture 가 null 아니라도 바인딩이 실패했을 수 있으므로 체크
        if (imageCapture == null) {
            startCamera()
            return
        }

        // TODO: Admin1StaffWeekFragment - Fragment 간 이동 Test
        val path: String = weekFilePath
//            if (args.flag) {
//                photoFilePath
//
//            } else {
//                weekFilePath
//            }

        val photoFile = File(requireContext().externalCacheDirs?.firstOrNull(), path)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        Log.e("CameraFragment", "capturedPhotoFile: $capturedPhotoFile")

        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            imageCapture?.takePicture(outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults?) {
                        try {
                            capturedPhotoFile = photoFile  // 파일 저장
                            Log.e("CameraFragment", "capturedPhotoFile: $capturedPhotoFile")
                            displayCapturedPhoto(photoFile)  // 미리보기 표시
                            setCancleBtn()
                        } catch (e: Exception) {
                            Log.e("CameraFragment", "onImageSaved 중 실패", e)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraFragment", "사진 저장 실패: ", exception)
                    }
                })
        }
    }

    private fun displayCapturedPhoto(photoFile: File) {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val exif = ExifInterface(photoFile)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val b = RotateBitmap(bitmap, orientation)

        with(binding) {
            ivCamera.setImageBitmap(b)
            ivCamera.visibility = View.VISIBLE
            viewCamera.visibility = View.GONE  // 사진이 보이도록 프리뷰 숨김
        }
    }

    // 캐시 확인
    private fun isCacheFileExists(): Boolean {
        // TODO: Admin1StaffWeekFragment - Fragment 간 이동 Test
//        val file: File = getFile(args.flag)
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)

        return file.exists()
    }

    // 사진 삭제
    private fun deleteCacheFile(): Boolean {
        try {
            // TODO: Admin1StaffWeekFragment - Fragment 간 이동 Test
//            val file: File = getFile(args.flag)
            val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)

            file.delete()
        } catch (e: Exception) {
            Log.e("CameraFragment", "사진 삭제 실패: ", e)
        }
        return isCacheFileExists()
    }

    private fun getFile(flag: Boolean): File {
        val file: File =
            if (flag)
                File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
            else
                File(requireContext().externalCacheDirs?.firstOrNull(), weekFilePath)
        return file
    }

    private fun returnToAdminFragment() {
        setFragmentResult("weekCamera", bundleOf("resultKey" to "dataReceived"))
        backToHome() // Fragment A로 돌아가기
    }


    // <editor-folder desc="setBack">
    private fun setBack() {
        binding.toolbarCamera.setNavigationOnClickListener {
            // 저장을 누르지 않았을 경우 경고 후 Back
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("뒤로가기 시 저장이 되지 않습니다.\n관리자 홈 화면으로 돌아가시겠습니까?")
                .setNegativeButton("취소") { _, _ ->
                    // 취소 시 아무 액션 없음
                }
                .setPositiveButton("확인") { _, _ ->
                    Log.e("CameraFragment", "사진 삭제")
                    cancleBackToHome()
                }
                .show()
        }
    }

    private fun backToHome() {
        //requireActivity().supportFragmentManager.popBackStack() // Fragment A로 돌아가기
        // TODO: Admin1StaffWeekFragment - Fragment 간 이동 Test
        findNavController().navigateUp()
    }

    private fun cancleBackToHome() {
        if (deleteCacheFile()) {
            backToHome()
        } else {
            backToHome()
        }
    }
    // </editor-folder>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        ProcessCameraProvider.getInstance(requireContext()).get()?.unbindAll() // 카메라 해제
        _binding = null
    }
}