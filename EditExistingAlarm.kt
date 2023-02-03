package com.example.wodasyf

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wodasyf.databinding.EditAlarmBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class EditExistingAlarm() : Fragment() {

    private var _binding: EditAlarmBinding? = null
    val alarmList: SavedAlarms by activityViewModels()
    val webScrubber = WebScrubber()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = EditAlarmBinding.inflate(inflater, container, false)
        createSpinner()
        binding.actualWater.text = alarmList.getAlarm(alarmList.lastEditedAlarm).currentWaterLevel.toString()
        binding.waterMax.text =  Editable.Factory.getInstance().newEditable(alarmList.getAlarm(alarmList.lastEditedAlarm).maxWaterLevel.toString())
        binding.waterMin.text  = Editable.Factory.getInstance().newEditable(alarmList.getAlarm(alarmList.lastEditedAlarm).minWaterLevel.toString())
        binding.stationName.text = Editable.Factory.getInstance().newEditable(alarmList.getAlarm(alarmList.lastEditedAlarm).name)
        binding.spinnerPlaces.setSelection(alarmList.getAlarm(alarmList.lastEditedAlarm).stationNameOrder)
        binding.alarmOn.isChecked = alarmList.getAlarm(alarmList.lastEditedAlarm).enable

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.checkButton.setOnClickListener{
            val data = webScrubber.fetchWaterData(binding.spinnerPlaces.selectedItem.toString().split(" ")[1].toInt(), this.activity!!)
            binding.actualWater.text = data.waterLevel.toString()
            if(data.dateObtained != "null") {
                //binding.dateObtained.text = data.dateObtained.split("T")[1].dropLast(1)
                val formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM ).withLocale( Locale.GERMANY ).withZone( ZoneId.systemDefault())
                val instant = Instant.parse(data.dateObtained)
                binding.dateObtained.text = formatter.format(instant)
            }
            else{
                binding.dateObtained.text = data.dateObtained

            }


        }
        binding.deleteButton.setOnClickListener{
            alarmList.deleteAlarm(alarmList.lastEditedAlarm)
            findNavController().navigate(R.id.action_editExistingAlarm_to_FirstFragment)

        }
        binding.saveButton.setOnClickListener {
            val tempAlarm = Alarm()
            tempAlarm.init(
                binding.actualWater.text.toString().toIntOrNull()?: 0,
                binding.waterMax.text.toString().toIntOrNull()?: 0,
                binding.waterMin.text.toString().toIntOrNull()?: 0,
                binding.spinnerPlaces.selectedItem.toString(),
                binding.spinnerPlaces.selectedItemPosition,
                binding.stationName.text.toString(),
                binding.alarmOn.isChecked
            )
            alarmList.editAlarm(tempAlarm, alarmList.lastEditedAlarm)

            findNavController().navigate(R.id.action_editExistingAlarm_to_FirstFragment)
            (activity as MainActivity).alarmHandler.setAlarmIfNecessary(true)


        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createSpinner() {
        val spinner: Spinner = binding.spinnerPlaces
// Create an ArrayAdapter using the string array and a default spinner layout
        activity?.let {
            ArrayAdapter.createFromResource(
                it.applicationContext,
                R.array.places_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }
        spinner.onItemSelectedListener = SpinnerActivity()
        spinner.getSelectedItemPosition()
    }

}
