package com.example.wodasyf

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.navArgs

class RecyclerAdapter(private val savedAlarms: SavedAlarms, val activity: MainActivity) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, private val savedAlarms: SavedAlarms, val activity: MainActivity) : RecyclerView.ViewHolder(view) {
        val id_number: TextView
        val clock_img : ImageView
        val alarm_name : TextView
        val station_name: TextView
        val alarm_enable : Switch
        val edit_button : ImageButton
        init {
            // Define click listener for the ViewHolder's View.
            id_number = view.findViewById(R.id.id_number)
            clock_img = view.findViewById(R.id.clock_img)
            alarm_name = view.findViewById(R.id.alarm_name)
            station_name = view.findViewById(R.id.station_text)
            alarm_enable = view.findViewById(R.id.alarm_enable)
            edit_button = view.findViewById(R.id.edit_button)
            alarm_enable.setOnCheckedChangeListener { _, isChecked ->
                savedAlarms.savedAlarms[id_number.text.toString().toInt()].enable = isChecked
                (activity as MainActivity).alarmHandler.setAlarmIfNecessary(true)

            }
            edit_button.setOnClickListener(){
                savedAlarms.lastEditedAlarm = id_number.text.toString().toInt()
                view.findNavController().navigate(R.id.action_FirstFragment_to_editExistingAlarm)

            }

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.alarm_list, viewGroup, false)

        return ViewHolder(view, savedAlarms, activity)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that elementsavedAlarms
            viewHolder.id_number.text = position.toString()
            viewHolder.station_name.text = savedAlarms.getAlarm(position).stationName.split(" ")[0]
            viewHolder.alarm_name.text = savedAlarms.getAlarm(position).name
            viewHolder.alarm_enable.isChecked = savedAlarms.getAlarm(position).enable
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = savedAlarms.getCount()


}
