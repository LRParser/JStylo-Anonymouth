/**
 * 
 */
package edu.drexel.psal.anonymouth.utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Andrew W.E. McDonald
 *
 */
public class SpecialCharacterTracker implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5900337779583604917L;
	// Basically parallel arrays... we use the replacement characters instead of the corresponding eos characters. 
	// Doing this allows us to break sentences only where we are sure we want to break them, and will allow the user more flexibility.
	// as a side note, while realEOS[2] and replacementEOS[2] look very similar, they are not the same character.. this can be tested (which I did at the bottom of 'main', below) by asking Java if they are equal to eachother.
	public static char[] realEOS = {'.', '?', '!'};
	public static char[] replacementEOS = {'๏', 'ʔ', '˩'};
	private ArrayList<EOS> eoses;

	/**
	 * Constructor
	 */
	public SpecialCharacterTracker() {
		eoses = new ArrayList<EOS>(100); //note at this point, it's unlikely that we'll have more than 100 sentences.. but this should eventually be changed to some global parameter than is relative to the length of the document or something.
	}
	
	/**
	 * Constructor for SpecialCharacterTracker. Essentially does a deep copy of the input SpecialCharacterTracker.
	 * @param eosCT
	 */
	public SpecialCharacterTracker( SpecialCharacterTracker sct) {
		int i;
		int numEOSes = sct.eoses.size();
		eoses = new ArrayList<EOS>(numEOSes);
		for( i = 0; i < numEOSes; i++)
			eoses.add(new EOS(sct.eoses.get(i)));
	}
	
	/**
	 * Adds the EOS eos to the EOS ArrayList
	 * @param eos
	 */
	public void addEOS(char eosChar, int location, boolean ignore) {
		eoses.add(new EOS(eosChar, location, ignore));
	}
	
	public void addParens(int openParen, int closeParen) {
		// todo this.
	}
	
	public void addQuotes(int openQuote, int closeQuote) {
		// todo this too.
		
	}
	
	public void setIgnore(int location, boolean b) {
		int length = eoses.size();
		
		for (int i = 0; i < length; i++) {
			if (location-1 == eoses.get(i).location) {
				eoses.get(i).setIgnore(b);
			}
		}
	}
	
	public boolean EOSAtIndex(int index) {
		boolean result = false;
		int length = eoses.size();

//		System.out.println("-----------------");
		if (length == 0) {
			result = true;
		} else {
			for (int i = 0; i < length; i++) {
//				System.out.println(index-1);
//				System.out.println("   " + eoses.get(i).location);
//				System.out.println("   " + eoses.get(i).ignore);
				if (index-1 == eoses.get(i).location) {
					if (!eoses.get(i).ignore) {
						result = true;
					}
					break;
				}
				
//				if (i == length - 1) {
//					result = true;
//				}
			}
		}
//		System.out.println("result = " + result);
		return result;
	}
	
	/**
	 * Removes the EOS objects located between [lowerBound, upperBound) ==> [inclusive, exclusive)
	 * @param lowerBound
	 * @param upperBound
	 */
	public boolean removeEOSesInRange(int lowerBound, int upperBound) {
		int i;
		int numEOSes = eoses.size();
		int thisEOSLoc;
		boolean haveRemoved = false;
//		System.out.println(lowerBound + " " + upperBound);
		for (i=0; i < numEOSes; i++) {
//			System.out.println("HELLO: " + eoses.get(i));
			thisEOSLoc = eoses.get(i).location;
			//System.out.printf("thisEOSLoc: %d, lowerBound: %d, upperBound: %d\n", thisEOSLoc, lowerBound, upperBound);
			if (thisEOSLoc >= lowerBound && thisEOSLoc < upperBound) {
				eoses.remove(i);
				i--; // decrement 'i' so that we don't miss the object that shifts down into the spot just freed.
				numEOSes--; // also decrement numEOSes so that 
				haveRemoved = true;
			}
		}
		//shiftAllEOSChars(false, upperBound, (upperBound - lowerBound));
//		for (i = 0; i < eoses.size(); i++) {
//			System.out.println("HELLO2: " + eoses.get(i));
//		}
		return haveRemoved;
		
	}
	
	/**
	 * Shifts the locations of all stored EOS objects by shiftAmount. If shiftRight is true, then it adds shiftAmount to each location.
	 * If shiftRight is false, then it subtracts shiftAmount from each location. However, any locations that are less than startIndex, won't be touched.
	 * The reason is, when you begin typing, everything behind the caret will stay where it is; but everything in front of the caret will be pushed.
	 * The same thing happens when you delete characters; anything behind your caret either stays put or is deleted, and anything in front of it is dragged backward. 
	 * @param shiftRight true to add to each EOS location, false to subtract from each EOS location
	 * @param startIndex ignore any locations that are less than this. 
	 * @param shiftAmount number to add to each location (locations past startIndex)
	 */
	public void shiftAllEOSChars(boolean shiftRight, int startIndex, int shiftAmount) {
		// note: right now, we'll just loop through the whole ArrayList of EOS objects, and check each one to see if its location is >= startIndex. 
		//There is almost certainly a more efficient way to do this, but as it's a small list, and I just want to get something working, I'm going to leave it like this for now.
		if (shiftRight)
			System.out.println("shifting all EOS characters right starting from "+startIndex+" by "+shiftAmount+" places...");
		else
			System.out.println("shifting all EOS characters left starting from "+startIndex+" by "+shiftAmount+" places...");
		int i;
		int numEOSes = eoses.size();
		if (shiftRight) { // add shiftAmount
			for (i = 0; i < numEOSes; i++) {
				EOS thisEOS = eoses.get(i);
				if (thisEOS.location >= startIndex)
					eoses.get(i).location += shiftAmount;
			}
		} else { // subtract shiftAmount
			for (i = 0; i < numEOSes; i++) {
				EOS thisEOS = eoses.get(i);
				if (thisEOS.location >= startIndex)
					eoses.get(i).location -= shiftAmount;
			}
		}
	}
	
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// NOTE Auto-generated method stub
//		SpecialCharacterTracker ect = new SpecialCharacterTracker();
//		ect.addEOS('.',5);
//		ect.addEOS('!',7);
//		ect.addEOS('?',9);
//		ect.addEOS('!',6);
//		ect.addEOS('.',12);
//		System.out.println(ect.toString());
//		ect.shiftAllEOSChars(true, 4, 5);
//		System.out.println(ect.toString());
//		ect.removeEOSesInRange(11, 15);
//		System.out.println(ect.toString());
//		System.out.println(SpecialCharacterTracker.realEOS[0] == SpecialCharacterTracker.replacementEOS[0]);
//		System.out.println(SpecialCharacterTracker.realEOS[1] == SpecialCharacterTracker.replacementEOS[1]);
//		System.out.println(SpecialCharacterTracker.realEOS[2] == SpecialCharacterTracker.replacementEOS[2]);
//	}
	
	/**
	 * Returns a string representation of this SpecialCharacterTracker
	 */
	public String toString() {
		int i;
		int numEOSes = eoses.size();
		String toReturn = "[ ";
		for(i = 0; i < numEOSes; i++){
			toReturn += eoses.get(i).toString() + ", ";
		}
		toReturn = toReturn.substring(0,toReturn.length()-1) + "]";
		return toReturn;
	}
}

enum Specials {EOS, PARENS, QUOTES};

/**
 * Holds the EOS character at a given location in a document, with respect to the beginning of the document.
 * @author Andrew W.E. McDonald
 *
 */
class EOS implements Serializable {

	private static final long serialVersionUID = -3147071940148952343L;
	protected char eos;
	protected int location;
	protected boolean ignore;
	
	/**
	 * Constructor
	 * @param eos the replacement EOS (not an actual EOS character)
	 * @param location
	 */
	public EOS( char eos, int location, boolean ignore) {
		this.eos = eos;
		this.location = location;
		this.ignore = ignore;
	}
	
	public EOS( EOS eosObj) {
		this.eos = eosObj.eos;
		this.location = eosObj.location;
	}
	
	public String toString() {
		return "[ "+eos+", "+location+" ]";
	}
	
	public void setIgnore(boolean b) {
		ignore = b;
	}
}	