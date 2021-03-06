package com.abdymalikmulky.perfilman.app.data.movie;

import android.content.Context;

import com.abdymalikmulky.perfilman.util.ConstantsUtil;
import com.abdymalikmulky.perfilman.util.NetworkUtil;

import java.util.List;

/**
 * Bismillahirrahmanirrahim
 * Created by abdymalikmulky on 7/7/17.
 */

public class MovieRepo implements MovieDataSource {

    private Context context;
    private MovieLocal movieLocal;
    private MovieRemote movieRemote;

    public MovieRepo(Context context, MovieLocal movieLocal, MovieRemote movieRemote) {
        this.context = context;

        this.movieLocal = movieLocal;
        this.movieRemote = movieRemote;
    }

    @Override
    public void load(int page, String filter, final LoadMoviesCallback callback) {
        //If sort by favorites, just go to local data
        if(filter.equals(ConstantsUtil.MOVIE_LIST_SORT_BY_MY_FAVORITES)) {
            movieLocal.load(page, filter, new LoadMoviesCallback() {
                @Override
                public void onLoaded(List<Movie> movies) {
                    callback.onLoaded(movies);
                }

                @Override
                public void onFailed(String errorMessage) {
                    callback.onFailed(errorMessage);
                }
            });

        } else {
            //Check if network is available
            if(NetworkUtil.isNetworkAvailable(context)) {
                movieRemote.load(page, filter, new LoadMoviesCallback() {
                    @Override
                    public void onLoaded(List<Movie> movies) {
                        saveMovieOnLocal(movies);
                        callback.onLoaded(movies);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        callback.onFailed(errorMessage);
                    }
                });
            } else {
                movieLocal.load(page, filter, new LoadMoviesCallback() {
                    @Override
                    public void onLoaded(List<Movie> movies) {
                        callback.onLoaded(movies);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        callback.onFailed(errorMessage);
                    }
                });
            }

        }

    }

    private void saveMovieOnLocal(List<Movie> movies) {
        for (Movie movie : movies) {
            //if exist on db local, dont save
            if(!movieLocal.isExist(movie.getId())) {
                movieLocal.save(movie);
            }
        }
    }
}
