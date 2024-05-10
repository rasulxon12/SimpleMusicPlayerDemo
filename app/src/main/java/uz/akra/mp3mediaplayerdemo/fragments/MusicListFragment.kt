package uz.akra.mp3mediaplayerdemo.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.ColorSpace.Adaptation
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import uz.akra.mp3mediaplayerdemo.R
import uz.akra.mp3mediaplayerdemo.adapters.MyListAdapter
import uz.akra.mp3mediaplayerdemo.databinding.FragmentMusicListBinding
import uz.akra.mp3mediaplayerdemo.models.Music
import uz.akra.mp3mediaplayerdemo.utils.MyData
import uz.akra.mp3mediaplayerdemo.utils.MyData.list
import uz.akra.mp3mediaplayerdemo.utils.MyData.mediaPlayer


class MusicListFragment : Fragment() {
    private val binding by lazy { FragmentMusicListBinding.inflate(layoutInflater) }
    lateinit var myListAdapter: MyListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        }

        MyData.list = binding.root.context.musicFiles() as ArrayList<Music>


        myListAdapter = MyListAdapter(this.requireContext(), list)
        binding.myContainerRv.adapter = myListAdapter

        binding.myContainerRv.setOnItemClickListener { parent, view, position, id ->
            findNavController().navigate(R.id.musicPlayingFragment, bundleOf("keyPos" to position))
        }


        return binding.root
    }

    @SuppressLint("Range")
    fun Context.musicFiles(): MutableList<Music> {
        val list: MutableList<Music> = mutableListOf()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = this.contentResolver.query(
            uri, // Uri
            null, // Projection
            selection, // Selection
            null, // Selection arguments
            sortOrder // Sort order
        )


        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val authorId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
            val imageId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)

            do {
                val audioId: Long = cursor.getLong(id)
                val artist = cursor.getString(authorId)
                val audioTitle: String = cursor.getString(title)
                var imagePath: String = ""
                if (imageId != -1) {
                    imagePath = cursor.getString(imageId)
                }
                val musicPath: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                // Add the current music to the list
                list.add(Music(audioId, audioTitle, artist, imagePath, musicPath))
            } while (cursor.moveToNext())
        }

        return list
    }
}