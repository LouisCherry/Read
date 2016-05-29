package com.read.pan.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.pan.R;
import com.read.pan.util.ReadUtils;
import com.yamin.reader.activity.CoreReadActivity;
import com.yamin.reader.adapter.ScanFileAdapter;
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.model.Book;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardGridView;


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
    //    @BindView(R.id.shelf_recycler)
    //    RecyclerView shelfRecycler;
    @BindView(R.id.carddemo_grid_base1)
    CardGridView carddemoGridBase1;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Book> shelfData = null;
    private final int BOOK_SHELF = 0;
    private final int BOOK_FAVORITE = 2;
    private final int BOOK_INIT = 3;
    private ContentResolver resolver;
    private ArrayList<ScanFileAdapter.FileInfo> mFileLists;
    private FBReaderApp myFBReaderApp;
    private CardGridArrayAdapter mCardArrayAdapter;
    private ArrayList<Card> cards = new ArrayList<Card>();
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
        initCards();
        return view;
    }

    @Override
    public void onStart() {
//        initCards();
        myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
        if (myFBReaderApp == null) {
            myFBReaderApp = new FBReaderApp(getActivity(),
                    new BookCollectionShadow());
        }
        if(getCollection().status()== IBookCollection.Status.NotStarted)
            getCollection().bindToService(getActivity(), null);
        new sdScanAysnTask(3).execute();
        super.onStart();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        if(getCollection().status()!= IBookCollection.Status.NotStarted)
            getCollection().unbind();
        super.onPause();
    }

    @Override
    public void onDestroy() {
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
                    //                    bookshelfAdapter.setmData(shelfData);
                    //                    bookshelfAdapter.notifyDataSetChanged();
                    mCardArrayAdapter.notifyDataSetChanged();
                    //                    updateView();
            }
            super.handleMessage(msg);
        }
    };

    private void initCards() {
        mCardArrayAdapter = new CardGridArrayAdapter(getActivity(), cards);
        carddemoGridBase1.setAdapter(mCardArrayAdapter);
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
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                }
                GetFiles(Environment.getExternalStorageDirectory());
            }
            // 1
            if (forWhat == 1) {
                //                DoneScanLocal();
            }
            // 2
            if (forWhat == 2) {
                loadShelfData();
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
                shelfData = DbDataOperation.getBookInfo(resolver);
                //                mABdapter.setmData(shelfData);
                //                if(mABdapter.isEditMode()){
                //                    mABdapter.setEditMode(false);
                //                }
                carddemoGridBase1.setAdapter(mCardArrayAdapter);
                mCardArrayAdapter.notifyDataSetChanged();
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
        cards.clear();
        for (int i = 0; i < shelfData.size(); i++) {
            ShelfCard card = new ShelfCard(getActivity());
            card.index=i;
            card.headerTitle = shelfData.get(i).getBookName();
            card.secondaryTitle = shelfData.get(i).getBookSize();
            if (TextUtils.isEmpty(shelfData.get(i).getBookProgress())) {
                Resources resources=getResources();
                String third=resources.getString(R.string.read_no);
                card.thirdTitle=third;
            } else {
                String third=shelfData.get(i).getBookProgress();
                card.thirdTitle=third;
            }
//            ZLFile file = ZLFile.createFileByPath(shelfData.get(i).getBookPath());
//            org.geometerplus.fbreader.book.Book book = createBookForFile(file);
//            String cover = BookUtil.getCover(book).getURI();
            String cover=null;
            if(cover!=null){

            }else{
                card.resourceIdThumbnail=R.drawable.empty_icon;
            }
            card.init();
            final int a=i;
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Book b = shelfData.get(a);
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
                    }else{
                        DbDataOperation.deleteBook(resolver,b.getBookId());
                        Snackbar.make(getActivity().getWindow().getDecorView(),"书籍失效",Snackbar.LENGTH_SHORT).show();
                        ZLFile file = ZLFile.createFileByPath(shelfData.get(a)
                                .getBookPath());
                        org.geometerplus.fbreader.book.Book book = createBookForFile(file);
                        if (book != null) {
                            myFBReaderApp.Collection.removeBook(book, false);
                        }
                        shelfData.remove(a);
                        new sdScanAysnTask(2).execute();
                    }
                }
            });
            cards.add(card);
        }
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

    class ShelfCard extends Card {
        protected int resourceIdThumbnail = -1;
        protected int count;
        protected String headerTitle;
        protected String secondaryTitle;
        protected float rating;
        protected String thirdTitle;
        TextView cardShelfRemark;
        TextView cardShelfSize;
        protected int index;
        protected String urlResource;
        public ShelfCard(Context context) {
            super(context, R.layout.card_shelf);
        }

        public ShelfCard(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        public void init() {
            CardHeader header = new CardHeader(getContext(), R.layout.native_inner_gplay_header);
            header.setButtonOverflowVisible(true);
            header.setTitle(headerTitle);
            header.setPopupMenu(R.menu.shelf_book, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    Book b = shelfData.get(index);
                    DbDataOperation.deleteBook(resolver,b.getBookId());
                    Snackbar.make(getActivity().getWindow().getDecorView(),"删除成功",Snackbar.LENGTH_SHORT).show();
                    ZLFile file = ZLFile.createFileByPath(shelfData.get(index)
                            .getBookPath());
                    org.geometerplus.fbreader.book.Book book = createBookForFile(file);
                    if (book != null) {
                        myFBReaderApp.Collection.removeBook(book, false);
                    }
                    shelfData.remove(index);
                    new sdScanAysnTask(2).execute();
                }
            });
            addCardHeader(header);
            GplayGridThumb thumbnail = new GplayGridThumb(getContext());
            if (resourceIdThumbnail > -1)
                thumbnail.setDrawableResource(resourceIdThumbnail);
            else
                thumbnail.setDrawableResource(resourceIdThumbnail);
            addCardThumbnail(thumbnail);

            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Do something
                }
            });
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            cardShelfRemark= (TextView) view.findViewById(R.id.card_shelf_remark);
            cardShelfSize= (TextView) view.findViewById(R.id.card_shelf_size);
            cardShelfRemark.setText(thirdTitle);
            cardShelfSize.setText(secondaryTitle);

        }

        class GplayGridThumb extends CardThumbnail {

            public GplayGridThumb(Context context) {
                super(context);
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
//                viewImage.getLayoutParams().width = 196;
//                viewImage.getLayoutParams().height = 196;

            }
        }

    }
    private String returnSuffix(String fileName){
        if (fileName.lastIndexOf(".") > 0){
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            return fileSuffix;
        }
        return null;
    }
}
