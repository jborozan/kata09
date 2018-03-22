package de.punktjb.example.kata09_checkout;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CartTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CartTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CartTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCart_totals()
    {
		Cart cart = new Cart();
		
		// add items
		cart.addPriceForItem("A", 0.50);
		cart.addPriceForItem("B", 0.30);
		cart.addPriceForItem("C", 0.20);
		cart.addPriceForItem("D", 0.15);
		
		// add discounts
		cart.addDiscountForitem("A", 3, 1.30 );
		cart.addDiscountForitem("A", 6, 2.00 );    		
		cart.addDiscountForitem("B", 2, 0.45);
		
		assertEquals( 0.0, cart.price("") );
		assertEquals( 0.5, cart.price("A"));
		assertEquals( 0.8, cart.price("AB"));
		assertEquals(1.15, cart.price("CDBA"));
		
		assertEquals(1.00, cart.price("AA"));
		assertEquals(1.30, cart.price("AAA"));
		assertEquals(1.80, cart.price("AAAA"));
		assertEquals(2.30, cart.price("AAAAA"));
		assertEquals(2.00, cart.price("AAAAAA")); // !!!
		
		assertEquals(1.60, cart.price("AAAB"));
		assertEquals(1.75, cart.price("AAABB"));
		assertEquals(1.90, cart.price("AAABBD"));
		assertEquals(1.90, cart.price("DABABA"));
		
		return;
    }
    
    public void testCart_incemental()
    {
    		Cart cart = new Cart();
    		
		// add items
		cart.addPriceForItem("A", 0.50);
		cart.addPriceForItem("B", 0.30);
		cart.addPriceForItem("C", 0.20);
		cart.addPriceForItem("D", 0.15);
		
		// add discounts
		cart.addDiscountForitem("A", 3, 1.30 );
		cart.addDiscountForitem("A", 6, 2.00 );    // !!!		
		cart.addDiscountForitem("B", 2, 0.45);
		
		assertEquals(  0.0, cart.total() );
		cart.scan("A");  assertEquals( 0.50, cart.total());
		cart.scan("B");  assertEquals( 0.80, cart.total());
		cart.scan("A");  assertEquals( 1.30, cart.total());
		cart.scan("A");  assertEquals( 1.60, cart.total());
		cart.scan("B");  assertEquals( 1.75, cart.total());

		return;
    }
}
