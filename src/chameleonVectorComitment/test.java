package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;


public class test {
    public static void main(String[] args) {
        CGen KEY = new CGen(3);
        Element ZERO = KEY.Zr.newElement(new BigInteger("0")).getImmutable();
        Element[] nullVector = {ZERO,ZERO,ZERO};
        Element r = KEY.Zr.newRandomElement().getImmutable();
        CCom c = new CCom(nullVector,r,KEY);
        Open pi = new Open(2,KEY,c.r,c.vector);
        Element m_ = KEY.Zr.newElement(new BigInteger("11")).getImmutable();
        CCol col = new CCol(2,m_,KEY.td,c);
        Open pi_ = new Open(2,KEY,col.r_,c.vector);

        System.out.println(Ver.Verify(c.c,2,m_,KEY,pi_.pi));


    }
}
