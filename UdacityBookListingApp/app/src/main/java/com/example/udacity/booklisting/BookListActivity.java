package com.example.udacity.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<Book>>{


    public static final String LOG_TAG = BookListActivity.class.getName();
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int BOOK_LOADER_ID = 1;

    private EditText mEtSearch;
    private String mSearchKey;
    private ImageView mSearchButton;
    private ListView mBookListView;
    private BookAdapter mAdapter;
    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        mEtSearch = (EditText) findViewById(R.id.et_search);

        String prevKey;
        boolean isSavedState = false;
        if(savedInstanceState != null){
            prevKey = savedInstanceState.getString("prev_key");
            isSavedState = true;
            if(prevKey != null){
                mEtSearch.setText(prevKey);
                mEtSearch.setSelection(prevKey.length());
            }
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mBookListView = (ListView) findViewById(R.id.book_list);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mBookListView.setEmptyView(mEmptyView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        mBookListView.setAdapter(mAdapter);
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                Book cutrentBook = mAdapter.getItem(position);
                String url = cutrentBook.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        mSearchButton = (ImageView) findViewById(R.id.action_search);
        mSearchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                initiateSearch(true);
            }
        });

        if(isSavedState){
            initiateSearch(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        mSearchKey = mEtSearch.getText().toString();
        outState.putString("prev_key", mSearchKey);
    }

    private void initiateSearch(boolean isNewSearch){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            LoaderManager loaderManager = getLoaderManager();
           if(isNewSearch){
                mSearchKey = mEtSearch.getText().toString();
                loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
            }else{
                loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            }
        }else{
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle){

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        return new BookLoader(this, GOOGLE_BOOKS_URL, mSearchKey);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books){

        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setText(R.string.empty_view);

        if(books != null && !books.isEmpty()){
            mAdapter.clear();
           mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader){
        mAdapter.clear();
    }
}
