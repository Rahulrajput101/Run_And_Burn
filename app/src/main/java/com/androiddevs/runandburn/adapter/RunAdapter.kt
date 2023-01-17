package com.androiddevs.runandburn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.runandburn.databinding.ItemRunBinding
import com.androiddevs.runandburn.db.Run
import com.androiddevs.runandburn.utlis.TrackingUtility
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class RunAdapter : RecyclerView.Adapter<RunAdapter.MyRunViewHolder>() {


    val differCallBack = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
          return oldItem.hashCode() == newItem.hashCode()
        }
    }

     val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRunViewHolder {
      return  MyRunViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyRunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    class MyRunViewHolder(private val binding : ItemRunBinding)  : RecyclerView.ViewHolder(binding.root){

        companion object{
            fun from(parent: ViewGroup) : MyRunViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding =ItemRunBinding.inflate(inflater,parent,false)
                return MyRunViewHolder(binding)
            }
        }

        fun bind(run: Run){
            Glide.with(binding.root).load(run.image).into(binding.imageView)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            binding.timeStampText.text =dateFormat.format(calendar.time)
            val avgSpeed ="${run.avgSpeedInKMH}km/h"
            binding.avgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters/1000f}km"
            binding.distanceText.text = distanceInKm

            val caloriesburn = "${run.caloriesBurned}kcal"
            binding.caloriesText.text = caloriesburn
            val time = TrackingUtility.getformattedStopWatchTime(run.timeInMillis)
            binding.timeShowText.text =time
        }



    }

}