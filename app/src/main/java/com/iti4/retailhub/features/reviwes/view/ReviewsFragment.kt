package com.iti4.retailhub.features.reviwes.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathercast.alarmandnotification.view.ProductDetailsDiffUtilAdapter
import com.iti4.retailhub.databinding.FragmentReviewsBinding
import com.iti4.retailhub.features.reviwes.viewmodel.ReviewsViewModel
import com.iti4.retailhub.models.Review
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ReviewsFragment : Fragment() {

    lateinit var binding: FragmentReviewsBinding
    private val reviewsViewModel by viewModels<ReviewsViewModel>()
    lateinit var allReviews:List<Review>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         allReviews=reviewsViewModel.getAllReviews((0..49).random())
        val reviewsAdapter= ReviewsDiffUtilAdapter(requireContext())
        binding.allreviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.allreviews.adapter=reviewsAdapter
        reviewsAdapter.submitList(allReviews)

        val sum = allReviews.sumByDouble { it.rate.toDouble() }
        binding.totalRateReviews.text = String.format(Locale.getDefault(), "%.1f", sum.toFloat() / allReviews.size)

        binding.numberOfRate.text="${allReviews.size} rating"
        val ratingCounts = getRatingCounts()
        binding.progressBar1.max=allReviews.size
        binding.progressBar2.max=allReviews.size
        binding.progressBar3.max=allReviews.size
        binding.progressBar4.max=allReviews.size
        binding.progressBar5.max=allReviews.size

        binding.progressBar1.progress = ratingCounts[0]
        binding.progressBar2.progress = ratingCounts[1]
        binding.progressBar3.progress = ratingCounts[2]
        binding.progressBar4.progress = ratingCounts[3]
        binding.progressBar5.progress = ratingCounts[4]
        binding.textView1.text = ratingCounts[0].toString()
        binding.textView2.text = ratingCounts[1].toString()
        binding.textView3.text = ratingCounts[2].toString()
        binding.textView4.text = ratingCounts[3].toString()
        binding.textView5.text = ratingCounts[4].toString()

    }
    fun getRatingCounts(): List<Int> {
        val ratingCounts = mutableListOf<Int>()
        for (i in 1..5) {
            ratingCounts.add(allReviews.count { it.rate.toInt() == i })
        }
        return ratingCounts
    }

}