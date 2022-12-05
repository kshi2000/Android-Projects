package com.example.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.sevenminuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null

    private var currentFormType: String = METRIC_UNITS_VIEW

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNITS_VIEW"
        private const val US_UNITS_VIEW = "US_UNITS_VIEW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmi)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarBmi?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.toolbarBmi?.title = "CALCULATE BMI"

        binding?.btnCalculate?.setOnClickListener {
            calculateBmi()
        }

        binding?.rgRadio?.setOnCheckedChangeListener { _, selectedRadioId ->
            if (selectedRadioId == R.id.rb_metric) {
                setMetricFormView()
            } else if (selectedRadioId == R.id.rb_us) {
                setUsFormView()
            }
        }
    }

    private fun setMetricFormView() {
        binding?.llFeetInchesContainer?.visibility = View.GONE
        binding?.tilHeight?.visibility = View.VISIBLE

        binding?.tilWeight?.hint = "Weight (in Kg)"
        currentFormType = METRIC_UNITS_VIEW
    }

    private fun setUsFormView() {
        binding?.llFeetInchesContainer?.visibility = View.VISIBLE
        binding?.tilHeight?.visibility = View.GONE

        binding?.tilWeight?.hint = "Weight (in Lb)"

        currentFormType = US_UNITS_VIEW
    }

    private fun isValidMetricInputs(): Boolean {
        var isValid = true

        if (binding?.etWeight?.text?.isEmpty() == true || binding?.etHeight?.text?.isEmpty() == true) {
            isValid = false
        }

        return isValid
    }

    private fun isValidUsInputs(): Boolean {
        var isValid = true

        if (binding?.etWeight?.text?.isEmpty() == true || binding?.etFeet?.text?.isEmpty() == true || binding?.etInches?.text?.isEmpty() == true) {
            isValid = false
        }

        return isValid
    }

    private fun calculateBmi() {
        val comment: String
        val category: String
        var result:Double = 0.0

        if (currentFormType == METRIC_UNITS_VIEW &&  isValidMetricInputs()) {
            val wt = binding?.etWeight?.text?.toString()?.toDouble()
            val ht = (binding?.etHeight?.text?.toString()?.toDouble())?.div(100)

            result =
                BigDecimal((wt!! / (ht!! * ht))).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
        else if(currentFormType == US_UNITS_VIEW && isValidUsInputs())
        {
            val wt = binding?.etWeight?.text?.toString()?.toDouble()
            val feet = (binding?.etFeet?.text?.toString()?.toDouble())
            val inches = (binding?.etInches?.text?.toString()?.toDouble())

            result =  BigDecimal(((wt!!) / ((feet!!*12 + inches!!)*(feet *12 + inches)))*703).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
        else {
            Toast.makeText(this, "Please fill out the details", Toast.LENGTH_LONG).show()
            return
        }
            if (result <= 18.5) {
                category = "Underweight"
                comment = "Must eat more"
            } else if (result > 18.5 && result <= 24.9) {
                category = "Healthy"
                comment = "Perfectly fine"
            } else if (result in 25.0..29.9) {
                category = "Overweight"
                comment = "Must eat less"
            } else {
                category = "Alien"
                comment = "You are not human!!"
            }

            binding?.tvBmi?.text = result.toString()
            binding?.tvBmiCategory?.text = category
            binding?.tvBmiComment?.text = comment

            binding?.llResults?.visibility = View.VISIBLE

    }
}