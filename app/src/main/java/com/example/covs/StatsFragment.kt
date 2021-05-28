package com.example.covs

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.core.view.isVisible
import androidx.webkit.WebViewFeature
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.fragment_stats.view.*

class StatsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_stats, container, false)
        view.webViewContainer.settings.javaScriptEnabled = true

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)){
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(
                        view.webViewContainer.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setForceDark(
                        view.webViewContainer.settings,
                        WebSettingsCompat.FORCE_DARK_OFF
                    )
                }

            }
        }

        val embedHTML : String = "<div class=\"bingwidget\" data-type=\"covid19_map\" data-market=\"en-in\" data-language=\"en-in\" data-location-id=\"/India\"></div>\n" +
                "  \n" +
                "<script src=\"https://www.bing.com/widget/bootstrap.answer.js\" async=\"\"></script>\n"
        view.webViewContainer.loadData(embedHTML, "text/html", "utf-8")

        return view
    }

}