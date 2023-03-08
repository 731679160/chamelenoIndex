package prepare;

import it.unisa.dia.gas.jpbc.Element;

public class ElementToLong {
    public static long ElementToLong(Element input){
        return input.toBigInteger().longValue();
    }
}
