package in.dasilvacont.hearthstonecards.app;

/**
 * Created by dasilvacontin on 16/03/15.
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.dasilvacont.hearthstonecards.R;

/**
 * {@link CardAdapter} exposes a list of cards
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class CardAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    /**
     * Cache of the children views for a card list item.
     */
    public static class ViewHolder {
        public final TextView nameView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_textview);
        }
    }

    public CardAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read date from cursor
        String cardName = cursor.getString(CardFragment.COL_CARD_NAME);
        viewHolder.nameView.setText(cardName);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}