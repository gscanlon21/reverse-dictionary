package com.gscanlon21.reversedictionary.ui.main.search.result

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.ui.main.MainFragment
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import com.gscanlon21.reversedictionary.vm.search.result.MetaViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MetaFragment : MainFragment() {
    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val metaViewModel: MetaViewModel by viewModels {
        InjectorUtil.provideMetaViewModelFactory(requireContext())
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release(); mediaPlayer = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search_result_meta, container, false)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .build()
            )
        }

        root.findViewById<TextView>(R.id.scrabble_score).text = getString(R.string.placeholder_scrabble_score, metaViewModel.scrabbleScore(searchTermViewModel.searchWord.value!!))

        lifecycleScope.launch {
            metaViewModel.getAudioUri(searchTermViewModel.searchWord.value!!).observe(viewLifecycleOwner, Observer { audioData ->
                val audioView = root.findViewById<Button>(R.id.audio)
                if (audioData?.audioUrl != null) {
                    mediaPlayer?.setDataSource(audioData.audioUrl)
                    mediaPlayer?.prepareAsync()
                    audioView.visibility = View.VISIBLE
                    audioView.setOnClickListener {
                        mediaPlayer?.start()
                    }
                    audioView.setOnLongClickListener {
                        showAttributionPopup(it, Uri.parse(audioData.attributionUrl))
                    }
                } else {
                    audioView.visibility = View.GONE
                }
            })
        }

        return root
    }

    private fun showAttributionPopup(v: View, attributionUrl: Uri): Boolean {
        val popup = PopupMenu(v.context, v)
        popup.menu.add(Menu.NONE, ATTRIBUTION_ID, ATTRIBUTION_ID, getString(R.string.context_menu_attribution))
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                ATTRIBUTION_ID -> {
                    context?.startActivity(Intent(Intent.ACTION_VIEW, attributionUrl))
                    true
                }
                else -> false
            }
        }
        popup.show()
        return true
    }

    companion object {
        private const val ATTRIBUTION_ID = 1
    }
}
