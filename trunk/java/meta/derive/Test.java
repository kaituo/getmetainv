package meta.derive;

import java.util.Arrays;

public class Test {
	public static void main(String[] args) {
		Test t = new Test();
		boolean[] present2 = new boolean[3];
		Arrays.fill(present2, 0, present2.length, true);
		for(int i=0; i<3; i++) 
			System.out.println(present2[i]);
		t.changeArray(present2);
		for(int i=0; i<3; i++) 
			System.out.println(present2[i]);
	}
	
	public void changeArray(boolean[] a) {
		if(a != null && a[0])
			a[0] = false;
	}

}
