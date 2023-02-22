package dev.ascallion.reversedictionary.adapter.preference

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.core.preference.Attribution
import dev.ascallion.reversedictionary.ui.preference.RawTextActivity

class AttributionAdapter(private val attributions: List<Attribution>) :
    RecyclerView.Adapter<AttributionAdapter.AttributionViewHolder>() {

    class AttributionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.adapter_attribution_text)
    }

    override fun getItemCount(): Int = attributions.size
    override fun getItemViewType(position: Int): Int {
        val license = attributions[position]
        return if (license.link == null) {
            R.layout.adapter_attribution_license
        } else {
            R.layout.adapter_attribution_link
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return AttributionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttributionViewHolder, position: Int) {
        val attribution = attributions[position]

        holder.apply {
            text.text = holder.itemView.context.getString(attribution.title)
            itemView.setOnClickListener {
                when (getItemViewType(position)) {
                    R.layout.adapter_attribution_license -> openLicense(holder, attribution)
                    R.layout.adapter_attribution_link -> openInBrowser(holder, attribution)
                }
            }
        }
    }

    private fun openInBrowser(holder: AttributionViewHolder, attribution: Attribution) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(holder.itemView.context.getString(attribution.link!!)))
        ContextCompat.startActivity(holder.itemView.context, browserIntent, null)
    }

    private fun openLicense(holder: AttributionViewHolder, attribution: Attribution) {
        val textIntent = Intent(holder.itemView.context, RawTextActivity::class.java).apply {
            putExtra(RawTextActivity.EXTRA_TITLE, attribution.title)
            putExtra(RawTextActivity.EXTRA_RAW_RESOURCE, attribution.license)
        }

        ContextCompat.startActivity(holder.itemView.context, textIntent, null)
    }
}
