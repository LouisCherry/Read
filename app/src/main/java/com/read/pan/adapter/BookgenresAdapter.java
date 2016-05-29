package com.read.pan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.pan.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pan on 2016/5/9.
 */
public class BookgenresAdapter extends RecyclerView.Adapter<BookgenresAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private int[] typeText;
    private int[] typeImg;
    private static int[] types;
    public BookgenresAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        init();
    }
    public static int getType(int position){
        return types[position];
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genres_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.genresImg.setImageResource(typeImg[position]);
        viewHolder.genresText.setText(typeText[position]);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(types[position]);
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
        return typeText.length;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.genres_text)
        TextView genresText;
        @BindView(R.id.genres_img)
        ImageView genresImg;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void init() {
        typeText=new int[]{R.string.genres_1,R.string.genres_2,R.string.genres_3,R.string.genres_4,
                R.string.genres_5, R.string.genres_6,R.string.genres_7,R.string.genres_8,
                R.string.genres_9,R.string.genres_10,R.string.genres_11};
        typeImg=new int[]{R.drawable.genres_1,R.drawable.genres_2,R.drawable.genres_3,
                R.drawable.genres_4,R.drawable.genres_5,R.drawable.genres_6,R.drawable.genres_7,
                R.drawable.genres_8,R.drawable.genres_9,R.drawable.genres_10,R.drawable.genres_11,};
        types=new int[]{1,2,3,4,5,6,7,8,9,10,11};
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
