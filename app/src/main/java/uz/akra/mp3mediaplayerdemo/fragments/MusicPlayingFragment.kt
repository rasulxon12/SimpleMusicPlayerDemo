package uz.akra.mp3mediaplayerdemo.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.Transliterator.Position
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import uz.akra.mp3mediaplayerdemo.R
import uz.akra.mp3mediaplayerdemo.databinding.FragmentMusicPlayingBinding
import uz.akra.mp3mediaplayerdemo.utils.MyData
import uz.akra.mp3mediaplayerdemo.utils.MyData.mediaPlayer
import uz.akra.mp3mediaplayerdemo.utils.NoticationReceiver


class MusicPlayingFragment : Fragment() {
    private val binding by lazy { FragmentMusicPlayingBinding.inflate(layoutInflater) }
    lateinit var handler: Handler
    var position: Int = 0
    var channelId = "1"
    val notificationManager = NotificationManagerCompat.from(requireActivity())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val notificationManager = NotificationManagerCompat.from(requireActivity())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)


        }


        position = arguments?.getInt("keyPos", 0)!!


        return binding.root
    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        if (position != -1) {
            mediaPlayer = MediaPlayer.create(
                this.requireContext(),
                Uri.parse(MyData.list[position].musicPath)
            )
            mediaPlayer?.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause_circle_filled)


            binding.mySeekbar.max = mediaPlayer?.duration!!

            handler = Handler(activity?.mainLooper!!)

            binding.tvAllmusic.text = MyData.list.size.toString()
            binding.tvThismusic.text = (position + 1).toString()
            if (MyData.list[position].imagePath != "") {
                val bm = BitmapFactory.decodeFile(MyData.list[position].imagePath)
                binding.image.setImageBitmap(bm)
            }

            binding.tvName.text = MyData.list[position].name
            binding.tvAuthor.text = MyData.list[position].author

            binding.tvMusicfintimer.text = milsecondsToTimer(mediaPlayer?.duration!!.toLong())

            if (mediaPlayer?.isPlaying!!) {
                handler.postDelayed(runnable, 100)
            }

            binding.mySeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            binding.btnReplay30.setOnClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.minus(30000))
            }
            binding.btnSkip30.setOnClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.plus(30000))
            }

            binding.btnList.setOnClickListener {
                releaseMP()
                findNavController().popBackStack()
            }

            binding.btnPlayPause.setOnClickListener {
                if (mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_playy)
                } else {
                    mediaPlayer?.start()
                    notificationManager.notify(1, notifyBuilder())
                    binding.btnPlayPause.setImageResource(R.drawable.ic_pause_circle_filled)
                }
            }
            binding.btnNext.setOnClickListener {
                if (++position < MyData.list.size) {
                    releaseMP()
                    onResume()
                } else {
                    position = 0
                    releaseMP()
                    onResume()
                }
            }
            binding.btnPrevious.setOnClickListener {
                if (--position >= 0) {
                    releaseMP()
                    onResume()
                } else
                    position = MyData.list.size - 1
                releaseMP()
                onResume()
            }
        }
    }

    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        releaseMP()
    }

    private var runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                binding.mySeekbar.progress = mediaPlayer?.currentPosition!!
                binding.tvMusictimer.text =
                    milsecondsToTimer(mediaPlayer?.currentPosition!!.toLong())
                if (binding.tvMusictimer.text.toString() == binding.tvMusicfintimer.text.toString()) {
                    releaseMP()
                    if (++position < MyData.list.size) {
                        releaseMP()
                        onResume()
                    } else {
                        position = 0
                        releaseMP()
                        onResume()
                    }
                }
                handler.postDelayed(this, 100)
            }
        }
    }


    private fun milsecondsToTimer(milliseconds: Long): String? {
        var finTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finTimerString = "$finTimerString$minutes:$secondsString"

        // return timer string
        return finTimerString

    }

    private fun notifyBuilder(): Notification {
        val collapsedView = RemoteViews(context?.packageName, R.layout.notification_collapsed)
        val expandedView = RemoteViews(context?.packageName, R.layout.notification_expanded)

        val clickIntent = Intent(context, NoticationReceiver::class.java)
        val clickPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        }


        expandedView.setImageViewResource(R.id.image_view_expanded,
            R.drawable.ic_launcher_background)
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent)

        return NotificationCompat.Builder(this.requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()).build()
    }


}