package com.example.barcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Books> mData;

    public BookRecyclerViewAdapter(Context mContext, List<Books> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater =LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.information_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mTvName.setText(mData.get(position).getBookName());
        holder.mTvAuthors.setText(mData.get(position).getAuthors());
        holder.mTvISBN.setText(String.valueOf(mData.get(position).getBsbn()));
    }

    @Override
    public int getItemCount() {
        return mData.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private TextView mTvAuthors;
        private TextView mTvISBN;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvName =  itemView.findViewById(R.id.Name);
            mTvAuthors =  itemView.findViewById(R.id.Author);
            mTvISBN =  itemView.findViewById(R.id.isbn_number);
        }
    }
}
