package meta.derive;

import java.util.ArrayList;

import daikon.PptTopLevel;

public class PptConfidenceImp {
	private int success;
	private int total;
	private PptTopLevel p1, p2;
    private ArrayList<Double> confidence_p1;
    private ArrayList<Double> confidence_p2;
    private double successrate;
    private double conf_multiplier_p1;
    private double averageImpConf;
	private MetaType mtype;
    
    
	public PptConfidenceImp(PptTopLevel p1, PptTopLevel p2, MetaType mtype) {
		this.success = 0;
		this.total = 0;
		this.p1 = p1;
		this.p2 = p2;
		this.confidence_p1 = new ArrayList<Double>();
		this.confidence_p2 = new ArrayList<Double>();
		this.successrate = 0;
		this.conf_multiplier_p1 = 0;
		this.averageImpConf = 0;
		this.mtype = mtype;
	}

	public void computeSuccessrate() {
		successrate = (double)success / (double)total;
	}
	
	public double getSuccessrate() {
		return successrate;
	}
	
	
	
	public void incrementSuccess() {
		success++;
	}
	
	public void incrementTotal() {
		total++;
	}
	
	public void multiplyConfidence() {
		int s = confidence_p1.size();
		for(int i=0; i<s; i++) {
			if(i == 0)
				conf_multiplier_p1 = confidence_p1.get(0);
			else
				conf_multiplier_p1 *= confidence_p1.get(i);
		}
	}
	
	public double getConf_Multiplier_P1() {
		return conf_multiplier_p1;
	}
	
	public void averageImpConfidence() {
		double sum = 0;
		int s = confidence_p2.size();
		for(int i=0; i<s; i++)
			sum += confidence_p2.get(i);
		averageImpConf = sum * conf_multiplier_p1 / s;
	}
	
	public double getImpConfidence() {
		return averageImpConf;
	}

	public PptTopLevel getP1() {
		return p1;
	}

	public void setP1(PptTopLevel p1) {
		this.p1 = p1;
	}

	public PptTopLevel getP2() {
		return p2;
	}

	public void setP2(PptTopLevel p2) {
		this.p2 = p2;
	}
	
	public void putConfidence_p1(double c) {
		confidence_p1.add(c);
	}
	
	public void putConfidence_p2(double c) {
		confidence_p2.add(c);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PptConfidenceImp other = (PptConfidenceImp) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PptConfidenceImp [p1=" + p1 + ", p2=" + p2 + ", successrate="
				+ successrate + ", averageImpConf=" + averageImpConf + "]";
	}

	public MetaType getMtype() {
		return mtype;
	}

	public void setMtype(MetaType mtype) {
		this.mtype = mtype;
	}	
}
