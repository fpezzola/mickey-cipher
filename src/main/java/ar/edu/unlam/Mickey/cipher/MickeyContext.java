package ar.edu.unlam.Mickey.cipher;

public class MickeyContext {
	
	private int [] r = new int [100];
    private int [] s = new int [100];
    private int [] key = new int [80];
    
    public MickeyContext(int[] key) {
    	this.key = key;
        for (int i = 0; i < 100; ++i) {
            r[i] = 0;
            s[i] = 0;
        }
    }
    
    
	public int[] getR() {
		return r;
	}
	public void setR(int[] r) {
		this.r = r;
	}
	
	public void setR(int position, int value) {
		this.r[position] = value;
	}
	
	public void setS(int position, int value) {
		this.s[position] = value;
	}
	
	
	public int[] getS() {
		return s;
	}
	public void setS(int[] s) {
		this.s = s;
	}
	public int[] getKey() {
		return key;
	}
	public void setKey(int[] key) {
		this.key = key;
	}
    
	public int getR(int position) {
		return this.r[position];
	}
	
	public int getS(int position) {
		return this.s[position];
	}
	
    
    

}
