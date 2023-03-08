package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Element;

public class CCol {
    public Element r_;
    public CCol(int i,Element m_,Element[] td,CCom aux){
        this.r_ = aux.r.add(td[i].mulZn(aux.vector[i].sub(m_)));
    }
}
