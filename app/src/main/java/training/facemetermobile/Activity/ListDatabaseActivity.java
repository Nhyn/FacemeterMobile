package training.facemetermobile.Activity;

/**
 * Created by agrfiqie6136 on 04/10/2016.
 */

import android.app.ListActivity;
import android.app.LoaderManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

public class ListDatabaseActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Object> {

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[]{ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};
    // This is the select criteria
    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";
    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
       // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT / 2,
        //        LinearLayout.LayoutParams.WRAP_CONTENT / 2);
        //layoutParams.gravity = Gravity.CENTER;
        //progressBar.setLayoutParams(layoutParams);
        progressBar.setScaleY(0.2f);
        progressBar.setScaleX(0.2f);
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        //setTitle("Loanable fund");
    }

    @Override
    public android.content.Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Object> loader, Object data) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Object> loader) {

    }
}

