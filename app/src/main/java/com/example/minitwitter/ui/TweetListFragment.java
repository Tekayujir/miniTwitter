package com.example.minitwitter.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

public class TweetListFragment extends Fragment {


    private int tweetListType = 1;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyTweetRecyclerViewAdapter adapter;
    List<Tweet> tweetList;
    TweetViewModel tweetViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TweetListFragment() {
    }

    public static TweetListFragment newInstance(int tweetListType) {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        args.putInt(Constantes.TWEET_LIST_TYPE, tweetListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetViewModel = ViewModelProviders.of(getActivity())
            .get(TweetViewModel.class);

        if (getArguments() != null) {
            tweetListType = getArguments().getInt(Constantes.TWEET_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAzul));

        // Método que se lanza al refrescar la lista
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if( tweetListType == Constantes.TWEET_LIST_ALL) {
                    loadNewData();
                } else if( tweetListType == Constantes.TWEET_LIST_FAVS) {
                    loadNewFavData();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MyTweetRecyclerViewAdapter(
                getActivity(),
                tweetList
        );
        recyclerView.setAdapter(adapter);

        if( tweetListType == Constantes.TWEET_LIST_ALL) {
            loadTweetData();
        } else if( tweetListType == Constantes.TWEET_LIST_FAVS) {
            loadFavTweetData();
        }

        return view;
    }


    /**
     * Carga el adapter con la lista de tweets
     */
    private void loadTweetData() {
        tweetViewModel.getTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(@Nullable List<Tweet> tweets) {
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });
    }

    // Nuevos tweets para la funcionalidad del Swipe
    private void loadNewData() {
        tweetViewModel.getNewTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(@Nullable List<Tweet> tweets) {
                tweetList = tweets;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(tweetList);

                // desactivando observer para que no se vuelva a lanzar cuando creemos un nuevo tweet
                tweetViewModel.getNewTweets().removeObserver(this);
            }
        });
    }

    /**
     * Carga el adapter con la lista de tweets con favs
     */
    private void loadFavTweetData() {
        tweetViewModel.getFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });
    }

    // Nuevos tweets con favs para la funcionalidad del Swipe
    private void loadNewFavData() {
        tweetViewModel.getNewFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(tweetList);

                // desactivando observer para que no se vuelva a lanzar cuando creemos un nuevo tweet fav
                tweetViewModel.getNewFavTweets().removeObserver(this);
            }
        });
    }
}
