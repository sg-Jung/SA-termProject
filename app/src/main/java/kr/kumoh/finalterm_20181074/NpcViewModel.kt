package kr.kumoh.finalterm_20181074

import android.app.Application
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class NpcViewModel(application: Application): AndroidViewModel(application) {
    data class Npc(var name: String, var l_name: String, var image: String)

    companion object{
        const val QUEUE_TAG = "NpcVolleyRequest"
        const val SERVER_URL = "https://testjsgjsg.run.goorm.io"
    }

    private val npcs = ArrayList<Npc>()
    private val _list = MutableLiveData<ArrayList<Npc>>()
    val list: LiveData<ArrayList<Npc>>
        get() = _list

    private var queue: RequestQueue
    val imageLoader: ImageLoader

    init{
        _list.value = npcs
        queue = Volley.newRequestQueue(getApplication())

        imageLoader = ImageLoader(queue, object: ImageLoader.ImageCache{
            private val cache = LruCache<String, Bitmap>(100)

            override fun getBitmap(url: String?): Bitmap? {
                return cache.get(url)
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                cache.put(url, bitmap)
            }
        })
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(npcs[i].image, "utf-8")

    fun requestNpc(){

        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/NPC",
            null,
            {
                npcs.clear()
                parseJson(it)
                _list.value = npcs
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        queue.add(request)
    }

    private fun parseJson(items: JSONArray){
        for(i in 0 until items.length()){
            val item: JSONObject = items[i] as JSONObject
            val name = item.getString("name")
            val locate = item.getString("l_name")
            val image = item.getString("image")

            npcs.add(Npc(name, locate, image))
        }
    }

    override fun onCleared() { // 프로그램 종료 시 RecyclerView가 사라지는 시점에 한 번 수행되는 함수
        super.onCleared()
        queue.cancelAll(QUEUE_TAG)
    }

}