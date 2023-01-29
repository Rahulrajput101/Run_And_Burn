package com.androiddevs.runandburn.ui.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.service.TrackingService
import com.androiddevs.runandburn.databinding.FragmentTrackingBinding
import com.androiddevs.runandburn.db.Run
import com.androiddevs.runandburn.service.Polyline
import com.androiddevs.runandburn.service.Polylines
import com.androiddevs.runandburn.service.TrackingService.Companion.isTracking
import com.androiddevs.runandburn.service.TrackingService.Companion.pathPoints
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.runandburn.utlis.Constants.ACTION_START_OR_RESUME
import com.androiddevs.runandburn.utlis.Constants.ACTION_STOP_SERVICE
import com.androiddevs.runandburn.utlis.Constants.POLYLINE_COLOR
import com.androiddevs.runandburn.utlis.Constants.POLYLINE_WIDTH
import com.androiddevs.runandburn.utlis.Constants.ZOOM_TO
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(),MenuProvider {

    private lateinit var binding : FragmentTrackingBinding
    private  var mapView: MapView? = null

    private  val viewModel : MainViewModel by viewModels()
    private var map : GoogleMap? =null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var currentTimeMillis =0L

    @set:Inject
    var weight = 55f

    private var menu : Menu? = null


    // @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrackingBinding.inflate(inflater)
        mapView = binding.mapView

         val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        mapView?.onCreate(savedInstanceState)

       mapView?.getMapAsync{
          map =it
           addAllPolylines()
         }


        binding.btnToggleRun.setOnClickListener{
            if(TrackingUtility.gpsIsOnOrOff(requireContext())){
                toogleRun()
            }else{

                val gpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(gpsIntent)
            }

        }


        binding.btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()

            saveRunToDatabase()
          }


       subscribeToObserves()



        return binding.root
    }



    private fun subscribeToObserves(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTrackingButton(it)
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
            menu?.getItem(0)?.isVisible=true
        }else{
            binding.tvTimer.text = "00:00:00:00"
            sendCommandToService(ACTION_START_OR_RESUME)
        }

    }

    private fun updateTrackingButton(isTracking : Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "Start"
            if(currentTimeMillis>3000){
                binding.btnFinishRun.visibility = View.VISIBLE
            }

        }else{
            binding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible=true
            binding.btnFinishRun.visibility= View.GONE
        }
    }



    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }


        map?.moveCamera(

            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView!!.width,
                mapView!!.height,
                (mapView!!.height * 0.15f).toInt()
            )
        )

    }

    private fun saveRunToDatabase(){
        map?.snapshot {  bmp->
          var distanceInMeters =0
            for(polyline in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val avgSpeed = round((distanceInMeters/1000f) / (currentTimeMillis/1000f/60/60) *10)/10f
            val caloriesBurned =((distanceInMeters/1000f) * weight).toInt()
            val run = Run(bmp,dateTimeStamp,avgSpeed,distanceInMeters,currentTimeMillis,caloriesBurned)
            viewModel.insert(run)
            Snackbar.make(
               requireView(),
            "Your run data is saved successfully",
            Snackbar.LENGTH_LONG).show()
            stopRun()
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

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        if(TrackingUtility.hasNotifcationPermission(requireContext()) && TrackingUtility.hasLocationPermission(requireContext())){
            Intent(requireContext(), TrackingService::class.java).also {
                it.action = action
                requireContext().startService(it)
            }
        }else{
            findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
        }
    }else{
        if(TrackingUtility.hasLocationPermission(requireContext())){
            Intent(requireContext(), TrackingService::class.java).also {
                it.action = action
                requireContext().startService(it)
            }
        }else{

            findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())


        }

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


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.tracking_menu,menu)
        this.menu = menu

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
     when(menuItem.itemId){
         R.id.miCancelTracking ->{
             showCancelDailog()
         }
     }
       return true
    }

    private fun showCancelDailog(){
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel the Run")
            .setMessage(" Are you sure to cancel the run")
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Yes") {_,_->
                stopRun()
            }
            .setNegativeButton("No"){ dialogInterface,_ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }


   private fun stopRun(){
       sendCommandToService(ACTION_STOP_SERVICE)
       findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
    }





}