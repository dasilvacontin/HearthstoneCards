/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.dasilvacont.hearthstonecards.app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import in.dasilvacont.hearthstonecards.R;
import in.dasilvacont.hearthstonecards.app.data.CardContract;
import in.dasilvacont.hearthstonecards.app.data.CardDbHelper;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class CardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = CardFragment.class.getSimpleName();

    private static final int CARD_LOADER = 0;
    private CardAdapter mCardAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    
    private static final String[] CARD_COLUMNS = {
            CardContract.CardEntry._ID,
            CardContract.CardEntry.COLUMN_CARD_ID,
            CardContract.CardEntry.COLUMN_CARD_NAME,
            CardContract.CardEntry.COLUMN_TYPE,
            CardContract.CardEntry.COLUMN_RARITY,
            CardContract.CardEntry.COLUMN_COST,
            CardContract.CardEntry.COLUMN_ATTACK,
            CardContract.CardEntry.COLUMN_HEALTH,
            CardContract.CardEntry.COLUMN_TEXT,
            CardContract.CardEntry.COLUMN_PLAYER_CLASS
    };

    // These indices are tied to CARD_COLUMNS.  If CARD_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_CARD_ID = 1;
    static final int COL_CARD_NAME = 2;
    static final int COL_TYPE = 3;
    static final int COL_RARITY = 4;
    static final int COL_COST = 5;
    static final int COL_ATTACK = 6;
    static final int COL_HEALTH = 7;
    static final int COL_TEXT = 8;
    static final int COL_PLAYER_CLASS = 9;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri cardUri);
    }

    public CardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cardfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_map) {
            // top kek
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The CardAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        String sortOrder = CardContract.CardEntry.COLUMN_CARD_NAME + " ASC";
        Cursor cur = getActivity().getContentResolver().query(
                CardContract.CardEntry.CONTENT_URI,
                null, null, null, sortOrder
        );

        if (cur.getCount() == 0) fetchCards();

        mCardAdapter = new CardAdapter(getActivity(), cur, 0);

        Log.d(LOG_TAG, "Inflating CardFragment...");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_card);
        mListView.setAdapter(mCardAdapter);

        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    /*
                    ((Callback) getActivity())
                            .onItemSelected(CardContract.CardEntry.buildCardUri(
                                    cursor.getString(COL_CARD_ID)
                            ));
                            */
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, CardContract.CardEntry.buildCardUriNumerical(cursor.getLong(COL_ID)));
                    startActivity(intent);
                }
                mPosition = position;

            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            mListView.smoothScrollToPosition(mPosition);
            mListView.setSelection(mPosition);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CARD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchCards() {
        FetchCardTask weatherTask = new FetchCardTask(getActivity());
        weatherTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = CardContract.CardEntry.COLUMN_CARD_NAME + " ASC";
        return new CursorLoader(getActivity(),
            CardContract.CardEntry.CONTENT_URI,
            null,
            null,
            null,
            sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        mCardAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCardAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}