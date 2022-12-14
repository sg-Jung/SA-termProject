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
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: NpcViewModel
    private val npcAdapter = NpcAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[NpcViewModel::class.java]

        binding.list.apply{
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = npcAdapter
        }

        model.list.observe(this){
            npcAdapter.notifyItemRangeInserted(0, model.list.value?.size?: 0)
        }

        model.requestNpc()
    }

    inner class NpcAdapter: RecyclerView.Adapter<NpcAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener{
            val txName: TextView = itemView.findViewById(R.id.text1)
            val txLocate: TextView = itemView.findViewById(R.id.text2)
            val npcImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init{
                npcImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val intent = Intent(application, GtaActivity::class.java)

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item_npc, parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txName.text = model.list.value?.get(position)?.name
            holder.txLocate.text = model.list.value?.get(position)?.l_name
            holder.npcImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size?: 0
    }
}