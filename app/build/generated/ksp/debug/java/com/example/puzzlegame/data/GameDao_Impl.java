package com.example.puzzlegame.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameDao_Impl implements GameDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GameStateEntity> __insertionAdapterOfGameStateEntity;

  public GameDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGameStateEntity = new EntityInsertionAdapter<GameStateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `game_state` (`id`,`currentScore`,`highScore`,`currentLevel`,`gridJson`,`availablePiecesJson`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GameStateEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCurrentScore());
        statement.bindLong(3, entity.getHighScore());
        statement.bindLong(4, entity.getCurrentLevel());
        statement.bindString(5, entity.getGridJson());
        statement.bindString(6, entity.getAvailablePiecesJson());
      }
    };
  }

  @Override
  public Object saveGameState(final GameStateEntity gameState,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGameStateEntity.insert(gameState);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getGameState(final Continuation<? super GameStateEntity> $completion) {
    final String _sql = "SELECT * FROM game_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GameStateEntity>() {
      @Override
      @Nullable
      public GameStateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final int _cursorIndexOfHighScore = CursorUtil.getColumnIndexOrThrow(_cursor, "highScore");
          final int _cursorIndexOfCurrentLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "currentLevel");
          final int _cursorIndexOfGridJson = CursorUtil.getColumnIndexOrThrow(_cursor, "gridJson");
          final int _cursorIndexOfAvailablePiecesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "availablePiecesJson");
          final GameStateEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCurrentScore;
            _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            final int _tmpHighScore;
            _tmpHighScore = _cursor.getInt(_cursorIndexOfHighScore);
            final int _tmpCurrentLevel;
            _tmpCurrentLevel = _cursor.getInt(_cursorIndexOfCurrentLevel);
            final String _tmpGridJson;
            _tmpGridJson = _cursor.getString(_cursorIndexOfGridJson);
            final String _tmpAvailablePiecesJson;
            _tmpAvailablePiecesJson = _cursor.getString(_cursorIndexOfAvailablePiecesJson);
            _result = new GameStateEntity(_tmpId,_tmpCurrentScore,_tmpHighScore,_tmpCurrentLevel,_tmpGridJson,_tmpAvailablePiecesJson);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
