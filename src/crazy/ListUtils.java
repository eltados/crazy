
package crazy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;


public class ListUtils<T> {

	private final List<T> list;

	public static <T> ListUtils<T> $( List<T> list ) {
		return new ListUtils<T>( list );

	}

	public static <T> ListUtils<T> $( T ... list ) {
		return new ListUtils<T>( list );
	}

	public ListUtils( List<T> list ) {
		this.list = list;
	}

	public ListUtils( T ... list ) {
		this.list = Arrays.asList( list );
	}


	public List<T> toList() {
		return list;
	}

	public List<T> done() {
		return toList();
	}

	public <O extends T> List<O> done( Class<O> clazz ) {
		return toList( clazz );
	}

	/**
	 * @param clazz
	 */
	@SuppressWarnings( "unchecked" )
	public <O extends T> List<O> toList( Class<O> clazz ) {
		return (List<O>) list;
	}

	public ListUtils<T> sort( Transformer transformer ) throws IllegalArgumentException {
		List<Object> tupleList = $( list ).map( new TupleTransformer( transformer ) ).toList();
		Collections.sort( tupleList, new TupleComparator() );
		return $( tupleList ).map( new TupleToSimpleTransformer() );

	}

	public ListUtils<T> sort( String methodName ) throws IllegalArgumentException {
		return sort( x( methodName ) );
	}

	public ListUtils<T> reverse() throws IllegalArgumentException {
		ListUtils<T> listUtils = new ListUtils<T>( list );
		Collections.reverse( listUtils.toList() );
		return listUtils;
	}


	public ListUtils<T> sort() throws IllegalArgumentException {
		return sort( TransformerUtils.nopTransformer() );
	}

	@SuppressWarnings( "unchecked" )
	public <O> ListUtils<O> map( Transformer transformer ) throws IllegalArgumentException {
		return new ListUtils<O>( (List<O>) CollectionUtils.collect( list, transformer ) );
	}


	public <O> ListUtils<O> map( String methodName ) throws IllegalArgumentException {
		return map( x( methodName ) );
	}



	public HashMap<Object, Object> toHash( Transformer key, Transformer value ) throws IllegalArgumentException {
		HashMap<Object, Object> hash = new HashMap<Object, Object>();
		List<Object> keys = map( key ).done();
		List<Object> values = map( value ).done();
		for ( int i = 0 ; i < keys.size() ; i++ ) {
			hash.put( keys.get( i ), values.get( i ) );
		}

		return hash;
	}

	public HashMap<Object, Object> toHash( String keyMethodName, String valueMethodName ) throws IllegalArgumentException {
		return toHash( x( keyMethodName ), x( valueMethodName ) );
	}

	@SuppressWarnings( "unchecked" )
	public HashMap<Object, T> toHash( Transformer key ) throws IllegalArgumentException {
		return (HashMap<Object, T>) toHash( key, TransformerUtils.nopTransformer() );
	}

	public HashMap<Object, T> toHash( String keyMethodName ) throws IllegalArgumentException {
		return toHash( x( keyMethodName ) );
	}

	@SuppressWarnings( "unchecked" )
	public ListUtils<T> select( Predicate predicate ) throws IllegalArgumentException {
		return new ListUtils<T>( (List<T>) CollectionUtils.select( list, predicate ) );
	}

	public ListUtils<T> select( Transformer transformer, Predicate predicate ) throws IllegalArgumentException {
		return select( PredicateUtils.transformedPredicate( transformer, predicate ) );
	}

	public ListUtils<T> select( String methodName, Predicate predicate ) throws IllegalArgumentException {
		return select( PredicateUtils.transformedPredicate( x( methodName ), predicate ) );
	}


	public ListUtils<T> reject( Predicate predicate ) throws IllegalArgumentException {
		return select( PredicateUtils.notPredicate( predicate ) );
	}

	public ListUtils<T> reject( Transformer transformer, Predicate predicate ) throws IllegalArgumentException {
		return reject( PredicateUtils.transformedPredicate( transformer, predicate ) );
	}

	public ListUtils<T> reject( String methodName, Predicate predicate ) throws IllegalArgumentException {
		return reject( PredicateUtils.transformedPredicate( x( methodName ), predicate ) );
	}

	public ListUtils<T> uniq() {
		return select( PredicateUtils.uniquePredicate() );
	}

	public ListUtils<T> uniq( Transformer transformer ) {
		return select( transformer, PredicateUtils.uniquePredicate() );
	}

	public ListUtils<T> uniq( String methodName ) {
		return select( methodName, PredicateUtils.uniquePredicate() );
	}

	public ListUtils<T> removeNulls() {
		return reject( isNull() );
	}

	public ListUtils<T> removeNulls( Transformer transformer ) {
		return reject( transformer, isNull() );
	}

	public ListUtils<T> removeNulls( String methodName ) {
		return reject( methodName, isNull() );
	}

	@SuppressWarnings( "unchecked" )
	public T find( Predicate predicate ) throws IllegalArgumentException {
		return (T) CollectionUtils.find( list, predicate );
	}

	public T find( Transformer transformer, Predicate predicate ) throws IllegalArgumentException {
		return find( PredicateUtils.transformedPredicate( transformer, predicate ) );
	}

	public T find( String methodName, Predicate predicate ) throws IllegalArgumentException {
		return find( PredicateUtils.transformedPredicate( x( methodName ), predicate ) );
	}

	public static Predicate eq( Object object ) {
		return PredicateUtils.equalPredicate( object );
	}


	public static Predicate regex( final String regex ) {
		return new Predicate() {

			@Override
			public boolean evaluate( Object arg0 ) {
				return arg0.toString().matches( regex );
			}
		};

	}

	public static Predicate notEq( Object object ) {
		return not( eq( object ) );
	}

	public static Predicate not( Predicate predicate ) {
		return PredicateUtils.notPredicate( predicate );
	}

	public static Predicate notNull() {
		return PredicateUtils.notNullPredicate();
	}

	public static Predicate isNull() {
		return PredicateUtils.nullPredicate();
	}

	public static Transformer nop() {
		return TransformerUtils.nopTransformer();
	}

	public static Transformer x( String methodString ) {
		return TransformerUtils.invokerTransformer( methodString );
	}

	public static Transformer x( String ... methodStrings ) {
		Transformer[] transformers = new Transformer[methodStrings.length];
		int i = 0;
		for ( String methodString : methodStrings ) {
			transformers[i] = x( methodString );
			i++;
    }

		return TransformerUtils.chainedTransformer( transformers );
	}


	private class TupleTransformer implements Transformer {

		private final Transformer transformer;

		public TupleTransformer( Transformer transformer ) {
			this.transformer = transformer;
		}

		@Override
		public Object transform( Object object ) {
			return new Object[]{ transformer.transform( object ), object };
		}

	}

	private class TupleToSimpleTransformer implements Transformer {
		@Override
		public Object transform( Object object ) {
			Object[] tuple = ((Object[]) object);
			return tuple[1];
		}

	}

	private class TupleComparator implements Comparator<Object> {

		@SuppressWarnings( "unchecked" )
		@Override
		public int compare( Object o1, Object o2 ) {
			Object[] tuple1 = ((Object[]) o1);
			Object[] tuple2 = ((Object[]) o2);
			return ((Comparable<Object>) tuple1[0]).compareTo( tuple2[0] );
		}


	}

}