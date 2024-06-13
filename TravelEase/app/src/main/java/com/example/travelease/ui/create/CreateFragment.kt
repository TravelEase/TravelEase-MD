package com.example.travelease.ui.create

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelease.R
import com.example.travelease.databinding.FragmentCreateBinding
import java.util.*

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private var selectedCategories = mutableSetOf<String>()
    private var selectedCity: String? = null
    private var selectedDates: String? = null
    private var numberOfPeople: Int = 1

    companion object {
        const val EXTRA_DATES = "EXTRA_DATES"
        const val EXTRA_CITY = "EXTRA_CITY"
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        const val EXTRA_NUMBER_OF_PEOPLE = "EXTRA_NUMBER_OF_PEOPLE"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createViewModel =
            ViewModelProvider(this).get(CreateViewModel::class.java)

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupDatePicker()
        setupCitySpinner()
        setupCategoryButtons()
        setupNumberOfPeopleButtons()
        setupContinueButton()

        return root
    }

    private fun setupDatePicker() {
        binding.btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val startDate = "$dayOfMonth-${month + 1}-$year"
                val endDateCalendar = Calendar.getInstance()
                DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
                    val endDate = "$endDay-${endMonth + 1}-$endYear"
                    selectedDates = "$startDate to $endDate"
                    binding.btnCalendar.text = selectedDates
                }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }

    private fun setupCitySpinner() {
        val cityNames = resources.getStringArray(R.array.city_names)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dropdownCity.adapter = adapter

        binding.dropdownCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCity = cityNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            binding.btnBudaya,
            binding.btnCagarAlam,
            binding.btnTamanHiburan,
            binding.btnBahari,
            binding.btnTempatIbadah,
            binding.btnPusatBelanja
        )

        categoryButtons.forEach { button ->
            button.setOnClickListener {
                toggleCategoryButton(button)
            }
        }
    }

    private fun toggleCategoryButton(button: Button) {
        val category = button.text.toString()
        if (selectedCategories.contains(category)) {
            selectedCategories.remove(category)
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_button_color))
        } else {
            selectedCategories.add(category)
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected_button_color))
        }
    }

    private fun setupNumberOfPeopleButtons() {
        binding.btnPlus.setOnClickListener {
            numberOfPeople++
            binding.tfNumberOfPeople.setText(numberOfPeople.toString())
        }

        binding.btnMinus.setOnClickListener {
            if (numberOfPeople > 1) {
                numberOfPeople--
                binding.tfNumberOfPeople.setText(numberOfPeople.toString())
            }
        }
    }

    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener {
            val intent = Intent(activity, ContinueCreateItineraryActivity::class.java)
            intent.putExtra(EXTRA_DATES, selectedDates)
            intent.putExtra(EXTRA_CITY, selectedCity)
            intent.putStringArrayListExtra(EXTRA_CATEGORY, ArrayList(selectedCategories))
            intent.putExtra(EXTRA_NUMBER_OF_PEOPLE, numberOfPeople)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
