package com.abdymalikmulky.perfilman.app.ui.movie.detail.videos;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abdymalikmulky.perfilman.R;
import com.abdymalikmulky.perfilman.app.data.video.Video;
import com.abdymalikmulky.perfilman.app.data.video.VideoLocal;
import com.abdymalikmulky.perfilman.app.data.video.VideoRemote;
import com.abdymalikmulky.perfilman.app.data.video.VideoRepo;
import com.abdymalikmulky.perfilman.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements VideoContract.View{

    @BindView(R.id.movie_detail_subtitle_trailer)
    TextView movieDetailSubtitleTrailer;
    @BindView(R.id.detail_list_trailer)
    RecyclerView listTrailer;

    Unbinder unbinder;

    private List<Video> videos;
    private VideoListAdapter videoListAdapter;

    private VideoLocal videoLocal;
    private VideoRemote videoRemote;
    private VideoRepo videoRepo;

    private VideoContract.Presenter videoPresenter;


    private String movieId;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoLocal = new VideoLocal();
        videoRemote = new VideoRemote();
        videoRepo = new VideoRepo(videoLocal, videoRemote);
        videoPresenter = new VideoPresenter(videoRepo, this);

        movieId = getArguments().getString(ConstantsUtil.INTENT_MOVIE_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailer, container, false);
        unbinder = ButterKnife.bind(this, view);

        initListTrailerLayout();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoPresenter.loadTrailers(movieId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initListTrailerLayout() {
        videos = new ArrayList<>();
        listTrailer.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        listTrailer.setLayoutManager(layoutManager);
        videoListAdapter = new VideoListAdapter(videos, this);
        listTrailer.setAdapter(videoListAdapter);
    }

    @Override
    public void setPresenter(VideoContract.Presenter presenter) {
        videoPresenter = presenter;
    }

    @Override
    public void showVideos(List<Video> videos) {
        this.videos = videos;
        videoListAdapter.refresh(videos);
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClicked(Video video) {
        ConstantsUtil.openVideoIntent(getActivity(), video);
    }
}