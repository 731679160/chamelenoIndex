package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Element;

public class CCom {
    public Element r;
    public Element c;
    public Element[] vector;
    public CCom(Element[] vector,Element r,CGen KEY){
        this.r = r;
        this.vector = vector;
        c = KEY.g.powZn(r);
        for(int i = 0;i < KEY.q;i++) {
            c = c.mul(KEY.hi[i].powZn(vector[i]));
        }
    }
}
