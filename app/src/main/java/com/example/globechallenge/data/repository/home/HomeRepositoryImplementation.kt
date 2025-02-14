package com.example.globechallenge.data.repository.home

import com.example.globechallenge.data.model.models.home.Genre
import com.example.globechallenge.data.model.models.home.Movie
import com.example.globechallenge.data.model.models.home.MovieToGenre
import com.example.globechallenge.data.network.Api

class HomeRepositoryImplementation : HomeRepository {

    private val movieByGenre = ArrayList<MovieToGenre>()
    private val service = Api.serviceMoviesDB()

    override suspend fun getGenre(): List<Genre> {
        return service.getGenre(Api.API_KEY)
            .genres
            .map {
                Genre(it.id, it.name)
            }
    }

    override suspend fun getMovie(): List<Movie> {
        return service.getMovie(Api.API_KEY)
            .results
            .map {
                Movie(it.id.toString(), it.title, it.genreIds, it.posterPath)
            }
    }

    override suspend fun getMovieByGenre(): ArrayList<MovieToGenre> {
        getGenre().forEach { genre ->
            val listMovies = selectMovieToGenre(genre.id)
            if(listMovies.size != 0) movieByGenre.add(MovieToGenre(genre.id, genre.name, listMovies))
        }
        return movieByGenre
    }

    override suspend fun selectMovieToGenre(id: Int): MutableList<Movie> {
        val movies: MutableList<Movie> = ArrayList()
        repeat(getGenre().size) {
            getMovie().forEach { movie ->
                movie.genre.forEach {
                    if (it == id) {
                        movies.add(Movie(movie.id, movie.name, movie.genre, movie.image))
                    }
                }
            }
            return movies
        }
        return movies
    }
}