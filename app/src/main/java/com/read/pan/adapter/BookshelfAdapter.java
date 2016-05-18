package com.read.pan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamin.reader.R;
import com.yamin.reader.model.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pan on 2016/5/9.
 */
public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ViewHolder>{
    private ArrayList<Book> mData;
    private LayoutInflater mInflater;
    private boolean isEditMode = false;
    private int[] itemState;
    public BookshelfAdapter(Context context, ArrayList<Book> mData) {
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
        //书籍图片
        if(returnSuffix(mData.get(position).getBookName()).contains(".txt")){
            viewHolder.cover.setBackgroundResource(R.drawable.listview_txtcover);
        }
        else if(returnSuffix(mData.get(position).getBookName()).contains(".epub")){
            viewHolder.cover.setBackgroundResource(R.drawable.listview_epubcover);
        }
        else if(returnSuffix(mData.get(position).getBookName()).contains(".html")){
            viewHolder.cover.setBackgroundResource(R.drawable.listview_htmlcover);
        }
        else if(returnSuffix(mData.get(position).getBookName()).contains(".oeb")){
            viewHolder.cover.setBackgroundResource(R.drawable.listview_oebicon);
        }
        else if(returnSuffix(mData.get(position).getBookName()).contains(".mobi")){
            viewHolder.cover.setBackgroundResource(R.drawable.listview_mobiicon);
        }
        else{
            viewHolder.cover.setBackgroundResource(R.drawable.listview_othercover);
        }
        //书籍名
        viewHolder.tvBookName.setText(mData.get(position).getBookName());
        //书籍大小
        viewHolder.tvBookSize.setText(mData.get(position).getBookSize());
        viewHolder.tvBookSize.setTextColor(Color.RED);
        //未读已读
        if (TextUtils.isEmpty(mData.get(position).getBookProgress())) {
            viewHolder.tvBookProgress.setText(R.string.read_no);
        } else {
            viewHolder.tvBookProgress.setText(mData.get(position).getBookProgress());
        }
        //判读是否为可编辑状态
        if (isEditMode) {
            viewHolder.bookshelfFileSelectIcon.setVisibility(View.VISIBLE);
            if (itemState[position] == 0) {
                viewHolder.bookshelfFileSelectIcon.setBackgroundResource(R.drawable.checkbox_unselect);
            } else {
                viewHolder.bookshelfFileSelectIcon.setBackgroundResource(R.drawable.checkbox_selected);
            }
        } else {
            viewHolder.bookshelfFileSelectIcon.setVisibility(View.GONE);
        }
        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(mData.get(position));
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
        return mData.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBookProgress)
        TextView tvBookProgress;
        @BindView(R.id.tvBookName)
        TextView tvBookName;
        @BindView(R.id.tvBookSize)
        TextView tvBookSize;
        @BindView(R.id.bookshelfFileSelectIcon)
        ImageView bookshelfFileSelectIcon;
        @BindView(R.id.cover)
        RelativeLayout cover;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private String returnSuffix(String fileName){
        if (fileName.lastIndexOf(".") > 0){
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            return fileSuffix;
        }
        return null;
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
        void onItemLongClick(View view , int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
