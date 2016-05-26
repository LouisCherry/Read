package com.read.pan.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.read.pan.R;
import com.read.pan.adapter.BookshelfAdapter;
import com.read.pan.util.ReadUtils;
import com.yamin.reader.activity.CoreReadActivity;
import com.yamin.reader.adapter.ScanFileAdapter;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.model.Book;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookshelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookshelfFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.shelf_recycler)
    RecyclerView shelfRecycler;
    GridLayoutManager gridLayoutManager;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Book> shelfData = new ArrayList<Book>();
    private final int BOOK_SHELF = 0;
    private final int BOOK_FAVORITE = 2;
    private final int BOOK_INIT = 3;
    private BookshelfAdapter bookshelfAdapter;
    private ContentResolver resolver;
    private ArrayList<ScanFileAdapter.FileInfo> mFileLists;
    private FBReaderApp myFBReaderApp;

    public BookshelfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookshelfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookshelfFragment newInstance(String param1, String param2) {
        BookshelfFragment fragment = new BookshelfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        resolver = getContext().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onStart() {
        myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
        if (myFBReaderApp == null) {
            myFBReaderApp = new FBReaderApp(getActivity(),
                    new BookCollectionShadow());
        }
        getCollection().bindToService(getActivity(), null);
        new sdScanAysnTask(3).execute();
        super.onStart();
    }

    @Override
    public void onResume() {
//        myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
//        if (myFBReaderApp == null) {
//            myFBReaderApp = new FBReaderApp(getActivity(),
//                    new BookCollectionShadow());
//        }
//        getCollection().bindToService(getActivity(), null);
//        new sdScanAysnTask(3).execute();
        super.onResume();
    }

    @Override
    public void onPause() {
//        getCollection().unbind();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getCollection().unbind();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case BOOK_SHELF:
//                    updateLayoutContent();
                    break;

                case BOOK_FAVORITE:
//                    startActivity(new Intent(MainActivity.this,BookFavoriteActivity.class));
                    break;

                case BOOK_INIT:
                    bookshelfAdapter.setmData(shelfData);
                    bookshelfAdapter.notifyDataSetChanged();
//                    updateView();
            }
            super.handleMessage(msg);
        }
    };
    //初始化操作
    private void init(){
        //给Recycler设置gridView布局
        gridLayoutManager=new GridLayoutManager(getContext(),3);
        shelfRecycler.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        shelfRecycler.setHasFixedSize(true);
        bookshelfAdapter=new BookshelfAdapter(getContext(),shelfData);
        shelfRecycler.setAdapter(bookshelfAdapter);
        bookshelfAdapter.setOnItemClickLitener(new BookshelfAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Book b=shelfData.get(position);
                // File Exist
                if (ReadUtils.fileIsExists(b.getBookPath())) {
                    ZLFile file = ZLFile.createFileByPath(b.getBookPath());
                    org.geometerplus.fbreader.book.Book book = createBookForFile(file);
                    if (book != null) {
                        CoreReadActivity.openBookActivity(getActivity(), book,
                                null);
                        getActivity().overridePendingTransition(
                                R.anim.activity_enter, R.anim.activity_exit);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        //设置Item增加、移除动画
        shelfRecycler.setItemAnimator(new DefaultItemAnimator());
        new sdScanAysnTask(3).execute();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class sdScanAysnTask extends AsyncTask<Integer, Integer, String[]> {
        private int forWhat = 0;

        public sdScanAysnTask(int forWhat) {
            super();
            this.forWhat = forWhat;
        }

        protected void onPreExecute() {
            // 0
            if (forWhat == 0) {
                //
//                showLoading(MainActivity.this, "SD卡扫描中...");
//                if (mFileLists != null && mFileLists.size() > 0) {
//                    mFileLists.clear();
//                }
            }
            // 1
            if (forWhat == 1) {
//                showLoading(MainActivity.this, "正加入书架中...");
            }
            // 2
            if (forWhat == 2) {
//                showLoading(MainActivity.this, "正在删除书籍...");
            }
            if (forWhat == 3) {
                // showLoading(MainActivity.this, "初始化书架...");
            }
            super.onPreExecute();
        }

        protected String[] doInBackground(Integer... params) {
            // 0
            if (forWhat == 0) {
                if (!android.os.Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED)) {
                }
                GetFiles(Environment.getExternalStorageDirectory());
            }
            // 1
            if (forWhat == 1) {
//                DoneScanLocal();
            }
            // 2
            if (forWhat == 2) {
//                delLocalShelf();
            }
            if (forWhat == 3) {
                loadShelfData();
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            // 0
            if (forWhat == 0) {
//                stopLoading();
//                Toast.makeText(MainActivity.this,
//                        "扫描完毕，找到" + mFileLists.size() + "个文件",
//                        Toast.LENGTH_SHORT).show();
//                showPopupWindow(0, 0);
            }
            // 1
            if (forWhat == 1) {
//                stopLoading();
//                shelfData = DbDataOperation.getBookInfo(resolver);
//                mABdapter.setmData(shelfData);
//                mABdapter.notifyDataSetChanged();
//                updateView();
//                Toast.makeText(MainActivity.this, "成功添加到书架!",
//                        Toast.LENGTH_SHORT).show();
            }
            // 2
            if (forWhat == 2) {
//                stopLoading();
//                shelfData = DbDataOperation.getBookInfo(resolver);
//                mABdapter.setmData(shelfData);
//                if(mABdapter.isEditMode()){
//                    mABdapter.setEditMode(false);
//                }
//                mABdapter.notifyDataSetChanged();
//                //
//                updateView();
//                if (mPopuwindow != null && mPopuwindow.isShowing()) {
//                    mPopuwindow.dismiss();
//                }
//                Toast.makeText(MainActivity.this, "成功删除书籍!", Toast.LENGTH_SHORT)
//                        .show();
            }
            if (forWhat == 3) {
                // stopLoading();
                Message message = new Message();
                message.what = BOOK_INIT;
                mHandler.sendMessage(message);
            }
            super.onPostExecute(result);
        }
    }
    public void GetFiles(File filePath) {
        File[] files = filePath.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    GetFiles(files[i]);
                } else {
                    if (files[i].getName().toLowerCase().endsWith("." + "txt")
                            || files[i].getName().toLowerCase()
                            .endsWith("." + "epub")
                            || files[i].getName().toLowerCase()
                            .endsWith("." + "mobi")
                            || files[i].getName().toLowerCase()
                            .endsWith("." + "fb2")
                            || files[i].getName().toLowerCase()
                            .endsWith("." + "oeb")
                            || files[i].getName().toLowerCase()
                            .endsWith("." + "html")) {
                        ScanFileAdapter.FileInfo fileInfo = new ScanFileAdapter.FileInfo(
                                files[i].getAbsolutePath(), files[i].getName(),
                                files[i].length(), false);
                        // String scanName = files[i].getName();
                        if (fileInfo != null) {
                            mFileLists.add(fileInfo);
                        }
                    }
                }
            }
        }
    }
    private void loadShelfData() {
        shelfData = DbDataOperation.getBookInfo(resolver);
    }
    /*
     * private void openBook(String bookPath) { ZLFile file =
     * ZLFile.createFileByPath(bookPath); org.geometerplus.fbreader.book.Book
     * book = createBookForFile(file); if (book != null) {
     * CoreReadActivity.openBookActivity(MainActivity.this, book, null);
     * MainActivity.this.overridePendingTransition(R.anim.activity_enter,
     * R.anim.activity_exit); } }
     */
    private org.geometerplus.fbreader.book.Book createBookForFile(ZLFile file) {
        if (file == null) {
            return null;
        }
        org.geometerplus.fbreader.book.Book book = getCollection()
                .getBookByFile(file);
        if (book != null) {
            return book;
        }
        if (file.isArchive()) {
            for (ZLFile child : file.children()) {
                book = getCollection().getBookByFile(child);
                if (book != null) {
                    return book;
                }
            }
        }
        return null;
    }
    /*
 * private void handleIntent(Intent intent) { if
 * (Intent.ACTION_VIEW.equals(intent.getAction())) { // handles a click on a
 * search suggestion; launches activity to show word Intent wordIntent = new
 * Intent(this, SearchResultActivity.class);
 * wordIntent.setData(intent.getData()); startActivity(wordIntent); } else
 * if (Intent.ACTION_SEARCH.equals(intent.getAction())) { // handles a
 * search query String query = intent.getStringExtra(SearchManager.QUERY);
 * showResults(query); } }
 */
    private BookCollectionShadow getCollection() {
        return (BookCollectionShadow) myFBReaderApp.Collection;
    }

}
