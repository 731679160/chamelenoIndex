package chameleonVectorComitment;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

public class CGen {
    public Field G1;
    public Field Zr;
    public Element g;
    public Pairing pair;
    public int q;
    public Element[] td;
    public Element[] hi;
    public Element[][] hij;
    public CGen(int q){
        PairingParameters pairingParameters = PairingFactory.getPairingParameters("jars/a.properties");
        pair = PairingFactory.getPairing(pairingParameters);
        G1 = pair.getG1();
        Zr = pair.getZr();
        g = G1.newRandomElement().getImmutable();
        td = new Element[q];
        hi = new Element[q];
        hij = new Element[q][q];
        for(int i = 0;i < q;i++){
            td[i] = Zr.newRandomElement().getImmutable();
            hi[i] = g.powZn(td[i]);
        }
        for(int i = 0;i < q;i++){
            for(int j = 0;j < q;j++){
                if(j == i) {
                    hij[i][j] = null;
                }else{
                    hij[i][j] = g.powZn(td[i].mulZn(td[j]));
                }
            }
        }
        this.q = q;
    }
}
