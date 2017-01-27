package me.cepeda.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import me.cepeda.popularmovies.R;
import me.cepeda.popularmovies.models.Movie;
import me.cepeda.popularmovies.utils.NetworkUtils;

/**
 * Created by CEPEDA on 22/1/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Movie[] movies;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Context context = holder.mPosterImageView.getContext();
        String posterPath = movies[position].getMoviePosterPath();
        URL posterURL = NetworkUtils.buildMovieThumbnailURL(posterPath);
        Picasso.with(context).load(String.valueOf(posterURL)).into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.length;
        } else {
            return 0;
        }
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movieClicked = movies[position];
            mClickHandler.onClick(movieClicked);
        }
    }
}
