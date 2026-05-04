package com.harrowhaus.crypseal.runtime.storage.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EventIndexDao_Impl implements EventIndexDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventIndexEntity> __insertionAdapterOfEventIndexEntity;

  public EventIndexDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventIndexEntity = new EntityInsertionAdapter<EventIndexEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `event_index` (`id`,`sessionId`,`type`,`createdAt`,`summary`,`jsonlOffset`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventIndexEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSessionId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getCreatedAt());
        if (entity.getSummary() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSummary());
        }
        if (entity.getJsonlOffset() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getJsonlOffset());
        }
      }
    };
  }

  @Override
  public void insertEventIndex(final EventIndexEntity eventIndex) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfEventIndexEntity.insert(eventIndex);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<EventIndexEntity> getEventIndexForSession(final String sessionId) {
    final String _sql = "SELECT * FROM event_index WHERE sessionId = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
      final int _cursorIndexOfJsonlOffset = CursorUtil.getColumnIndexOrThrow(_cursor, "jsonlOffset");
      final List<EventIndexEntity> _result = new ArrayList<EventIndexEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final EventIndexEntity _item;
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        final String _tmpSessionId;
        _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
        final String _tmpType;
        _tmpType = _cursor.getString(_cursorIndexOfType);
        final String _tmpCreatedAt;
        _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
        final String _tmpSummary;
        if (_cursor.isNull(_cursorIndexOfSummary)) {
          _tmpSummary = null;
        } else {
          _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
        }
        final Long _tmpJsonlOffset;
        if (_cursor.isNull(_cursorIndexOfJsonlOffset)) {
          _tmpJsonlOffset = null;
        } else {
          _tmpJsonlOffset = _cursor.getLong(_cursorIndexOfJsonlOffset);
        }
        _item = new EventIndexEntity(_tmpId,_tmpSessionId,_tmpType,_tmpCreatedAt,_tmpSummary,_tmpJsonlOffset);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
