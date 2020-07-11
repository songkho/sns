package com.idh.sns

import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.card_post.view.*
import org.w3c.dom.Text

class WriteActivity : AppCompatActivity() {

    var currentBgPosition = 0

    val bgList = mutableListOf(
        "android.resource://com.idh.sns/drawable/default_bg"
        , "android.resource://com.idh.sns/drawable/bg2"
        , "android.resource://com.idh.sns/drawable/bg3"
        , "android.resource://com.idh.sns/drawable/bg4"
        , "android.resource://com.idh.sns/drawable/bg5"
        , "android.resource://com.idh.sns/drawable/bg6"
        , "android.resource://com.idh.sns/drawable/bg7"
        , "android.resource://com.idh.sns/drawable/bg8"
        , "android.resource://com.idh.sns/drawable/bg9"
    )

    var mode = "post"

    var postId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)


        intent.getStringExtra("mode")?.let {
            mode = intent.getStringExtra("mode")
            postId = intent.getStringExtra("postId")

        }


    supportActionBar?.title = if (mode == "post") "글쓰기" else "댓글쓰기"

    // 리사이클러뷰 에서 사요알 레이아웃 매니저를 생성한다.
    val layoutManager = LinearLayoutManager(this@WriteActivity)

    // 리사이클러뷰를 횡으로 스크롤 할 것이므로 레이아웃 메니져의 방향을 호리젠탈로 설정한다.
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL


    recyclerView.layoutManager  = layoutManager

    recyclerView.adapter = MyAdapter()



    sendButton.setOnClickListener{
        if (TextUtils.isEmpty(input.text)) {
            Toast.makeText(applicationContext, "메세지를 입력하세요", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }

        if (mode == "post"){

        val post = Post()

        val newRef = FirebaseDatabase.getInstance().getReference("Posts").push()

        post.writeTime = ServerValue.TIMESTAMP

        post.bgUri = bgList[currentBgPosition]

        post.message = input.text.toString()

        post.writerId = getMyId()

        post.postId = newRef.key.toString()

        newRef.setValue(post)

        Toast.makeText(applicationContext, "공유되었습니다.", Toast.LENGTH_LONG).show()

        finish()

    }else
    {
        val comment = Comment()

        val newRef = FirebaseDatabase.getInstance().getReference("Comments/$postId").push()

        comment.writeTime = ServerValue.TIMESTAMP
        comment.bgUri = bgList[currentBgPosition]

        comment.message = input.text.toString()

        comment.writerId = getMyId()

        comment.commentId = newRef.key.toString()

        comment.postId = postId

        newRef.setValue(comment)

        Toast.makeText(applicationContext, "공유되었습니다.", Toast.LENGTH_LONG).show()

        finish()

    }

}
    }

    private fun getMyId(): String {

        return Settings.Secure.getString(this.contentResolver,Settings.Secure.ANDROID_ID)

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val imageView = itemView.imageView

    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            return MyViewHolder(LayoutInflater.from(this@WriteActivity).inflate(R.layout.card_background, parent, false))

        }

        override fun getItemCount(): Int {

            return bgList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            Picasso.get()
                .load(Uri.parse(bgList[position]))
                .fit()
                .centerCrop()
                .into(holder.imageView)


            holder.itemView.setOnClickListener {

                currentBgPosition = position


                Picasso.get()
                    .load(Uri.parse(bgList[position]))
                    .fit()
                    .centerCrop()
                    .into(writeBackground)
            }


        }

    }
}
