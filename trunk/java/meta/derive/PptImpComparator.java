package meta.derive;

import java.util.Comparator;

public class PptImpComparator implements Comparator<PptConfidenceImp> {
	
	@Override
	public int compare(PptConfidenceImp p1, PptConfidenceImp p2) {
		double c1 = p1.getImpConfidence();
		double c2 =  p2.getImpConfidence();
		if(c1 < c2)
			return -1;
		else if(c1 > c2)
			return 1;
		else {
			double s1 = p1.getSuccessrate();
			double s2 = p2.getSuccessrate();
			if(s1 < s2)
				return -1;
			else if(s1 > s2)
				return 1;
			else
				return 0;
		}
	}

}
