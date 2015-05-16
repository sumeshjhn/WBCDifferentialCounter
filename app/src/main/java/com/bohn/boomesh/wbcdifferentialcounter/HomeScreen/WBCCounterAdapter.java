package com.bohn.boomesh.wbcdifferentialcounter.homescreen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bohn.boomesh.wbcdifferentialcounter.R;
import com.bohn.boomesh.wbcdifferentialcounter.models.WhiteBloodCell;

import java.util.List;

public class WBCCounterAdapter extends RecyclerView.Adapter<WBCCounterAdapter.ViewHolder> {

    public interface OnItemClickedListener {
        void onItemClicked(int pPosition, View pView);
    }

    private final List<WhiteBloodCell> mListOfWBC;
    private OnItemClickedListener mItemClickedListener;

    public WBCCounterAdapter(List<WhiteBloodCell> pList, OnItemClickedListener pListener) {
        mListOfWBC = pList;
        mItemClickedListener = pListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.differential_cell_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListOfWBC == null) {
            return;
        }

        final WhiteBloodCell whiteBloodCell = mListOfWBC.get(position);
        holder.mCounterTextView.setText(WhiteBloodCell.getFormattedCellCount(whiteBloodCell.getCount()));
        holder.mCellImageView.setImageResource(whiteBloodCell.getType().mImageResourceId);
        holder.mCellNameTextView.setText(whiteBloodCell.getType().mStringResourceId);
    }

    @Override
    public int getItemCount() {
        return mListOfWBC.size();
    }

    public void setItemClickedListener(OnItemClickedListener pListener) {
        mItemClickedListener = pListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mCounterTextView;
        public final ImageView mCellImageView;
        public final TextView mCellNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCounterTextView = (TextView) itemView.findViewById(R.id.cell_count_text_view);
            mCellImageView = (ImageView) itemView.findViewById(R.id.cell_img);
            mCellNameTextView = (TextView) itemView.findViewById(R.id.cell_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickedListener != null) {
                mItemClickedListener.onItemClicked(getAdapterPosition(), v);
            }
        }
    }
}
