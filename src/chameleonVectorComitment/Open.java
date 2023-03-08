package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class Open {
    public Element pi;
    public Open(int i, CGen KEY, Element r, Element[] vector){
        pi = KEY.hi[i].powZn(r);
        for(int j = 0;j < KEY.q;j++){
            if(j != i){
                pi = pi.mul(KEY.hij[i][j].powZn(vector[j]));
            }
        }
    }
}
