package com.gscanlon21.reversedictionary.ui.main.search.result

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

        searchTermViewModel.searchWord.observe(viewLifecycleOwner, Observer { searchTerm ->
            root.findViewById<TextView>(R.id.scrabble_score).text =
                getString(R.string.placeholder_scrabble_score, metaViewModel.scrabbleScore(searchTerm!!))

            lifecycleScope.launch {
                metaViewModel.getAudioUri(searchTerm).observe(viewLifecycleOwner, Observer { audioUri ->
                    val audioView = root.findViewById<Button>(R.id.audio)
                    if (audioUri != null) {
                        mediaPlayer?.setDataSource(audioUri)
                        mediaPlayer?.prepareAsync()
                        audioView.visibility = View.VISIBLE
                        audioView.setOnClickListener {
                            mediaPlayer?.start()
                        }
                    } else {
                        audioView.visibility = View.GONE
                    }
                })
            }
        })

        return root
    }
}
