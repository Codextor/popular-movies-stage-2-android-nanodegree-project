package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    private int mNumberItems;

    /**
     * Constructor for Video Adapter that accepts a number of items to display
     *
     * @param numberOfItems Number of items to display in list
     */
    public VideoAdapter(int numberOfItems) {
        mNumberItems = numberOfItems;
    }

    /**
     * Cache of the children views for a list item.
     */
    class VideoViewHolder extends RecyclerView.ViewHolder {

        public final TextView listItemVideoView;

        /**
         * Constructor for our ViewHolder.
         *
         * @param itemView The View that you inflated in
         *                 {@link VideoAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public VideoViewHolder(View itemView) {

            super(itemView);
            listItemVideoView = itemView.findViewById(R.id.tv_video_title);
        }

        /**
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {

            listItemVideoView.setText("Trailer # " + String.valueOf(listIndex));
        }
    }

    /**
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new VideoViewHolder that holds the View for each list item
     */
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        VideoViewHolder viewHolder = new VideoViewHolder(view);

        return viewHolder;
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        return mNumberItems;
    }
}
