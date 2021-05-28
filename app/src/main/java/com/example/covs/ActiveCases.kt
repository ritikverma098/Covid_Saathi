package com.example.covs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform


class ActiveCases : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentFrame
            duration = 1000.toLong()
            scrimColor  = Color.TRANSPARENT
            setAllContainerColors(MaterialColors.getColor(requireContext(), R.attr.appPrimaryColor, Color.WHITE))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_active_cases, container, false)

        return view
    }



}