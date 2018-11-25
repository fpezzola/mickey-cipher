package ar.edu.unlam.Mickey.cipher;

import ar.edu.unlam.Mickey.utils.OperationsUtils;

public class MickeyCipher {
    
public final int REGISTER_LENGTH = 100; 
 
public MickeyContext context;
public OperationsUtils utils;

public final int [] rTap = {
        0,0,0,1,0,1,1,0,1,1,0,0,1,1,0,0,1,0,0,1,1,1,1,0,0,
        1,1,0,1,0,0,0,1,1,0,1,0,1,0,0,0,1,1,0,0,1,1,0,0,0,
        1,1,1,0,1,0,0,1,1,0,1,1,0,1,1,1,1,1,0,1,0,1,1,0,0,
        0,1,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,0,0};
    
public final int [] comp0 = {
        0,0,0,0,1,1,0,0,0,1,0,1,1,1,1,0,1,0,0,1,0,1,0,1,0,
        1,0,1,0,1,1,0,1,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,1,
        0,0,0,0,1,0,1,0,0,1,1,1,1,0,0,1,0,1,0,1,1,1,1,1,1,
        1,1,1,0,1,0,1,1,1,1,1,1,0,1,0,1,0,0,0,0,0,0,1,1};
 
public final int [] comp1 = {
        0,1,0,1,1,0,0,1,0,1,1,1,1,0,0,1,0,1,0,0,0,1,1,0,1,
        0,1,1,1,0,1,1,1,1,0,0,0,1,1,0,1,0,1,1,1,0,0,0,0,1,
        0,0,0,1,0,1,1,1,0,0,0,1,1,1,1,1,1,0,1,0,1,1,1,0,1,
        1,1,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0,0,1,1,0,0};
 
public final int [] fb0 = {
        1,1,1,1,0,1,0,1,1,1,1,1,1,1,1,0,0,1,0,1,1,1,1,1,1,
        1,1,1,1,0,0,1,1,0,0,0,0,0,0,1,1,1,0,0,1,0,0,1,0,1,
        0,1,0,0,1,0,1,1,1,1,0,1,0,1,0,1,0,0,0,0,0,0,0,0,0,
        1,1,0,1,0,0,0,1,1,0,1,1,1,0,0,1,1,1,0,0,1,1,0,0,0};
 
public final int [] fb1 = {
        1,1,1,0,1,1,1,0,0,0,0,1,1,1,0,1,0,0,1,1,0,0,0,1,0,
        0,1,1,0,0,1,0,1,1,0,0,0,1,1,0,0,0,0,0,1,1,0,1,1,0,
        0,0,1,0,0,0,1,0,0,1,0,0,1,0,1,1,0,1,0,1,0,0,1,0,1,
        0,0,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,1};


public MickeyCipher(int [] key, int [] initVector) {
    context = new MickeyContext(key);
    utils = new OperationsUtils();

    for (int i = 0; i < initVector.length * 8; ++i) {
        clockKG(true, utils.signedRightShift(initVector[i / 8],(7 - (i % 8))) & 1);
    }
    
    for (int i = 0; i < 80; ++i) {
        clockKG(true, utils.signedRightShift(key[i / 8],(7 - (i % 8))) & 1);
    }
    
    for (int i = 0; i < 100; ++i) {
        clockKG(true, 0);
    }
}

 
private void clockR(
        int inputBitR,
        int controlBitR) {
    int feedbackBit = utils.xor(context.getR(99), inputBitR);
    
    if (controlBitR == 1) {
        if (feedbackBit == 1) {
            for (int i = 99; i > 0; --i) {
                context.setR(i,utils.xor(context.getR(i - 1), context.getR(i), rTap[i]));
            }
            context.setR(0,utils.xor(rTap[0], context.getR(0)));
        } else {
            for (int i = 99; i > 0; --i) {
            	context.setR(i,utils.xor(context.getR(i - 1), context.getR(i)));
            }
        }
    } else {
        if (feedbackBit == 1) {
            for (int i = 99; i > 0; --i) {
            	context.setR(i,utils.xor(context.getR(i - 1),rTap[i]));
            }
            context.setR(0,rTap[0]);
        } else {
            for (int i = 99; i > 0; --i) {
            	context.setR(i,context.getR(i - 1));
            }   
            context.setR(0,0);
        }
    }
}
 
private void clockS(
        int inputBitS,
        int controlBitS) {
    int [] sHat = new int [100];
    int feedbackBit = utils.xor(context.getS(99),inputBitS);
    
    for (int i = 98; i > 0; --i) {
        sHat[i] = utils.xor(context.getS(i - 1),((utils.xor(context.getS(i),comp0[i])) & (utils.xor(context.getS(i + 1),comp1[i]))));
    }
    sHat[0] = 0;
    sHat[99] = context.getS(98);
    
    for (int i = 0; i < 100; ++i) {
        context.setS(i,sHat[i]);
    }
    
    if (feedbackBit == 1) {
        if (controlBitS == 1) {
            for (int i = 0; i < 100; ++i) {
                context.setS(i,utils.xor(sHat[i],fb1[i]));
            }   
        } else {
            for (int i = 0; i < 100; ++i) {
                context.setS(i,utils.xor(sHat[i],fb0[i]));
            }
        }
    }
}
 
private int clockKG(boolean mixing, int inputBit) {
    int keystreamBit = (utils.xor(context.getR(0), context.getS(0))) & 1;
    int controlBitR = utils.xor(context.getS(34), context.getR(67));
    int controlBitS =  utils.xor(context.getS(67),context.getR(33));
    
    if (mixing) {
        clockR(utils.xor(inputBit,context.getS(50)), controlBitR);
    } else {
        clockR(inputBit, controlBitR);
    }
    
    clockS(inputBit, controlBitS);
    
    return keystreamBit;
}
 

 
public int [] encrypt(int [] bytes) {
    int [] output = new int [bytes.length];
    for (int i = 0; i < bytes.length; ++i) {
        output[i] = bytes[i];
        for (int j = 0; j < 8; ++j) {
            output[i] ^= clockKG(false, 0) << (7 - j);
        }
    }
    return output;
}
 
}