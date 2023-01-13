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
import androidx.lifecycle.Observer
import com.androiddevs.runandburn.service.TrackingService
import com.androiddevs.runandburn.databinding.FragmentTrackingBinding
import com.androiddevs.runandburn.service.Polyline
import com.androiddevs.runandburn.service.Polylines
import com.androiddevs.runandburn.service.TrackingService.Companion.isTracking
import com.androiddevs.runandburn.service.TrackingService.Companion.pathPoints
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.runandburn.utlis.Constants.ACTION_START_OR_RESUME
import com.androiddevs.runandburn.utlis.Constants.POLYLINE_COLOR
import com.androiddevs.runandburn.utlis.Constants.POLYLINE_WIDTH
import com.androiddevs.runandburn.utlis.Constants.ZOOM_TO
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class TrackingFragment : Fragment() {

    private lateinit var binding : FragmentTrackingBinding
    private  var mapView: MapView? =null

    private  val viemodel : MainViewModel by viewModels()
    private var map : GoogleMap? =null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var currentTimeMillis =0L



   // @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
           addAllPolylines()
         }

        binding.btnToggleRun.setOnClickListener{
            toogleRun()
        }
       subscribeToObserves()

        return binding.root
    }

    private fun subscribeToObserves(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer{
            pathPoints = it
            addLatestPolyline()
            moveFocus()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getformattedStopWatchTime(currentTimeMillis,true)
            binding.tvTimer.text = formattedTime
        })
    }


   // @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun toogleRun(){
        if(isTracking){
              sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME)
        }

    }

    private fun updateTracking(isTracking : Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility= View.GONE
        }
    }

    private fun moveFocus(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    ZOOM_TO
                )
            )
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }


    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size >1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polyOptions = PolylineOptions().color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
                map?.addPolyline(polyOptions)
        }

    }


    //@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendCommandToService(action: String) {

        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
            Timber.d(" runjjjjj")
//
//        if (TrackingUtility.hasNotifcationPermission(requireContext())) {
//            Intent(requireContext(), TrackingService::class.java).also {
//                it.action = action
//                requireContext().startService(it)
//                Timber.d(" runjjjjj")
//            }
//        } else {
//            //Toast.makeText(requireContext(),"Allow the Notification in setting for using this feature", Toast.LENGTH_LONG
//            //).show()
//            EasyPermissions.requestPermissions(
//                this,
//                "Allow the Notification for using this feature",
//                1,
//                android.Manifest.permission.POST_NOTIFICATIONS
//            )
//        }

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