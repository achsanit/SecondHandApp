package com.example.finalprojectbinaracademy_secondhandapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.finalprojectbinaracademy_secondhandapp.R
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.Banner
import com.example.finalprojectbinaracademy_secondhandapp.databinding.SliderItemBinding
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.slider_item.view.*

class ImageSliderAdapter(
    private val data: List<Banner>
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    class ViewHolder(var binding: SliderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentList = data[position]

        if (currentList.image_url == null) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.default_photo)
                .fitCenter()
                .into(holder.itemView.iv_slider)
        } else {
            Glide.with(holder.itemView.context)
                .load(currentList.image_url)
                .centerCrop()
                .into(holder.itemView.iv_slider)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}

//class SliderAdapter internal constructor(sliderItems: List<SliderItems>, viewPager2: ViewPager2) :
//    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {
//    private val sliderItems: List<SliderItems>
//    private val viewPager2: ViewPager2
//
//    @NonNull
//    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): SliderViewHolder {
//        return SliderViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.slide_item_container, parent, false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(@NonNull holder: SliderViewHolder, position: Int) {
//        holder.setImage(sliderItems[position])
//        if (position == sliderItems.size - 2) {
//            viewPager2.post(runnable)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return sliderItems.size
//    }
//
//    internal inner class SliderViewHolder(@NonNull itemView: View) :
//        RecyclerView.ViewHolder(itemView) {
//        private val imageView: RoundedImageView
//        fun setImage(sliderItems: SliderItems) {
////use glide or picasso in case you get image from internet
//            imageView.setImageResource(sliderItems.getImage())
//        }
//
//        init {
//            imageView = itemView.findViewById(R.id.imageSlide)
//        }
//    }
//
//    init {
//        this.sliderItems = sliderItems
//        this.viewPager2 = viewPager2
//    }
//}