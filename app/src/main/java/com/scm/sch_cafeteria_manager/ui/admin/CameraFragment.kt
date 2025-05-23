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
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.databinding.FragmentCameraBinding
import com.scm.sch_cafeteria_manager.util.RotateBitmap
import com.scm.sch_cafeteria_manager.util.uploadingWeekMealPlans
import com.scm.sch_cafeteria_manager.util.utilAll.dummyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.dummyMEAL1
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import java.io.File
import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
import com.scm.sch_cafeteria_manager.util.utilAll.photoFileType
import com.scm.sch_cafeteria_manager.util.utilAll.weekFilePath
import com.scm.sch_cafeteria_manager.util.utilAll.weekFileType
import kotlinx.coroutines.launch
import java.util.Objects.isNull

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val args: CameraFragmentArgs by navArgs() // true: week, false: day of week
    private var imageCapture: ImageCapture? = null
    private var capturedPhotoFile: File? = null  // 저장할 파일 변수
    private var filePath: String = ""
    private var fileType: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        if (args.flag) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        requireActivity().supportFragmentManager.popBackStack() // Fragment A로 돌아가기
                    }
                })
        }
        Log.e("CameraFragment", "Check - ${args.flag}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 카메라 권한 요청
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

    // <editor-fold desc="setLayout">
    // 촬영 버튼 활성화
    private fun setCaptureBtn() {
        Log.e("CameraFragment", "setCaptureBtn")

        with(binding) {
            // 취소 버튼 비활성화
            btnPhotoCancel.visibility = View.GONE
            // 저장 버튼 비활성화
            btnPhotoSave.visibility = View.GONE
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

    // 취소 & 저장 버튼 활성화
    private fun setCancleBtn() {
        Log.e("CameraFragment", "setCancelBtn")

        with(binding) {
            // 촬영 버튼 비활성화
            btnPhotoCapture.visibility = View.INVISIBLE
            btnPhotoCapture.focusable = View.NOT_FOCUSABLE
            // 취소 버튼 활성화
            btnPhotoCancel.visibility = View.VISIBLE
            btnPhotoCancel.focusable = View.FOCUSABLE
            // 저장 버튼 활성화
            btnPhotoSave.visibility = View.VISIBLE
            btnPhotoSave.focusable = View.FOCUSABLE
            btnPhotoSave.alpha = 1f

            // 취소 클릭 시
            btnPhotoCancel.setOnClickListener {
                ivCamera.visibility = View.GONE
                viewCamera.visibility = View.VISIBLE
                deleteCacheFile()
                setCaptureBtn()
            }
            btnPhotoSave.setOnClickListener {
                setSaveBtn()
            }
        }
    }

    // 저장 버튼 클릭 리스너
    private fun setSaveBtn() {
        Log.e("CameraFragment", "setSaveBtn")
        val title = args.manageDate.cf
        Log.e("CameraFragment", "Save - $title")
        // 캐시가 있으면 true
        if (isNull(isCacheFileExists())) {
            Log.e("CameraFragment", "setSaveBtn - if")
            Toast.makeText(requireContext(), "사진을 찍지 않았습니다.", Toast.LENGTH_LONG).show()
        } else {
            Log.e("CameraFragment", "setSaveBtn - else")
            val file = File(requireContext().externalCacheDirs?.firstOrNull(), filePath)
            if (file.exists()) {
                Log.e("CameraFragment", "setSaveBtn - else - if")
                val menu: List<dailyMeals>
                if(title == CafeteriaData.HYANGSEOL1.cfName){
                    menu = listOf(
                        dailyMeals(
                            dOw.MONDAY.engName, dummyMEAL
                        )
                    )
                } else{
                    menu = listOf(
                        dailyMeals(
                            dOw.MONDAY.engName, dummyMEAL1
                        )
                    )
                }
                Log.e("CameraFragment", "title - $title")

                binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
                binding.progressbarBackground.visibility = View.VISIBLE
                binding.progressbarBackground.isClickable = true
                lifecycleScope.launch {
                    try {
                        if (fileType == weekFileType){
                            uploadingWeekMealPlans(
                                requireContext(),
                                title,
                                getWeekStartDate(args.manageDate.day),
                                menu,
                                file
                            )
                            Toast.makeText(requireContext(), "Successful", Toast.LENGTH_LONG).show()
                        } else {
                            Log.e("CameraFragment", "setSaveBtn - photoFileType")
                        }
                    } catch (e: Exception) {
                        Log.e("CameraFragment", "setSaveBtn Error: $e")
                        Toast.makeText(
                            requireContext(),
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                        binding.progressbarBackground.visibility = View.GONE
                        Log.e("CameraFragment", "file.exists(): ${file.exists()}")
                        returnToAdminFragment()
                    }
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="setCamera">
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
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // 빠른 촬영 모드
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
        // path check
        if (args.flag) {
            filePath = weekFilePath
            fileType = weekFileType
        } else {
            filePath = photoFilePath
            fileType = photoFileType
        }
        val photoFile = File(requireContext().externalCacheDirs?.firstOrNull(), filePath)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        Log.e("CameraFragment", "capturedPhotoFile: $filePath -> $capturedPhotoFile")

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

    // 찍은 사진 display
    private fun displayCapturedPhoto(photoFile: File) {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        // 이미지 돌리기
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
    // </editor-fold>

    // 캐시 확인
    private fun isCacheFileExists(): Boolean {
        val file: File = getFile(args.flag)
        return file.exists()
    }

    // 사진 삭제
    private fun deleteCacheFile(): Boolean {
        try {
            val file: File = getFile(args.flag)
            file.delete()
        } catch (e: Exception) {
            Log.e("CameraFragment", "사진 삭제 실패: ", e)
        }
        return isCacheFileExists()
    }

    // path 확인
    private fun getFile(flag: Boolean): File {
        val file: File =
            if (flag)
                File(requireContext().externalCacheDirs?.firstOrNull(), weekFilePath)
            else
                File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        return file
    }

    // flag 확인 후 적절한 back 세팅
    private fun returnToAdminFragment() {
//        if (args.flag) setFragmentResult("weekCamera", bundleOf("resultKey" to "dataReceived"))
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
        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }

    private fun backToHome() {
//        if (args.flag)
//            requireActivity().supportFragmentManager.popBackStack() // Fragment A로 돌아가기
//        else
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

    // 권한 체크
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