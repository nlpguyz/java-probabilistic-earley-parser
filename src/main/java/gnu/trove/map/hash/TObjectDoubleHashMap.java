///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.map.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.TDoubleCollection;


import java.io.*;
import java.util.*;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. HAND EDIT! //
//////////////////////////////////////////////////


/**
 * An open addressed Map implementation for Object keys and double values.
 *
 * Created: Sun Nov  4 08:52:45 2001
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */
@SuppressWarnings("unused")
public class TObjectDoubleHashMap<K> extends TObjectHash<K>
    implements TObjectDoubleMap<K>, Externalizable {

    static final long serialVersionUID = 1L;

    private final TObjectDoubleProcedure<K> PUT_ALL_PROC = (key, value) -> {
        put(key, value);
        return true;
    };

    /** the values of the map */
    private transient double[] _values;

    /** the value that represents null */
    private double no_entry_value;


    /**
     * Creates a new <code>TObjectDoubleHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TObjectDoubleHashMap() {
        super();
        no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }


    /**
     * Creates a new <code>TObjectDoubleHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TObjectDoubleHashMap(final int initialCapacity ) {
        super( initialCapacity );
        no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }


    /**
     * Creates a new <code>TObjectDoubleHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TObjectDoubleHashMap(final int initialCapacity, final float loadFactor ) {
        super( initialCapacity, loadFactor );
        no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }


    /**
     * Creates a new <code>TObjectDoubleHashMap</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor used to calculate the threshold over which
     * rehashing takes place.
     * @param noEntryValue the value used to represent null.
     */
    public TObjectDoubleHashMap(final int initialCapacity, final float loadFactor, final double noEntryValue ) {
        super( initialCapacity, loadFactor );
        no_entry_value = noEntryValue;
        //noinspection RedundantCast
        if ( no_entry_value != ( double ) 0 ) {
            Arrays.fill( _values, no_entry_value );
        }
    }


    /**
     * Creates a new <code>TObjectDoubleHashMap</code> that contains the entries
     * in the map passed to it.
     *
     * @param map the <tt>TObjectDoubleMap</tt> to be copied.
     */
    public TObjectDoubleHashMap(final TObjectDoubleMap<? extends K> map ) {
        this( map.size(), 0.5f, map.getNoEntryValue() );
        if ( map instanceof TObjectDoubleHashMap ) {
            final TObjectDoubleHashMap hashmap = ( TObjectDoubleHashMap ) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_value = hashmap.no_entry_value;
            //noinspection RedundantCast
            if ( this.no_entry_value != ( double ) 0 ) {
                Arrays.fill( _values, this.no_entry_value );
            }
            setUp( (int) Math.ceil( DEFAULT_CAPACITY / _loadFactor ) );
        }
        putAll( map );
    }


    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    public int setUp(final int initialCapacity ) {
        final int capacity;

        capacity = super.setUp( initialCapacity );
        _values = new double[capacity];
        return capacity;
    }


    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    protected void rehash(final int newCapacity ) {
        final int oldCapacity = _set.length;

        //noinspection unchecked
        final K[] oldKeys = ( K[] ) _set;
        final double[] oldVals = _values;

        _set = new Object[newCapacity];
        Arrays.fill( _set, FREE );
        _values = new double[newCapacity];
        Arrays.fill( _values, no_entry_value );

        for ( int i = oldCapacity; i-- > 0; ) {
          if( oldKeys[i] != FREE && oldKeys[i] != REMOVED ) {
                final K o = oldKeys[i];
                final int index = insertKey(o);
                if ( index < 0 ) {
                    throwObjectContractViolation( _set[ (-index -1) ], o);
                }
                _set[index] = o;
                _values[index] = oldVals[i];
            }
        }
    }


    // Query Operations

    /** {@inheritDoc} */
    public double getNoEntryValue() {
        return no_entry_value;
    }


    /** {@inheritDoc} */
    public boolean containsKey(final Object key ) {
        return contains( key );
    }


    /** {@inheritDoc} */
    public boolean containsValue(final double val ) {
        final Object[] keys = _set;
        final double[] vals = _values;

        for ( int i = vals.length; i-- > 0; ) {
            if ( keys[i] != FREE && keys[i] != REMOVED && val == vals[i] ) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    public double get(final Object key ) {
        final int index = index( key );
        return index < 0 ? no_entry_value : _values[index];
    }


    // Modification Operations

    /** {@inheritDoc} */
    public double put(final K key, final double value ) {
        final int index = insertKey( key );
        return doPut( value, index );
    }


    /** {@inheritDoc} */
    public double putIfAbsent(final K key, final double value ) {
        final int index = insertKey(key);
        if ( index < 0 )
            return _values[-index - 1];
        return doPut( value, index );
    }


    private double doPut(final double value, int index ) {
        double previous = no_entry_value;
        boolean isNewMapping = true;
        if ( index < 0 ) {
            index = -index -1;
            previous = _values[index];
            isNewMapping = false;
        }
        //noinspection unchecked
        _values[index] = value;

        if ( isNewMapping ) {
            postInsertHook( consumeFreeSlot );
        }
        return previous;
    }


    /** {@inheritDoc} */
    public double remove(final Object key ) {
        double prev = no_entry_value;
        final int index = index(key);
        if ( index >= 0 ) {
            prev = _values[index];
            removeAt( index );    // clear key,state; adjust size
        }
        return prev;
    }


    /**
     * Removes the mapping at <tt>index</tt> from the map.
     * This method is used internally and public mainly because
     * of packaging reasons.  Caveat Programmer.
     *
     * @param index an <code>int</code> value
     */
    protected void removeAt(final int index ) {
        _values[index] = no_entry_value;
        super.removeAt( index );  // clear key, state; adjust size
    }


    // Bulk Operations

    /** {@inheritDoc} */
    public void putAll(final Map<? extends K, ? extends Double> map ) {
        final Set<? extends Map.Entry<? extends K,? extends Double>> set = map.entrySet();
        for ( final Map.Entry<? extends K,? extends Double> entry : set ) {
            put( entry.getKey(), entry.getValue() );
        }
    }
    

    /** {@inheritDoc} */
    public void putAll(final TObjectDoubleMap<? extends K> map ){
        map.forEachEntry( PUT_ALL_PROC );
    }


    /** {@inheritDoc} */
    public void clear() {
        super.clear();
        Arrays.fill( _set, 0, _set.length, FREE );
        Arrays.fill( _values, 0, _values.length, no_entry_value );
    }


    // Views

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return new KeyView();
    }


    /** {@inheritDoc} */
    public Object[] keys() {
        //noinspection unchecked
        final K[] keys = ( K[] ) new Object[size()];
        final Object[] k = _set;

        for ( int i = k.length, j = 0; i-- > 0; ) {
            if ( k[i] != FREE && k[i] != REMOVED ) {
                //noinspection unchecked
                keys[j++] = ( K ) k[i];
            }
        }
        return keys;
    }


    /** {@inheritDoc} */
    public K[] keys( K[] a ) {
        final int size = size();
        if ( a.length < size ) {
            //noinspection unchecked
            a = ( K[] ) java.lang.reflect.Array.newInstance(
                          a.getClass().getComponentType(), size );
        }

        final Object[] k = _set;

        for ( int i = k.length, j = 0; i-- > 0; ) {
            if ( k[i] != FREE && k[i] != REMOVED ) {
                //noinspection unchecked
                a[j++] = ( K ) k[i];
            }
        }
        return a;
    }


    /** {@inheritDoc} */
    public TDoubleCollection valueCollection() {
        return new TDoubleValueCollection();
    }


    /** {@inheritDoc} */
    public double[] values() {
        final double[] vals = new double[size()];
        final double[] v = _values;
        final Object[] keys = _set;

        for ( int i = v.length, j = 0; i-- > 0; ) {
            if ( keys[i] != FREE && keys[i] != REMOVED ) {
                vals[j++] = v[i];
            }
        }
        return vals;
    }


    /** {@inheritDoc} */
    public double[] values( double[] array ) {
        final int size = size();
        if ( array.length < size ) {
            array = new double[size];
        }

        final double[] v = _values;
        final Object[] keys = _set;

        for ( int i = v.length, j = 0; i-- > 0; ) {
            if ( keys[i] != FREE && keys[i] != REMOVED ) {
                array[j++] = v[i];
            }
        }
        if ( array.length > size ) {
            array[size] = no_entry_value;
        }
        return array;
    }


    /**
     * @return an iterator over the entries in this map
     */
    public TObjectDoubleIterator<K> iterator() {
        return new TObjectDoubleHashIterator<>(this);
    }


    /** {@inheritDoc} */
    @SuppressWarnings({"RedundantCast"})
    public boolean increment(final K key ) {
        //noinspection RedundantCast
        return adjustValue( key, (double)1 );
    }


    /** {@inheritDoc} */
    public boolean adjustValue(final K key, final double amount ) {
        final int index = index(key);
        if ( index < 0 ) {
            return false;
        } else {
            _values[index] += amount;
            return true;
        }
    }


    /** {@inheritDoc} */
    public double adjustOrPutValue( final K key, final double adjust_amount,
		final double put_amount ) {

        int index = insertKey( key );
        final boolean isNewMapping;
        final double newValue;
        if ( index < 0 ) {
            index = -index -1;
            newValue = ( _values[index] += adjust_amount );
            isNewMapping = false;
        } else {
            newValue = ( _values[index] = put_amount );
            isNewMapping = true;
        }

        //noinspection unchecked

        if ( isNewMapping ) {
            postInsertHook( consumeFreeSlot );
        }

        return newValue;
    }


    /**
     * Executes <tt>procedure</tt> for each key in the map.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the keys terminated because
     * the procedure returned false for some key.
     */
    public boolean forEachKey(final TObjectProcedure<? super K> procedure ) {
        return forEach( procedure );
    }


    /**
     * Executes <tt>procedure</tt> for each value in the map.
     *
     * @param procedure a <code>TDoubleProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(final TDoubleProcedure procedure ) {
        final Object[] keys = _set;
        final double[] values = _values;
        for ( int i = values.length; i-- > 0; ) {
            if ( keys[i] != FREE && keys[i] != REMOVED
                && procedure.execute( values[i] )) {
                return false;
            }
        }
        return true;
    }


    /**
     * Executes <tt>procedure</tt> for each key/value entry in the
     * map.
     *
     * @param procedure a <code>TOObjectDoubleProcedure</code> value
     * @return false if the loop over the entries terminated because
     * the procedure returned false for some entry.
     */
    @SuppressWarnings({"unchecked"})
    public boolean forEachEntry(final TObjectDoubleProcedure<? super K> procedure ) {
        final Object[] keys = _set;
        final double[] values = _values;
        for ( int i = keys.length; i-- > 0; ) {
            if ( keys[i] != FREE
                && keys[i] != REMOVED
                && ! procedure.execute( ( K ) keys[i], values[i] ) ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Retains only those entries in the map for which the procedure
     * returns a true value.
     *
     * @param procedure determines which entries to keep
     * @return true if the map was modified.
     */
    public boolean retainEntries(final TObjectDoubleProcedure<? super K> procedure ) {
        boolean modified = false;
        //noinspection unchecked
        final K[] keys = ( K[] ) _set;
        final double[] values = _values;

        // Temporarily disable compaction. This is a fix for bug #1738760
        tempDisableAutoCompaction();
        try {
            for ( int i = keys.length; i-- > 0; ) {
                if ( keys[i] != FREE
                    && keys[i] != REMOVED
                    && ! procedure.execute( keys[i], values[i] ) ) {
                    removeAt(i);
                    modified = true;
                }
            }
        }
        finally {
            reenableAutoCompaction( true );
        }

        return modified;
    }


    /**
     * Transform the values in this map using <tt>function</tt>.
     *
     * @param function a <code>TDoubleFunction</code> value
     */
    public void transformValues(final TDoubleFunction function ) {
        final Object[] keys = _set;
        final double[] values = _values;
        for ( int i = values.length; i-- > 0; ) {
            if ( keys[i] != null && keys[i] != REMOVED ) {
                values[i] = function.execute( values[i] );
            }
        }
    }


    // Comparison and hashing

    /**
     * Compares this map with another map for equality of their stored
     * entries.
     *
     * @param other an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(final Object other ) {
        if ( ! ( other instanceof TObjectDoubleMap ) ) {
            return false;
        }
        final TObjectDoubleMap that = ( TObjectDoubleMap ) other;
        if ( that.size() != this.size() ) {
            return false;
        }
        try {
            final TObjectDoubleIterator iter = this.iterator();
            while ( iter.hasNext() ) {
                iter.advance();
                final Object key = iter.key();
                final double value = iter.value();
                if ( value == no_entry_value ) {
                    if ( !( that.get( key ) == that.getNoEntryValue() &&
	                    that.containsKey( key ) ) ) {

                        return false;
                    }
                } else {
                    if ( value != that.get( key ) ) {
                        return false;
                    }
                }
            }
        } catch ( final ClassCastException ex ) {
            // unused.
        }
        return true;
    }


    /** {@inheritDoc} */
    public int hashCode() {
        int hashcode = 0;
        final Object[] keys = _set;
        final double[] values = _values;
        for ( int i = values.length; i-- > 0; ) {
            if ( keys[i] != FREE && keys[i] != REMOVED ) {
                hashcode += HashFunctions.hash( values[i] ) ^
                            ( keys[i] == null ? 0 : keys[i].hashCode() );
            }
        }
        return hashcode;
    }


    /** a view onto the keys of the map. */
    protected class KeyView extends MapBackedView<K> {

        @SuppressWarnings({"unchecked"})
        public Iterator<K> iterator() {
            return new TObjectHashIterator( TObjectDoubleHashMap.this );
        }

        public boolean removeElement(final K key ) {
            return no_entry_value != TObjectDoubleHashMap.this.remove( key );
        }

        public boolean containsElement(final K key ) {
            return TObjectDoubleHashMap.this.contains( key );
        }
    }


    private abstract class MapBackedView<E> extends AbstractSet<E>
            implements Set<E>, Iterable<E> {

        protected abstract boolean removeElement(E key);

        protected abstract boolean containsElement(E key);

        @SuppressWarnings({"unchecked"})
        public boolean contains(final Object key ) {
            return containsElement( (E) key );
        }

        @SuppressWarnings({"unchecked"})
        public boolean remove(final Object o ) {
            return removeElement( (E) o );
        }

        public void clear() {
            TObjectDoubleHashMap.this.clear();
        }

        public boolean add(final E obj ) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectDoubleHashMap.this.size();
        }

        public Object[] toArray() {
            final Object[] result = new Object[size()];
            final Iterator<E> e = iterator();
            for ( int i = 0; e.hasNext(); i++ ) {
                result[i] = e.next();
            }
            return result;
        }

        @SuppressWarnings("Duplicates")
        public <T> T[] toArray(T[] a ) {
            final int size = size();
            if ( a.length < size ) {
                //noinspection unchecked
                a = (T[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(), size );
            }

            final Iterator<E> it = iterator();
            final Object[] result = a;
            for ( int i = 0; i < size; i++ ) {
                result[i] = it.next();
            }

            if ( a.length > size ) {
                a[size] = null;
            }

            return a;
        }

        public boolean isEmpty() {
            return TObjectDoubleHashMap.this.isEmpty();
        }

        public boolean addAll(final Collection<? extends E> collection ) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings({"SuspiciousMethodCalls"})
        public boolean retainAll(final Collection<?> collection ) {
            boolean changed = false;
            final Iterator<E> i = iterator();
            while ( i.hasNext() ) {
                if ( !collection.contains( i.next() ) ) {
                    i.remove();
                    changed = true;
                }
            }
            return changed;
        }
    }


    class TDoubleValueCollection implements TDoubleCollection {


        public TDoubleIterator iterator() {
            return new TObjectDoubleValueHashIterator();
        }


        public double getNoEntryValue() {
            return no_entry_value;
        }


        public int size() {
            return _size;
        }


        public boolean isEmpty() {
            return 0 == _size;
        }


        public boolean contains(final double entry ) {
            return !TObjectDoubleHashMap.this.containsValue( entry );
        }


        public double[] toArray() {
            return TObjectDoubleHashMap.this.values();
        }


        public double[] toArray(final double[] dest ) {
            return TObjectDoubleHashMap.this.values( dest );
        }

        public boolean add(final double entry ) {
            throw new UnsupportedOperationException();
        }


        public boolean remove(final double entry ) {
            final double[] values = _values;
            final Object[] set = _set;

            for ( int i = values.length; i-- > 0; ) {
                if ( ( set[i] != FREE && set[i] != REMOVED ) && entry == values[i] ) {
                    removeAt( i );
                    return true;
                }
            }
            return false;
        }


        public boolean containsAll(final Collection<?> collection ) {
            for ( final Object element : collection ) {
                if ( element instanceof Double ) {
                    final double ele = ( ( Double ) element ).doubleValue();
                    if ( ! TObjectDoubleHashMap.this.containsValue( ele ) ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }


        public boolean containsAll(final TDoubleCollection collection ) {
            final TDoubleIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TObjectDoubleHashMap.this.containsValue( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        public boolean containsAll(final double[] array ) {
            for ( final double element : array ) {
                if ( ! TObjectDoubleHashMap.this.containsValue( element ) ) {
                    return false;
                }
            }
            return true;
        }


        public boolean addAll(final Collection<? extends Double> collection ) {
            throw new UnsupportedOperationException();
        }


        public boolean addAll(final TDoubleCollection collection ) {
            throw new UnsupportedOperationException();
        }


        public boolean addAll(final double[] array ) {
            throw new UnsupportedOperationException();
        }


        @SuppressWarnings({"SuspiciousMethodCalls"})
        public boolean retainAll(final Collection<?> collection ) {
            boolean modified = false;
            final TDoubleIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Double.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        @SuppressWarnings("Duplicates")
        public boolean retainAll(final TDoubleCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            final TDoubleIterator iter = iterator();
            while ( iter.hasNext() ) {
                if (collection.contains( iter.next() )) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        public boolean retainAll(final double[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            final double[] values = _values;

            final Object[] set = _set;
            for ( int i = set.length; i-- > 0; ) {
                if ( set[i] != FREE
                     && set[i] != REMOVED
                     && ( Arrays.binarySearch( array, values[i] ) < 0) ) {
                    removeAt( i );
                    changed = true;
                }
            }
            return changed;
        }


        @SuppressWarnings("Duplicates")
        public boolean removeAll(final Collection<?> collection ) {
            boolean changed = false;
            for ( final Object element : collection ) {
                if ( element instanceof Double ) {
                    final double c = (Double) element;
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        @SuppressWarnings("Duplicates")
        public boolean removeAll(final TDoubleCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            final TDoubleIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                final double element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        public boolean removeAll(final double[] array ) {
            boolean changed = false;
            for ( int i = array.length; i-- > 0; ) {
                if ( remove( array[i] ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        public void clear() {
            TObjectDoubleHashMap.this.clear();
        }


        public boolean forEach(final TDoubleProcedure procedure ) {
            return TObjectDoubleHashMap.this.forEachValue( procedure );
        }


        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TDoubleProcedure() {
                private boolean first = true;

                public boolean execute(final double value ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( value );
                    return false;
                }
            } );
            buf.append( "}" );
            return buf.toString();
        }


        class TObjectDoubleValueHashIterator implements TDoubleIterator {

            final THash _hash = TObjectDoubleHashMap.this;

            /**
             * the number of elements this iterator believes are in the
             * data structure it accesses.
             */
            int _expectedSize;

            /** the index used for iteration. */
            int _index;

            /** Creates an iterator over the specified map */
            TObjectDoubleValueHashIterator() {
                _expectedSize = _hash.size();
                _index = _hash.capacity();
            }


            public boolean hasNext() {
                return nextIndex() >= 0;
            }


            public double next() {
                moveToNextIndex();
                return _values[_index];
            }

            /** @{inheritDoc} */
            public void remove() {
                if ( _expectedSize != _hash.size() ) {
                    throw new ConcurrentModificationException();
                }

                // Disable auto compaction during the remove. This is a workaround for
                // bug 1642768.
                try {
                    _hash.tempDisableAutoCompaction();
                    TObjectDoubleHashMap.this.removeAt( _index );
                }
                finally {
                    _hash.reenableAutoCompaction( false );
                }

                _expectedSize--;
            }

            /**
             * Sets the internal <tt>index</tt> so that the `next' object
             * can be returned.
             */
            final void moveToNextIndex() {
                // doing the assignment && < 0 in one line shaves
                // 3 opcodes...
                if ( ( _index = nextIndex() ) < 0 ) {
                    throw new NoSuchElementException();
                }
            }

            /**
             * Returns the index of the next value in the data structure
             * or a negative value if the iterator is exhausted.
             *
             * @return an <code>int</code> value
             * @throws ConcurrentModificationException
             *          if the underlying
             *          collection's size has been modified since the iterator was
             *          created.
             */
            final int nextIndex() {
                if ( _expectedSize != _hash.size() ) {
                    throw new ConcurrentModificationException();
                }

                final Object[] set = TObjectDoubleHashMap.this._set;
                int i = _index;
                while ( i-- > 0 && ( set[i] == TObjectHash.FREE ||
	                set[i] == TObjectHash.REMOVED ) ) {

					// do nothing
                }
                return i;
            }
        }
    }


    class TObjectDoubleHashIterator<K> extends TObjectHashIterator<K>
        implements TObjectDoubleIterator<K> {

        /** the collection being iterated over */
        private final TObjectDoubleHashMap<K> _map;

        TObjectDoubleHashIterator(final TObjectDoubleHashMap<K> map) {
            super( map );
            this._map = map;
        }


        public void advance() {
            moveToNextIndex();
        }


        @SuppressWarnings({"unchecked"})
        public K key() {
            return ( K ) _map._set[_index];
        }


        public double value() {
            return _map._values[_index];
        }


        public double setValue(final double val ) {
            final double old = value();
            _map._values[_index] = val;
            return old;
        }
    }


    // Externalization

    public void writeExternal(final ObjectOutput out ) throws IOException {
        // VERSION
        out.writeByte( 0 );

        // SUPER
        super.writeExternal( out );

        // NO_ENTRY_VALUE
        out.writeDouble( no_entry_value );

        // NUMBER OF ENTRIES
        out.writeInt( _size );

        // ENTRIES
        for ( int i = _set.length; i-- > 0; ) {
            if ( _set[i] != REMOVED && _set[i] != FREE ) {
                out.writeObject( _set[i] );
                out.writeDouble( _values[i] );
            }
        }
    }


    public void readExternal(final ObjectInput in )
        throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // SUPER
        super.readExternal( in );

        // NO_ENTRY_VALUE
        no_entry_value = in.readDouble();

        // NUMBER OF ENTRIES
        int size = in.readInt();
        setUp( size );

        // ENTRIES
        while (size-- > 0) {
            //noinspection unchecked
            final K key = ( K ) in.readObject();
            final double val = in.readDouble();
            put(key, val);
        }
    }


    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        forEachEntry( new TObjectDoubleProcedure<K>() {
            private boolean first = true;
            public boolean execute(final K key, final double value ) {
                if ( first ) first = false;
                else buf.append( "," );

                buf.append( key ).append( "=" ).append( value );
                return true;
            }
        });
        buf.append( "}" );
        return buf.toString();
    }
} // TObjectDoubleHashMap
