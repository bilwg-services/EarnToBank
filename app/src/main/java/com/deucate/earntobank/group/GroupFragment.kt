package com.deucate.earntobank.group


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.WriterException
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earntobank.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_group.view.*
import timber.log.Timber


class GroupFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val data = ArrayList<Group>()
    private val adapter = GroupAdapter(data)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_group, container, false)

        val qrgEncoder = QRGEncoder(auth.currentUser!!.uid, null, QRGContents.Type.TEXT, 200)
        try {
            val bitmap = qrgEncoder.encodeAsBitmap()
            rootView.groupQRCode.setImageBitmap(bitmap)
            rootView.groupUserName.text = auth.currentUser!!.displayName
        } catch (e: WriterException) {
            Timber.v(e.toString())
        }

        db.collection(getString(R.string.users)).document(auth.uid!!).collection(getString(R.string.ref)).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result!!) {
                    val group = data.toObject(Group::class.java)
                    this@GroupFragment.data.add(group)
                }
                adapter.notifyDataSetChanged()
            }
        }

        val recyclerView = rootView.groupRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        return rootView
    }
}