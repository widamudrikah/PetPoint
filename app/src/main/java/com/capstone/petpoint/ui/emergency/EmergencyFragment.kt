package com.capstone.petpoint.ui.emergency

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.capstone.petpoint.MainActivity
import com.capstone.petpoint.R
import com.capstone.petpoint.databinding.FragmentEmergencyBinding
import com.capstone.petpoint.response.PostEmergencyResponse
import com.capstone.petpoint.ui.myreport.MyReportFragment
import com.capstone.petpoint.utils.ApiService
import com.capstone.petpoint.utils.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

//        inisialisasi button
        binding.iconCamera.setOnClickListener{ startGallery()}
        binding.iconSetLocation.setOnClickListener{ getLocation() }
        binding.btnConfirm.setOnClickListener { uploadEmergency() }
    }


    //     Start Image picker
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            val imageView = binding.inputPhoto.findViewById<ImageView>(R.id.selected_image)
            imageView.setImageURI(it)
            imageView.visibility = View.VISIBLE

            binding.iconCamera.visibility = View.GONE
            binding.textTakePicture.visibility = View.GONE
        }
    }
//    End Image picker

//    Start Location
    private fun getLocation() {
        if (!checkLocationEnabled()) {
            binding.edtSetLocation.setText("Aktifkan layanan lokasi di perangkat")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Minta izin jika belum diberikan
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Ambil lokasi terakhir
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000L
            ).build()

            fusedLocationClient.requestLocationUpdates(locationRequest, object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val longitude = location.longitude
                        val latitude = location.latitude
                        showLocation(latitude, longitude)
                    } else {
                        binding.edtSetLocation.setText("Unable to fetch location")
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.getMainLooper())
                .addOnFailureListener { exception ->
                    Log.e("EmergencyFragment", "Error fetching location", exception)
                    binding.edtSetLocation.setText("Error fetching location")
                }
        }
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocation(longitude: Double, latitude: Double){
        binding.edtSetLocation.setText("$longitude, $latitude")
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getLocation()
        } else {
            binding.edtSetLocation.setText("Aktifkan Lokasi")
        }
    }
//    End location

//    Uploada Emergency
    private fun uploadEmergency() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "ShowImage: ${imageFile.path}")
            val location = binding.edtSetLocation.text.toString()
            val notes = binding.edtNote.text.toString()

            val petCategory = when (binding.selectPet.checkedRadioButtonId) {
                R.id.option_cat -> "kucing"
                R.id.option_dog -> "anjing"
                else -> "Uknow"
            }

            // Validasi input
            if (petCategory.isBlank() || location.isBlank() || notes.isBlank() || imageFile == null) {
                if (petCategory.isBlank()) Log.e("EmergencyFragment", "Pet category is empty!")
                if (location.isBlank()) Log.e("EmergencyFragment", "Pet location is empty!")
                if (notes.isBlank()) Log.e("EmergencyFragment", "Notes are empty!")
                if (imageFile == null) Log.e("EmergencyFragment", "No file selected!")
                return
            }


            val requestBodyLocation = location.toRequestBody("text/plain".toMediaType())
            val requestBodyNotes = notes.toRequestBody("text/plain".toMediaType())
            val requestBodyPetCategory = petCategory.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "pic_pet",
                imageFile.name,
                requestImageFile
            )

            val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            Log.d("EmergencyFragment", "Token retrieved: $token")

            if (token.isNullOrEmpty()) {
                Log.e("uploadEmergency", "Token Not Found")
            }

            lifecycleScope.launch {
                try {
                    val apiService = RetrofitInstance.api
                    val successResponse = apiService.uploadEmergency(
                        "Bearer $token",
                        multipartBody,
                        requestBodyLocation,
                        requestBodyNotes,
                        requestBodyPetCategory
                    )
                    successResponse.message?.let { showToast(it) }
                    val navConroller = findNavController()
                    navConroller.navigate(R.id.navigation_my_report)
                    (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.mobile_navigation).menu.findItem(R.id.navigation_my_report).isChecked = true


                } catch (e: HttpException) {
                    Log.e("EmergencyFragment", "HttpException: ${e.response()?.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("EmergencyFragment", "Exception: ${e.message}")
                }
            }
        } ?: showToast("Ada kesalahan")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}