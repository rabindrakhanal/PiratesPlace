package edu.ecu.cs.pirateplaces.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import edu.ecu.cs.pirateplaces.PirateBase;
import edu.ecu.cs.pirateplaces.PiratePlace;

import static edu.ecu.cs.pirateplaces.database.PiratePlaceDbSchema.*;

/**
 * Created by mhills on 10/12/17.
 */

public class PirateCursorWrapper extends CursorWrapper
{
    public PirateCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public PiratePlace getPiratePlace()
    {
        String uuidString = getString(getColumnIndex(PiratePlaceTable.Cols.UUID));
        String placeName = getString(getColumnIndex(PiratePlaceTable.Cols.PLACE_NAME));
        long lastVisitedDate = getLong(getColumnIndex(PiratePlaceTable.Cols.LAST_VISITED_DATE));

        PiratePlace piratePlace = new PiratePlace(UUID.fromString(uuidString));
        piratePlace.setPlaceName(placeName);
        piratePlace.setLastVisited(new Date(lastVisitedDate));

        return piratePlace;
    }

}
