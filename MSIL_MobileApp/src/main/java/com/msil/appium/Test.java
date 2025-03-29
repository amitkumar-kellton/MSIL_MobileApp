package com.msil.appium;

public class Test {

	public static void main(String[] args) {
//		String s = "abcabcbb";
//		String t[];

//		Boolean repeat;

		int i;
		int length = 0;
//		String[] itr = { "G", "e", "e", "k", "s", "f", "o", "r", "G", "e", "e", "k", "s" };

		String str = "geeksforgeeks";
		System.out.println("\nString Length is: " + str.length());

		String[] strArray = str.split("");
		String temp;

		for (i = 0; i<=str.length()-1; i++) {
			System.out.println("\n\n\nArray is in the loop i and character of i is " + strArray[i] + "\nValue of i is " + i);
			temp = strArray[i];
			
			for(int j = i+1; j<=str.length()-1; j++) {
				if(temp.equals(strArray[j])) {
					
					System.out.println("\nArray is in the loop j and character of j is " + strArray[j] + "\nValue of j is " + j);
					if(length<j) {
						length = j;
					}
					break;
				}

			}
		}
		
		System.out.println("\n\n\n\nLength is: " + length);

//		
//		for(i = 0; i<= s.length()-1; i++) {
//			String n[] = s.split(s);
//			char t = s.charAt(i);
//			System.out.println("\nArray: " + t);
//
//		}

//		System.out.println("\nArray: " + t);

	}

}
