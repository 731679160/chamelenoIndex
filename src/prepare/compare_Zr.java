package prepare;

import chameleonVectorComitment.CGen;
import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class compare_Zr {
    public static int isBigger(Element a,Element b){
        BigInteger a_ = a.toBigInteger();
        BigInteger b_ = b.toBigInteger();
        return a_.compareTo(b_);
    }

    public static void main(String[] args) {
        CGen KEY = new CGen(3);
        Element a = KEY.Zr.newElement(new BigInteger(String.valueOf(12))).getImmutable();
        Element b = KEY.Zr.newElement(new BigInteger(String.valueOf(11))).getImmutable();
        System.out.println(isBigger(a,b));
    }
}
