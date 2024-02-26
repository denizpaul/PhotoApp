package com.dennis.photoapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dennis.photoapp.R
import com.dennis.photoapp.data.sources.local.model.PhotoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePageAdapter: RecyclerView.Adapter<HomePageAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    private lateinit var articleImage: ImageView
    private lateinit var articleTitle: TextView
    private lateinit var articleDescription: TextView
    private lateinit var articleLikesCount: TextView
    private lateinit var articleDate: TextView
    private var onPhotoItemClickListener: ((PhotoEntity) -> Unit)? = null

    private val differCallback = object: DiffUtil.ItemCallback<PhotoEntity>() {
        override fun areItemsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo_home, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        articleTitle = holder.itemView.findViewById(R.id.authorName)
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleLikesCount = holder.itemView.findViewById(R.id.likesCount)
        articleDate = holder.itemView.findViewById(R.id.dateText)

        holder.itemView.apply {
            articleTitle.text = article.author
            articleLikesCount.text = article.likesCount.toString() + " Likes"
            setFormattedTimestamp(articleDate, article.lastViewedTimeStamp!!)
            Glide.with(this)
                .load(article.downloadUrl)
                .into(articleImage)
        }
        holder.itemView.setOnClickListener {
            onPhotoItemClickListener?.let {
                it(article)
            }
        }
    }

    fun setOnItemClickListener(listener: (PhotoEntity) -> Unit) {
        this.onPhotoItemClickListener = listener
    }

    private fun setFormattedTimestamp(textView: TextView, timestamp: Long) {
        if(timestamp > 0) {
            textView.visibility = View.VISIBLE
            val dateFormat = SimpleDateFormat("yy/MM/dd_HHmm", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(timestamp))
            textView.text = formattedDate
        } else {
            textView.visibility = View.GONE
        }
    }

}