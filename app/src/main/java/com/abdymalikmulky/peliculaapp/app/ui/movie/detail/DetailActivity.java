package com.abdymalikmulky.peliculaapp.app.ui.movie.detail;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdymalikmulky.peliculaapp.R;
import com.abdymalikmulky.peliculaapp.app.data.movie.Movie;
import com.abdymalikmulky.peliculaapp.app.ui.movie.detail.reviews.ReviewFragment;
import com.abdymalikmulky.peliculaapp.app.ui.movie.detail.videos.VideoFragment;
import com.abdymalikmulky.peliculaapp.util.ConstantsUtil;
import com.abdymalikmulky.peliculaapp.util.DateTimeUtil;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.movie_detail_backdrop)
    ImageView movieDetailBackdrop;
    @BindView(R.id.movie_detail_title)
    TextView movieDetailTitle;
    @BindView(R.id.movie_detail_years)
    TextView movieDetailYears;
    @BindView(R.id.movie_detail_poster)
    ImageView movieDetailPoster;
    @BindView(R.id.movie_detail_overview)
    TextView movieDetailOverview;
    @BindView(R.id.movie_detail_rating)
    TextView movieDetailRating;
    @BindView(R.id.movie_detail_rating_count)
    TextView movieDetailRatingCount;
    @BindView(R.id.fragment_trailer)
    FrameLayout fragmentTrailer;


    private DetailContract.Presenter detailPresenter;
    private Movie movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setToolbar();


        try {
            movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(ConstantsUtil.INTENT_MOVIE));
        } catch (NullPointerException e) {
            Timber.e(e.toString());
        }

        detailPresenter = new DetailPresenter(this);

        setupTrailerFragment();
        setupReviewFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailPresenter.start();
    }

    private void setToolbar() {
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        collapsingToolbarLayout.setTitle(getString(R.string.title_detail_movie));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
        collapsingToolbarLayout.setTitleEnabled(true);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        } else {
            //
        }
    }


    //MOVIE
    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        detailPresenter = presenter;
    }

    @Override
    public void showMovie() {
        movieDetailTitle.setText(movie.getTitle());
        movieDetailOverview.setText(movie.getOverview());
        movieDetailYears.setText(DateTimeUtil.convertToHumanReadableDate(movie.getReleaseDate()));
        movieDetailRating.setText(String.valueOf(movie.getVoteAverage()));
        movieDetailRatingCount.setText(String.valueOf(movie.getVoteCount()));

        Picasso.with(getApplicationContext())
                .load(movie.getPosterPath())
                .placeholder(R.drawable.blank_movie_poster)
                .error(R.drawable.blank_movie_poster)
                .into(movieDetailPoster);

        Picasso.with(getApplicationContext())
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.blank_movie_poster)
                .error(R.drawable.blank_movie_poster)
                .into(movieDetailBackdrop);
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void setupTrailerFragment() {

        addFragmentMovieToActivity(R.id.fragment_trailer, new VideoFragment(), movie.getId());
    }
    private void setupReviewFragment() {
        addFragmentMovieToActivity(R.id.fragment_reviews, new ReviewFragment(), movie.getId());
    }
    private void addFragmentMovieToActivity(int fragmentLayout, Fragment fragmentObj, String movieId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsUtil.INTENT_MOVIE_ID, movieId);
        fragmentObj.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentLayout, fragmentObj);
        ft.commit();
    }
}
