package com.xiaomi.common.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionHelper {

    private static class CollectionCaster<S, D> extends AbstractCollection<D> {

        private final Collection<S> mSrcCollection;

        public CollectionCaster(Collection<S> src) {
            mSrcCollection = src;
        }

        @Override
        public Iterator<D> iterator() {
            return new IteratorCaster<S, D>(mSrcCollection.iterator());
        }

        @Override
        public int size() {
            return mSrcCollection.size();
        }

        static class IteratorCaster<S, D> implements Iterator<D> {
            private final Iterator<S> mIterator;

            IteratorCaster(Iterator<S> it) {
                mIterator = it;
            }

            @Override
            public boolean hasNext() {
                return mIterator.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public D next() {
                return (D)mIterator.next();
            }

            @Override
            public void remove() {
                mIterator.remove();
            }

        }

    }

    /**
     * 将源集合按目标集合遍历，减少一次copy.
     * @NOTE 用户保证类型转换的合法性
     * @param <D> 目标集合元素类型
     * @param <S> 源集合元素类型
     * @param src 源集合
     * @param elementType 目标集合元素类型
     * @return
     */
    public static <D, S> Collection<D> castTo(Collection<S> src) {
        if (src == null) {
            return null;
        }

        return new CollectionCaster<S, D>(src);
    }

    static class LongCollection extends AbstractCollection<Long> {
        private final long[] mSrc;
        private final int mLen;

        LongCollection(long[] src, int len) {
            mSrc = src;
            if (len <= 0) {
                mLen = src.length;
            } else {
                mLen = len < src.length ? len : src.length;
            }
        }

        @Override
        public Iterator<Long> iterator() {
            return new LongIterator(mSrc, mLen);
        }

        @Override
        public int size() {
            return mLen;
        }

        static class LongIterator implements Iterator<Long> {
            private final long[] mSrc;
            private final int mLen;
            private int mCounter;

            public LongIterator(long[] src, int len) {
                mSrc = src;
                mLen = len;
                mCounter = 0;
            }

            @Override
            public boolean hasNext() {
                return mCounter < mLen;
            }

            @Override
            public Long next() {
                return mSrc[mCounter++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("unsupported remove from ArrayIterator");
            }

        }
    }

    public static Collection<Long> asLongCollection(long[] src, int len) {
        return new LongCollection(src, len);
    }

    public static interface Predication<T> {
        public boolean predicate(T v);
    }

    /**
     * 将在from中且满足Pred中的元素加入to中，且to中元素不允许重复
     */
    public static <T> int differenceList(List<T> from, List<T> to, Predication<T> pred) {
        if (from == null || to == null) {
            return 0;
        }

        int oldSize = to.size();

        if (pred != null) {
            for (T v : from) {
                if (pred != null && pred.predicate(v) && !to.contains(v)) {
                    to.add(v);
                }
            }
        } else {
            for (T v : from) {
                if (!to.contains(v)) {
                    to.add(v);
                }
            }
        }

        return to.size() - oldSize;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static final char HEX_DIGITS[] = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String compressToString(long[] values, int len) {
        return compressToString(asLongCollection(values, len));
    }

    public static String compressToString(final Collection<Long> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (long n : values) {
            if (n == 0) {
                sb.append("0;");
            } else {
                while (n != 0) {
                    int digit = (int)(n & 0xf);
                    n >>= 4;
                    sb.append(HEX_DIGITS[digit]);
                }
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public static void decodeFromString(final Collection<Long> values, final String q, int maxLen) {
        values.clear();
        if (q == null) {
            return;
        }

        int qlen = q.length();
        if (qlen <= 1) {
            return;
        }
        long n = 0;
        int shift = 0;
        for (int i = 0; i < qlen; i++) {
            char c = q.charAt(i);
            if (c == ';') {
                if (maxLen > 0 && n >= maxLen) {
                    // bogus history data
                    values.clear();
                    break;
                }
                values.add(n);
                n = 0;
                shift = 0;
            } else {
                if (c >= '0' && c <= '9') {
                    n += ((c - '0') << shift);
                } else if (c >= 'a' && c <= 'f') {
                    n += ((10 + c - 'a') << shift);
                } else {
                    // bogus history data
                    values.clear();
                    break;
                }
                shift += 4;
            }
        }
    }

}
