package com.read.pan.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.read.pan.R;
import com.read.pan.entity.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pan on 2016/5/9.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<Book> mData;
    private LayoutInflater mInflater;
    private int[] itemState;

    public SearchAdapter(Context context, ArrayList<Book> mData) {
        mInflater = LayoutInflater.from(context);
        this.mData = mData;
        itemState = new int[mData.size()];
        init();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String imguri=mData.get(position).getCover();
        Uri uri=Uri.parse(imguri);
        viewHolder.storeTopImg.setImageURI(uri);
        viewHolder.storeTopTitle.setText(mData.get(position).getBookName());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(mData.get(position));
    }

    //将数据与界面进行绑定
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position, List<Object> payloads) {
        onBindViewHolder(viewHolder, position);
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, pos);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(viewHolder.itemView, pos);
                    return false;
                }
            });
        }
    }

    //获取数据数量
    @Override
    public int getItemCount() {
        if (mData != null && !mData.isEmpty())
            return mData.size();
        else
            return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.store_top_img)
        SimpleDraweeView storeTopImg;
        @BindView(R.id.store_top_title)
        TextView storeTopTitle;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ArrayList<Book> getmData() {
        return mData;
    }

    public void setmData(ArrayList<Book> smData) {
        this.mData = smData;
        itemState = new int[smData.size()];
        init();
    }

    private void init() {

        for (int i = 0; i < mData.size(); i++) {
            itemState[i] = 0;
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
