package com.hello

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hello.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.earth)
    }

    private var index = 0

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val b = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            c.drawBitmap(bitmap, 0f, 0f, null)
            val mode = BlendMode.values()[index % BlendMode.values().size]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                c.drawColor(Color.parseColor("#55FF0000"), mode)
            }
            binding.imageView.setImageBitmap(b)
            index += 1
            binding.textView.text = "mode = ${mode.name}, index = $index"
        }

        binding.button2.setOnClickListener {

        }
    }
}