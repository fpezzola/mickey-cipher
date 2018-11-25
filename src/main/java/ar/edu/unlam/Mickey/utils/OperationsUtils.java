package ar.edu.unlam.Mickey.utils;

public class OperationsUtils {
	
	public int xor(int arg1, int arg2) {
		return arg1 ^ arg2;
	}
	
	public int xor(int arg1, int arg2,int arg3) {
		return arg1 ^ arg2 ^ arg3;
	}
	
	public int signedRightShift(int val,int positions) {
		return val >> positions;
	}

}
