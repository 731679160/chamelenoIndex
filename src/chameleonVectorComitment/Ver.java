package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Element;

public class Ver {
    public static boolean  Verify(Element c,int i, Element m,CGen KEY,Element pi){
        Element left = KEY.pair.pairing(c.div((KEY.hi[i]).powZn(m)),KEY.hi[i]);
        Element right = KEY.pair.pairing(pi,KEY.g);
        if(left.equals(right)){
            return true;
        }else{
            return false;
        }
    }
}
