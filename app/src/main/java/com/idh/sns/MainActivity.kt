package com.idh.sns

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.card_background.view.imageView
import kotlinx.android.synthetic.main.card_post.view.*

class MainActivity : AppCompatActivity() {




    // 글 목록을 저장하는 변수
    val posts: MutableList<Post> = mutableListOf()


    val TAG = "MainActivity"

    val ref = FirebaseDatabase.getInstance().getReference("test")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportActionBar?.title = "글목록"

        floatingActionButton2.setOnClickListener {
            val intent = Intent(this@MainActivity, WriteActivity::class.java)
            startActivity(intent)
        }



        val layoutManager = LinearLayoutManager(this@MainActivity)


        //아이템을 역순으로 정렬하게 함
        layoutManager.reverseLayout = true

        layoutManager.stackFromEnd = true

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MyAdapter()




        FirebaseDatabase.getInstance().getReference("/Posts")
            .orderByChild("writeTime").addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildKey: String?) {

                    snapshot?.let { snapshot->
                        val post = snapshot.getValue(Post::class.java)
                        post?.let {
                            if (previousChildKey == null){
                                posts.add(it)
                                recyclerView.adapter?.notifyItemInserted(posts.size - 1)

                            }else{
                                val prevIndex = posts.map { it.postId }.indexOf(previousChildKey)
                                posts.add(prevIndex + 1 , post)

                                recyclerView.adapter?.notifyItemInserted(prevIndex + 1)
                            }
                        }
                    }

                }


                override fun onChildChanged(snapshot: DataSnapshot, previousChildKey: String?) {

                    snapshot?.let { snapshot ->
                        val post = snapshot.getValue(Post::class.java)
                        post?.let { post ->

                            val prevIndex = posts.map { it.postId }.indexOf(previousChildKey)
                            posts[prevIndex + 1] = post
                            recyclerView.adapter?.notifyItemChanged(prevIndex + 1)
                        }
                    }

                }


                override fun onChildMoved(snapshot: DataSnapshot, previousChildKey: String?) {

                    snapshot?.let {
                        val post = snapshot.getValue(Post :: class.java)

                        post?.let { post ->

                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            recyclerView.adapter?.notifyItemRemoved(existIndex)

                            if (previousChildKey == null){
                                posts.add(post)
                                recyclerView.adapter?.notifyItemChanged(posts.size - 1)

                            }else{
                                val prevIndex = posts.map { it.postId }.indexOf(previousChildKey)
                                posts.add(prevIndex + 1 , post)
                                recyclerView.adapter?.notifyItemChanged(prevIndex + 1)
                            }

                        }
                    }

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)

                        post?.let { post ->
                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            recyclerView.adapter?.notifyItemRemoved(existIndex)

                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                    databaseError?.toException()?.printStackTrace()
                }


            })




    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val imageView: ImageView = itemView.imageView

        val contentsText: TextView = itemView.contentsText

        val timeTextView : TextView = itemView.timeTextView

        val commentCountText : TextView = itemView.commentCountText



    }


    //2020.07.08

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            return MyViewHolder(LayoutInflater.from(this@MainActivity).inflate(R.layout.card_post,parent, false))
        }

        override fun getItemCount(): Int {

            return posts.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


            val post = posts[position]

            Picasso.get().load(Uri.parse(post.bgUri)).fit().centerCrop().into(holder.imageView)

            holder.contentsText.text = post.message

            holder.timeTextView.text = getDiffTimeText(post.writeTime as Long)

            holder.commentCountText.text = "0"


        }

    }

    // 2020.07.10
    private fun getDiffTimeText(targetTime : Long): String {

        val curDate

    }


}
