package com.xiaomi.common.util;

import android.database.Cursor;

public class CursorHelper {

    public static boolean traverse(Cursor cursor, CursorVisitor visitor) {
        if (cursor == null) {
            return false;
        }
        int position = cursor.getPosition();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (!visitor.visit(cursor)) {
                break;
            }
        }

        cursor.moveToPosition(position);
        return true;
    }

    public static interface CursorVisitor {
        /**
         * Visit current record of cursor
         * @param cursor
         * @return 返回true表示继续遍历，否则遍历中断
         */
        public boolean visit(Cursor cursor);
    }

    public static interface CursorCollector<E> extends CursorVisitor {
        public boolean reset(Cursor c);
        public E[] result();
    }

    public static CursorCollector<String> newStringArrayCollector(String column) {
        return new StringObjArrayCollector(column);
    }

    public static CursorCollector<Long> newLongArrayCollector(String column) {
        return new LongObjArrayCollector(column);
    }

    public static abstract class ObjArrayCollector<T> implements CursorCollector<T> {
        private final String mColumnName;
        protected int mDataIdx = -1;
        protected T[] mContainer;

        public ObjArrayCollector(String column) {
            mColumnName = column;
        }

        @Override
        public boolean reset(Cursor cursor) {
            if (cursor == null) {
                return false;
            }
            mDataIdx = cursor.getColumnIndexOrThrow(mColumnName);
            int count = cursor.getCount();
            if (mContainer == null || mContainer.length != count) {
                mContainer = mallocArray(count);
            }

            return true;
        }

        @Override
        public T[] result() {
            return mContainer;
        }

        protected abstract T[] mallocArray(int size);
    }

    static class LongObjArrayCollector extends ObjArrayCollector<Long> {

        public LongObjArrayCollector(String column) {
            super(column);
        }

        @Override
        protected Long[] mallocArray(int size) {
            return new Long[size];
        }

        @Override
        public boolean visit(Cursor cursor) {
            mContainer[cursor.getPosition()] = cursor.getLong(mDataIdx);
            return true;
        }

    }

    static class StringObjArrayCollector extends ObjArrayCollector<String> {

        public StringObjArrayCollector(String column) {
            super(column);
        }

        @Override
        protected String[] mallocArray(int size) {
            return new String[size];
        }

        @Override
        public boolean visit(Cursor cursor) {
            mContainer[cursor.getPosition()] = cursor.getString(mDataIdx);
            return true;
        }

    }
}
