package kr.kumoh.finalterm_20181074

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.kumoh.finalterm.R
import kr.kumoh.finalterm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // activity_main.xml을 viewBinding으로 사용
    private lateinit var model: NpcViewModel // NpcViewModel을 ViewModel로 사용
    private val npcAdapter = NpcAdapter() // NpcAdapter 인스턴스 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // viewBinding 연결
        setContentView(binding.root) // 컨텐츠 뷰로 binding 사용

        model = ViewModelProvider(this)[NpcViewModel::class.java] // ViewModelProvider로 NpcViewModel 사용

        binding.list.apply{
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = npcAdapter
        }

        model.list.observe(this){ // NpcViewModel을 Observer 패턴으로 사용
            npcAdapter.notifyItemRangeInserted(0, model.list.value?.size?: 0)
        }

        model.requestNpc()
    }

    // Adapter 패턴을 사용하기 위해 NpcAdapter를 만듦
    inner class NpcAdapter: RecyclerView.Adapter<NpcAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener{
            val txName: TextView = itemView.findViewById(R.id.text1)
            val txLocate: TextView = itemView.findViewById(R.id.text2)
            val npcImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init{
                npcImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) { // OnClickListener 인터페이스를 상속받았기 때문에 onClick() 오버라이딩
                val intent = Intent(application, GtaActivity::class.java) // intent로 GtaActivity사용

                // intent에 key, value 방식으로 값 전달
                intent.putExtra(
                    GtaActivity.KEY_NAME,
                    model.list.value?.get(adapterPosition)?.name)
                intent.putExtra(
                    GtaActivity.KEY_LOCATE,
                    model.list.value?.get(adapterPosition)?.l_name)
                intent.putExtra(
                    GtaActivity.KEY_IMAGE,
                    model.getImageUrl(adapterPosition))

                startActivity(intent)

            }
        }
        
        // RecyclerView.Adapter를 상속 받았으므로 아래 3개 함수를 오버라이딩해 사용함
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // ViewHolder 생성 시 한 번 호출
            val view = layoutInflater.inflate(
                R.layout.item_npc, parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) { // ViewHolder의 데이터 갱신을 위해 사용 됨
            holder.txName.text = model.list.value?.get(position)?.name
            holder.txLocate.text = model.list.value?.get(position)?.l_name
            holder.npcImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size?: 0
    }
}