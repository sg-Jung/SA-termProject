package kr.kumoh.finalterm_20181074

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import kr.kumoh.finalterm.databinding.ActivityGtaBinding

class GtaActivity : AppCompatActivity() {
    companion object{
        const val KEY_LOCATE = "NpcLocate"
        const val KEY_NAME = "NpcName"
        const val KEY_IMAGE = "NpcImage"
    }

    private lateinit var binding: ActivityGtaBinding
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGtaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageLoader = ImageLoader(
            Volley.newRequestQueue(this),
            object: ImageLoader.ImageCache{
                private val cache = LruCache<String, Bitmap>(100)

                override fun getBitmap(url: String?): Bitmap? {
                    return cache.get(url)
                }

                override fun putBitmap(url: String?, bitmap: Bitmap?) {
                    cache.put(url, bitmap)
                }
            })

        // intent로 전송한 값들을 Key로 받아와 사용한다.
        binding.imageNpc.setImageUrl(intent.getStringExtra(KEY_IMAGE), imageLoader)
        binding.textLocate.text = intent.getStringExtra(KEY_LOCATE)
        binding.textName.text = intent.getStringExtra(KEY_NAME)
    }
}