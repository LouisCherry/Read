package com.read.pan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.read.pan.R;
import com.read.pan.entity.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by pan on 2016/5/9.
 */
public class BookStoreAdapter extends RecyclerView.Adapter<BookStoreAdapter.ViewHolder>{
    private ArrayList<Book> mData;
    private LayoutInflater mInflater;
    private boolean isEditMode = false;
    private int[] itemState;
    public BookStoreAdapter(Context context, ArrayList<Book> mData) {
        mInflater = LayoutInflater.from(context);
        this.mData = mData;
        itemState = new int[mData.size()];
        init();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookshelf_tem_gridview, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

    }

    //将数据与界面进行绑定
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position, List<Object> payloads) {
        onBindViewHolder(viewHolder,position);
        if(mOnItemClickLitener!=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.itemView,pos);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(viewHolder.itemView,pos);
                    return false;
                }
            });
        }
    }

    //获取数据数量
    @Override
    public int getItemCount() {
        if(mData!=null&&!mData.isEmpty())
            return mData.size();
        else
            return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public ArrayList<Book> getmData() {
        return mData;
    }
    public void setmData(ArrayList<Book> smData) {
        this.mData =smData;
        itemState = new int[smData.size()];
        init();
    }

    private void init() {

        for (int i = 0; i < mData.size(); i++) {
            itemState[i] = 0;
        }
    }
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
