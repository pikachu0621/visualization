package com.pikachu.visualization

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pkpk.zaudio.view.VisualizationAudioAdapter

class AudioTypeViewAdapter(
    private val arrAudioTypeData: List<AudioTypeData>,
    private val itemClick: (audioTypeData: AudioTypeData) -> Unit
) : RecyclerView.Adapter<AudioTypeViewAdapter.AudioTypeViewHolder>() {

    data class AudioTypeData(
        val name: String,
        val clazz: Class<out VisualizationAudioAdapter>
    )

    class AudioTypeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var text: TextView
        init {
            text = itemView.findViewById(R.id.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioTypeViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_adapter_click, parent, false)
        return AudioTypeViewHolder(inflate)
    }

    override fun getItemCount(): Int = arrAudioTypeData.size

    override fun onBindViewHolder(holder: AudioTypeViewHolder, position: Int) {
        val audioTypeData = arrAudioTypeData[position]
        holder.text.text = audioTypeData.name
        holder.text.setOnClickListener {
            itemClick(audioTypeData)
        }
    }

}