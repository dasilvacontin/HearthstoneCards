
/**
 * Created by dasilvacontin on 16/03/15.
 */

package in.dasilvacont.hearthstonecards.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CardContract {

    public static final String CONTENT_AUTHORITY = "in.dasilvacont.hearthstonecards.app";
public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

public static final String PATH_CARDS = "card";

public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;

        // Table name
        public static final String TABLE_NAME = "cards";

        public static final String COLUMN_CARD_NAME = "name";
        public static final String COLUMN_CARD_ID = "card_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_RARITY = "rarity";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_ATTACK = "attack";
        public static final String COLUMN_HEALTH = "health";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_PLAYER_CLASS = "player_class";

        public static Uri buildCardUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static Uri buildCardUriNumerical(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }
    }
}
