package com.iti4.retailhub.features.mybag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentMyBagBinding


class MyBagFragment : Fragment() {
    private lateinit var binding: FragmentMyBagBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val manager = LinearLayoutManager(requireContext())
        manager.setOrientation(RecyclerView.VERTICAL)
        binding.rvMyBag.layoutManager = manager

        val adapter =
            MyBagProductRecyclerViewAdapter(
            )
        binding.rvMyBag.adapter = adapter
        adapter.submitList(
            listOf(
                Product("Pullover1", 51.0, "Black", "L", R.drawable.photo, 2),
                Product("Shirt2", 67.0, "Green", "L", R.drawable.photo, 4),
                Product("T-Shirt3", 45.0, "Red", "L", R.drawable.photo, 5),
                Product("Pullover4", 51.0, "Black", "L", R.drawable.photo, 6),
                Product("T-Shirt5", 45.0, "Red", "L", R.drawable.photo, 7),
                Product("Pullover6", 51.0, "Black", "L", R.drawable.photo, 2),
                Product("Shirt7", 67.0, "Green", "L", R.drawable.photo, 12),
            )
        )
    }


}


