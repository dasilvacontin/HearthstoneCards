package in.dasilvacont.hearthstonecards.app;

/**
 * Created by dasilvacontin on 17/03/15.
 */

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.dasilvacont.hearthstonecards.R;
import in.dasilvacont.hearthstonecards.app.data.CardContract;
import in.dasilvacont.hearthstonecards.app.data.CardContract.CardEntry;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final int DETAIL_LOADER = 0;

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

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

            TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
            detailTextView.setText(
                String.format(
                "Card Name: %s\n" +
                "Card ID: %s\n" +
                "Type: %s\n" +
                "Rarity: %s\n" +
                "Mana Cost: %s\n" +
                "Attack/Heath: %s/%s\n" +
                "Player Class: %s\n\n" +
                "Text: %s",
                        data.getString(COL_CARD_NAME),
                        data.getString(COL_CARD_ID),
                        data.getString(COL_TYPE),
                        data.getString(COL_RARITY),
                        data.getString(COL_COST),
                        data.getString(COL_ATTACK),
                        data.getString(COL_HEALTH),
                        data.getString(COL_PLAYER_CLASS),
                        data.getString(COL_TEXT))
            );
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }
}