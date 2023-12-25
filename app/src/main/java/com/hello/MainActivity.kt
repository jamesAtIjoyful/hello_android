package com.hello

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hello.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener {


            val bitmap = Bitmap.createBitmap(binding.imageView.width, binding.imageView.height, Bitmap.Config.ARGB_8888)
            val heart = BitmapFactory.decodeResource(resources, R.drawable.dot3)
//            val rect = Rect(100, 100, 100 + heart.width, 100 + heart.height)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            //
            val b = heart.extractAlpha()
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                val d = resources.displayMetrics.density
                maskFilter = BlurMaskFilter(3f * d, BlurMaskFilter.Blur.NORMAL)
                color = Color.parseColor("#66000000")
//                color = Color.RED
                binding.textView.text = "imageView size = ${binding.imageView.width}x${binding.imageView.height}, ${d * 3f}"
            }

            val mario = BitmapFactory.decodeResource(resources, R.drawable.mario)
            val b2 = Bitmap.createBitmap(heart.width, heart.height, Bitmap.Config.ARGB_8888)
            val c2 = Canvas(b2)
            c2.drawBitmap(heart, 0f, 0f, null)
            c2.drawBitmap(mario, Rect(0, 0, mario.width, mario.height), Rect(0, 0, heart.width, heart.height), Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            })
            val r1 = Rect(98, 102, 98 + heart.width, 102 + heart.height)
            canvas.drawBitmap(b, null, r1, p)
//            val r2 = Rect(100, 100, 100 + heart.width, 100 + heart.height)
//            c2.drawBitmap(b, null, r2, null)
            canvas.drawBitmap(b2, 100f, 100f, null)

            binding.imageView.setImageBitmap(bitmap)
        }


    }
}