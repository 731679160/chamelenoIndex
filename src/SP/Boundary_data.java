package SP;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;

//用于queryVO_data，表示验证边界信息
public class Boundary_data {
    public NodeVerificationVO[] boundary_pi = new NodeVerificationVO[2];
    public int[] boundary_position = new int[2];
    public Element[] boundary_m = new Element[2];
    public long keyword;

    @Override
    public String toString() {
        return Arrays.toString(boundary_pi) + Arrays.toString(boundary_position) + Arrays.toString(boundary_m);
    }
}
