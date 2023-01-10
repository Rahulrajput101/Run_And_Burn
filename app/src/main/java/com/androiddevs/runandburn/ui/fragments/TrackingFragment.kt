package com.androiddevs.runandburn.ui.fragments

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.androiddevs.runandburn.service.TrackingService
import com.androiddevs.runandburn.databinding.FragmentTrackingBinding
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.ACTION_START_OR_RESUME
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class TrackingFragment : Fragment() {

    private lateinit var binding : FragmentTrackingBinding
    private  var mapView: MapView? =null

    private  val viemodel : MainViewModel by viewModels()
    private var map : GoogleMap? =null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST,)
        // Inflate the layout for this fragment
        binding = FragmentTrackingBinding.inflate(inflater)
        mapView = binding.mapView


        mapView?.onCreate(savedInstanceState)

       mapView?.getMapAsync{
          map =it
         }

        binding.btnToggleRun.setOnClickListener{
            if(TrackingUtility.hasNotifcationPermission(requireContext())){
                Intent(requireContext(),TrackingService::class.java).also {
                    it.action = ACTION_START_OR_RESUME
                    requireContext().startService(it)
                    Timber.d(" runjjjjj")
                }
            }else{
              //Toast.makeText(requireContext(),"Allow the Notification in setting for using this feature", Toast.LENGTH_LONG
              //).show()
                EasyPermissions.requestPermissions(
                    this,
                    "Allow the Notification for using this feature",
                    1,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }

        }


        return binding.root
    }


    private fun sendCommandToService(action : String){

        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
       mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
       mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
    }


}