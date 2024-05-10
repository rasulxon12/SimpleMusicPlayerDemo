package uz.akra.mp3mediaplayerdemo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import uz.akra.mp3mediaplayerdemo.R
import uz.akra.mp3mediaplayerdemo.databinding.ItemRvBinding
import uz.akra.mp3mediaplayerdemo.models.Music

class MyListAdapter(context: Context, val list: List<Music>):ArrayAdapter<Music>(context, R.layout.item_rv, list) {

    @SuppressLint("SuspiciousIndentation")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var itemView:ItemRvBinding
        if (convertView == null){
            itemView = ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }else{
            itemView = ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
    itemView.tvName.text = list[position].name
//    val bm = BitmapFactory.decodeFile(list[position].imagePath)
//     itemView.image.setImageBitmap(bm)
     itemView.tvAuthor.text = list[position].author


        return itemView.root
    }



}