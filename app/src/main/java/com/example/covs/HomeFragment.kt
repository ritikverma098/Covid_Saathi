package com.example.covs

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.futured.donut.DonutSection

import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        view.donut_view.cap = 1f
        view.donut_view.submitData(getSections())
        return view
    }

    private fun getSections(): List<DonutSection>{
        return listOf(
            DonutSection(
                name = "Active",
                color = Color.parseColor("#FCEC52"),
                amount = 1f
            ),
            DonutSection(
                name = "Recovered",
                color = Color.parseColor("#058C42"),
                amount = 2f
            ),
            DonutSection(
                name = "Death",
                color = Color.parseColor("#FF0000"),
                amount = 1f
            )
        )
    }

}