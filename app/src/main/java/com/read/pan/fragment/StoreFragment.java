package com.read.pan.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.read.pan.R;
import com.read.pan.adapter.BookStoreAdapter;
import com.read.pan.entity.Book;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;
import com.read.pan.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GridLayoutManager gridLayoutManager;
    @BindView(R.id.store_recycler)
    RecyclerView storeRecycler;
    @BindView(R.id.refresher)
    SwipeRefreshLayout refresher;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //无网状态
    private static final int NONET = 101;
    private OnFragmentInteractionListener mListener;
    private BookStoreAdapter bookStoreAdapter;
    private ArrayList<Book> books=new ArrayList<>();
    //分页起始位置
    private int offset;
    //分页一页的条数
    private int limit = 5;
    private final int REFRESH=1;
    private View rootView;
    public StoreFragment() {
        // Required empty public constructor
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case REFRESH:
                    bookStoreAdapter.notifyDataSetChanged();
                    refresher.setRefreshing(false);
                    break;
                case NONET:
                    refresher.setRefreshing(false);
                    Snackbar.make(getView(), "请检查网络", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, rootView);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData(rootView,0);
    }

    private void init(final View view) {
        offset = 0;
        //给Recycler设置gridView布局
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        storeRecycler.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        storeRecycler.setHasFixedSize(true);
        //设置Item增加、移除动画
        storeRecycler.setItemAnimator(new DefaultItemAnimator());
        bookStoreAdapter = new BookStoreAdapter(getContext(), books);
        storeRecycler.setAdapter(bookStoreAdapter);
        //scroollListener在不使用时，要记得移除
        storeRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem=-1;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == bookStoreAdapter.getItemCount()) {
                    offset=lastVisibleItem+1;
                    refreshData(rootView,offset);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            }
        });
        bookStoreAdapter.setOnItemClickLitener(new BookStoreAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(rootView,books.get(position).getBookName(),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!SystemUtil.isNetworkAvailable(getActivity())) {
                    Message message = new Message();
                    message.what = NONET;
                    mHandler.sendMessage(message);
                } else {
                    refreshData(rootView,0);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void refreshData(final View rootView,int offset){
        if(offset==0){
            books.clear();
        }
        refresher.setRefreshing(true);
        RestClient.bookApi().topList(offset, limit).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                int code = response.code();
                if (code == ResultCode.SUCCESS) {
                    List<Book> books1 = response.body();
                    books.addAll(books1);
//                    bookStoreAdapter.setmData(books);
                    Message message = new Message();
                    message.what = REFRESH;
                    mHandler.sendMessage(message);
                }
                if (code == ResultCode.EMPTYLIST) {
                    Snackbar.make(rootView, "暂无更多信息", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    refresher.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Snackbar.make(rootView, "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                refresher.setRefreshing(false);
            }
        });
    }
}
