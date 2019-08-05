package com.iven.potatowalls.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.iven.potatowalls.R
import com.iven.potatowalls.mPotatoPreferences
import com.iven.potatowalls.ui.Utils

class VectorsAdapter(private val context: Context) : RecyclerView.Adapter<VectorsAdapter.VectorsHolder>() {

    var onVectorClick: ((Int?) -> Unit)? = null

    //first = background color, second = vector color
    private val mVectors = listOf(
        R.drawable.ic_potato,
        R.drawable.ic_potato_full,
        R.drawable.ic_potato_alt
    )

    private var mSelectedDrawable = R.drawable.ic_potato_full

    init {
        mSelectedDrawable = mPotatoPreferences.vector
    }

    fun getVectorPosition(drawable: Int): Int {
        return try {
            mVectors.indexOf(drawable)
        } catch (e: Exception) {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VectorsHolder {
        return VectorsHolder(LayoutInflater.from(parent.context).inflate(R.layout.vector_option, parent, false))
    }

    override fun getItemCount(): Int {
        return mVectors.size
    }

    override fun onBindViewHolder(holder: VectorsHolder, position: Int) {
        holder.bindItems(mVectors[holder.adapterPosition])
    }

    inner class VectorsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val accent = Utils.getSystemAccentColor(context)

        fun bindItems(drawable: Int) {

            val vectorButton = itemView as ImageButton

            vectorButton.setImageResource(drawable)

            if (mSelectedDrawable == drawable) vectorButton.setBackgroundColor(accent)
            else
                vectorButton.setBackgroundColor(Color.TRANSPARENT)

            itemView.setOnClickListener {
                notifyItemChanged(getVectorPosition(mSelectedDrawable))
                mSelectedDrawable = drawable
                vectorButton.setBackgroundColor(accent)
                onVectorClick?.invoke(drawable)
            }
        }
    }
}