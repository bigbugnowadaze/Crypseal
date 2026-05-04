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
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProjectDao_Impl implements ProjectDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProjectEntity> __insertionAdapterOfProjectEntity;

  public ProjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProjectEntity = new EntityInsertionAdapter<ProjectEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `projects` (`id`,`name`,`rootPath`,`createdAt`,`updatedAt`,`lastSessionId`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProjectEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getRootPath());
        statement.bindString(4, entity.getCreatedAt());
        statement.bindString(5, entity.getUpdatedAt());
        if (entity.getLastSessionId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLastSessionId());
        }
      }
    };
  }

  @Override
  public void insertProject(final ProjectEntity project) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProjectEntity.insert(project);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<ProjectEntity> getAllProjects() {
    final String _sql = "SELECT * FROM projects";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfRootPath = CursorUtil.getColumnIndexOrThrow(_cursor, "rootPath");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final int _cursorIndexOfLastSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionId");
      final List<ProjectEntity> _result = new ArrayList<ProjectEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ProjectEntity _item;
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpRootPath;
        _tmpRootPath = _cursor.getString(_cursorIndexOfRootPath);
        final String _tmpCreatedAt;
        _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
        final String _tmpUpdatedAt;
        _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
        final String _tmpLastSessionId;
        if (_cursor.isNull(_cursorIndexOfLastSessionId)) {
          _tmpLastSessionId = null;
        } else {
          _tmpLastSessionId = _cursor.getString(_cursorIndexOfLastSessionId);
        }
        _item = new ProjectEntity(_tmpId,_tmpName,_tmpRootPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastSessionId);
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
