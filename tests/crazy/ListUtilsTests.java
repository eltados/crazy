
package crazy;

import static crazy.ListUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ListUtilsTests {

	private List<String> days;

	@Before
	public void setup() {
		days = Arrays.asList( "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday" );
	}

	@Test
	public void testMap() {
		Assert.assertEquals( "[MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY]", $( days ).map( "toUpperCase" ).toList().toString() );
		Assert.assertEquals( "[6, 7, 9, 8, 6, 8, 6]", $( days ).map( "length" ).toList( Integer.class ).toString() );
	}

	@Test
	public void testSelect() {
		Assert.assertEquals( "[monday, friday, sunday]", $( days ).select( x( "length" ), eq( 6 ) ).toList().toString() );
		Assert.assertEquals( "[tuesday, wednesday, thursday, saturday]", $( days ).select( x( "length" ), not( eq( 6 ) ) ).toList().toString() );
		Assert.assertEquals( "[tuesday, thursday]", $( days ).select( regex( "t.*" ) ).toList().toString() );
	}

	@Test
	public void testReject() {
		Assert.assertEquals( "[monday, wednesday, friday, saturday]", $( days ).reject( eq( "sunday" ) ).reject( regex( "t.*" ) ).toList().toString() );
	}

	@Test
	public void testFind() {
		Assert.assertEquals( "monday", $( days ).find( x( "length" ), eq( 6 ) ) );
		Assert.assertEquals( null, $( days ).find( x( "length" ), eq( 12 ) ) );
	}

	@Test
	public void testHash() {
		Assert.assertEquals( "{wednesday=9, thursday=8, monday=6, sunday=6, saturday=8, friday=6, tuesday=7}", $( days ).toHash( nop(), x( "length" ) ).toString() );
		Assert.assertEquals( "{1393530710=wednesday, 1572055514=thursday, -1068502768=monday, -891186736=sunday, -2114201671=saturday, -1266285217=friday, -977343923=tuesday}", $( days ).toHash( x( "hashCode" ) ).toString() );
	}

	@Test
	public void testChain() {
		Assert.assertEquals( "[FRIDAY, MONDAY, SUNDAY]", $( days ).select( x( "length" ), eq( 6 ) ).map( "toUpperCase" ).sort().toList().toString() );
	}

}