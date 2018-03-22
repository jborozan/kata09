package de.punktjb.example.kata09_checkout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;

/**
 * 
 * @author jurica
 *
 */
public class Cart 
{	
	// items and their prices
	Map<String, Double> prices = new HashMap<>();
	
	// discount options: map of maps: (item, (discounted quantity, discounted price))
	Map<String, Map<Integer, Double>> discounts = new HashMap<>();

	// items
	List<String> items = new ArrayList<>();

	/**
	 * Returns total price of the cart
	 * @param items items as string (it will be splitted)
	 * @return
	 */
    public Observable<Double> calculateForCart( List<String> items )
    {        
    		return Observable.from(items) 			// list of items
	        .groupBy(string -> string)			// group them
	        .flatMap( group -> group.count().map(count -> new Pair(group.getKey(), count)) )	// create pairs (item, count)
	        .map( pair -> calculatePriceForItem(pair.getKey(), pair.getCount()))	// convert them to price
	        .reduce( (x, y) -> x+y );			// sum the prices
    }
    
    /**
     * Adds discount option for item and quantity
     * @param item cart item
     * @param quantity discounted quantity
     * @param price price of discounted quantity
     */
    public void addDiscountForitem(final String item, final int quantity, final double price) {
   
    		// create tree map (quantity, price) contains descending keys
    		if( ! this.discounts.containsKey(item) )
    			this.discounts.put(item, new TreeMap<Integer, Double>( (k1, k2) -> k2.compareTo(k1) ) );
    	
    		this.discounts.get(item).put(quantity, price);
    }
    
    /**
     * Adds price for single item
     * @param item item
     * @param price single item price
     */
    public void addPriceForItem(String item, double price) {
    	
    		this.prices.put(item, price);
    }
    
    /**
     * Calculates price for certain item with respect to discount on quantity
     * @param item given item
     * @param cartQuantity quantity from cart
     * @return item price share in cart
     */
    private Double calculatePriceForItem(final String item, final int cartQuantity)
    {
    		// container class which needs to be final for consumer
    		class Container {
        		int rest = cartQuantity;
        		double total = 0.0;    			
    		}    		
    		final Container container = new Container();
    		
    		// return 0 if no data in prices
    		if( ! this.prices.containsKey(item ))
    			return container.total;
    		
    		// try discounts
    		if( this.discounts.containsKey(item ))
    		{
    			this.discounts.get(item).keySet().stream().forEach(
	    			discountQuantity -> {
		    			container.total += container.rest/discountQuantity * this.discounts.get(item).get(discountQuantity);
		    			container.rest = container.rest % discountQuantity;
			});
    		}
    		
    		container.total += container.rest * this.prices.get(item);
    	
    		return container.total;
    }
    
    /**
     * Scans an item - adds it to the internal list
     * @param item
     */
    public void scan(String item)
    {
    		this.items.add(item);
    }
    
    /**
     * Uses internal list of items and calculates sum of cart
     * @return double sum
     */
    public double total()
    {
    		// container class which needs to be final for consumer
		class Container {
    			double total = 0.0;    			
		}		
		final Container container = new Container();
		
		calculateForCart( this.items ).subscribe(
			t -> container.total = t,
			(e) -> container.total = 0.0		// in case of error set 0.0
		);
		
		return container.total;
    }
    
    /**
     * Uses provided string of items and calculates sum of cart
     * @param items items in form of string
     * @return double sum
     */
    public double price(String items)
    {
    		// container class which needs to be final for consumer
		class Container {
			double total = 0.0;    			
		}
		final Container container = new Container();
		
		calculateForCart(Arrays.asList(items.split(""))).subscribe(
			t -> container.total = t,
			(e) -> container.total = 0.0		// in case of error set 0.0	
		);
		
    		return container.total;
    }
    
    /**
     * Holder class - pairs of item, cart quantity 
     * @author jurica
     *
     */
    final private class Pair 
    {    	
    		String key;
    		int count;
    		
    		public Pair(String key, int count) {
    			
    			this.key = key;
    			this.count = count;
    		}

			public String getKey() {
				return key;
			}

			public int getCount() {
				return count;
			}
    }
}
