package com.androiddevs.runandburn.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.runandburn.R
import com.androiddevs.runandburn.adapter.RunAdapter
import com.androiddevs.runandburn.databinding.FragmentRunBinding
import com.androiddevs.runandburn.ui.MainActivity
import com.androiddevs.runandburn.ui.viewModels.MainViewModel
import com.androiddevs.runandburn.utlis.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.androiddevs.runandburn.utlis.SortType
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RunFragment : Fragment() {

    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: RunAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRunBinding.inflate(inflater)
        requestPermission()
        adapter = RunAdapter()
        swipeAble()
        binding.rvRuns.adapter = adapter
        binding.rvRuns.layoutManager = LinearLayoutManager(context)

        val Sadapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_options,
            R.layout.spinner_item
        )
        binding.spinner.adapter = Sadapter

        when (viewModel.sortType) {
            SortType.DATE -> binding.spinner.setSelection(0)
            SortType.AVG_SPEED -> binding.spinner.setSelection(1)
            SortType.DISTANCE -> binding.spinner.setSelection(2)
            SortType.TIME -> binding.spinner.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spinner.setSelection(4)

        }

        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> viewModel.sortRun(SortType.DATE)
                    1 -> viewModel.sortRun(SortType.AVG_SPEED)
                    2 -> viewModel.sortRun(SortType.DISTANCE)
                    3 -> viewModel.sortRun(SortType.TIME)
                    4 -> viewModel.sortRun(SortType.CALORIES_BURNED)

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        viewModel.run.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }


        return binding.root
    }








    fun swipeAble() {

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val run = adapter.differ.currentList[position]
                viewModel.delete(run)

                view?.let {
                    Snackbar.make(
                        requireView(),
                        " Successfully deleted the run ",
                        Snackbar.LENGTH_LONG
                    ).apply {
                        setAction("Undo") {
                            viewModel.insert(run)
                        }.show()

                    }
                }
            }


        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.rvRuns)
        }

    }






    val deniedPermissions = arrayListOf<String>()
    val muitiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                Timber.d("$permissionName = name and  is Granetd =$isGranted")
            }else{
                if (!shouldShowRequestPermissionRationale(permissionName)) {
                    deniedPermissions.add(permissionName)

            }

            }


        }
        if(deniedPermissions.isNotEmpty()){
            askForFinalPermission(deniedPermissions)
        }

    }


    fun requestPermission() {
        val permissionRequest = mutableListOf<String>()
        if (!TrackingUtility.fLocationPermission(requireContext()) && !TrackingUtility.cLocationPermission(
                requireContext()
            )
        ) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!TrackingUtility.notificationPermission(requireContext())) {
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionRequest.isNotEmpty()) {
            muitiplePermissions.launch(permissionRequest.toTypedArray())

        }


    }

    private fun askForFinalPermission(deniedPermissions: ArrayList<String>) {
        val message = getString(R.string.permission_denied_message, deniedPermissions.joinToString())
        val intent = Intent()
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Go to ->") { _, _ ->
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val packageName = requireContext().packageName
                //Toast.makeText(requireContext()," package:$packageName", Toast.LENGTH_LONG).show()
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)

            }
            .setNegativeButton("Deny") { dialogInterface, _ ->
                dialogInterface.dismiss()

            }.create()

        dialog.show()
    }


}