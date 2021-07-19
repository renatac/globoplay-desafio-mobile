package com.example.globechallenge.ui.details.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.globechallenge.R
import com.example.globechallenge.data.model.features.home.MovieToGenre
import com.example.globechallenge.data.repository.details.MovieDetailsRepository
import com.example.globechallenge.databinding.ActivityMovieDetailsBinding
import com.example.globechallenge.helper.SharedPreferenceHelper
import com.example.globechallenge.helper.concatGenre
import com.example.globechallenge.helper.loadImage
import com.example.globechallenge.helper.setDrawable
import com.example.globechallenge.ui.details.adapters.MovieInfoAdapter
import com.example.globechallenge.ui.details.fragments.DetailsFragment
import com.example.globechallenge.ui.details.fragments.WatchTooFragment
import com.example.globechallenge.ui.details.viewmodels.MovieDetailsViewModel

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var viewModel: MovieDetailsViewModel
    private val detailFragment = DetailsFragment()
    private val watchTooFragment = WatchTooFragment()
    private var movieToGenre : ArrayList<MovieToGenre>? = ArrayList()
    private var imageString = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        setupPageView()
        setupViewModel()
        getValuesIntent()
        setValues()
        val isFavorite = SharedPreferenceHelper(this@MovieDetailsActivity)
            .getIsFavoriteSharedPreferences()
        settingBtnMyList(isFavorite)
        setupButtons()
    }

    private fun settingBtnMyList(isFavorite: Boolean) {
            with(binding) {
                if(isFavorite) {
                    btnMyList.setDrawable(this@MovieDetailsActivity, R.drawable.ic_home_menu)
                    btnMyList.text = getString(R.string.added)
                 //   val imagesList = getMovieImages(movieToGenre!!)
                    val a = imageString

                } else {
                    btnMyList.setDrawable(this@MovieDetailsActivity, R.drawable.ic_star)
                    btnMyList.text = getString(R.string.btn_my_list)
                    val a = imageString
                }
            }
    }

//    private fun getMovieImages(movieToGenre: ArrayList<MovieToGenre>): MutableList<String> {
//        val moviesList = mutableListOf<Movie>()
//        movieToGenre.forEach{ movieToGenreItem ->
//            moviesList.addAll(movieToGenreItem.getMovies() as MutableList<Movie>)
//        }
//        val imagesList = mutableListOf<String>()
//        moviesList.forEach { movie->
//            imagesList.add(movie.image)
//        }
//        return imagesList
//    }

    private fun setupButtons() {
        binding.btnWatch.setOnClickListener {

        }
        binding.btnMyList.setOnClickListener {
            settingBtnMyList(getTheChangefromIsFavorite())
        }
    }

    private fun getTheChangefromIsFavorite(): Boolean{
        val isFavorite = SharedPreferenceHelper(this@MovieDetailsActivity)
            .getIsFavoriteSharedPreferences()
        SharedPreferenceHelper(this@MovieDetailsActivity)
            .setIsFavoriteSharedPreferences(!isFavorite)
        return isFavorite
    }

    private fun setupPageView() {
        val adapter = MovieInfoAdapter(supportFragmentManager)
        adapter.addFragment(watchTooFragment, WatchTooFragment.TITLE_MY_FAVORITE)
        adapter.addFragment(detailFragment, DetailsFragment.TITLE_DETAIL)
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            MovieDetailsViewModel
                .MovieDetailViewModelFactory(MovieDetailsRepository())
        )
            .get(MovieDetailsViewModel::class.java)
    }

    private fun getValuesIntent() {
        intent.getStringExtra(EXTRA_ID).run {
            viewModel.getMovieDetail(this.toString())
        }
        intent.getStringExtra(EXTRA_ID).run {
            viewModel.getMovieCreditToGetCast(this.toString())
        }
        movieToGenre = intent.getParcelableArrayListExtra(EXTRA_MOVIE_TO_GENRE)
        watchTooFragment.setMovieToGenre(movieToGenre)

    }

    private fun setValues() {
        viewModel.movieDetail.observe(this) {
            binding.txtTitle.text = it.title
            binding.txtDescription.text = it.overview
            binding.txtGenre.text = it.genres.concatGenre()
            binding.imgBlur.loadImage(it.postPath, true)
            binding.imgMovie.loadImage(it.postPath)
            imageString = it.postPath
            detailFragment.setMovie(it)
        }

        viewModel.movieCreditToGetCast.observe(this) {
            detailFragment.setMovieCast(it)
        }
    }

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_MOVIE_TO_GENRE = "EXTRA_MOVIE_TO_GENRE"
        fun getIntentMovieDetail(context: Context): Intent {
            return Intent(context, MovieDetailsActivity::class.java)
        }
    }
}