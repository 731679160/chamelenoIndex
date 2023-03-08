package SP;

import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

public class QueryVO_data {
    public long target_keyword;
    public Element target;
    public NodeVerificationVO target_pi;
    public int target_position;
    public List<Boundary_data> allBoundary_data;

    @Override
    public String toString() {
        String target_piStr = target_pi == null ? "" : target_pi.toString();
        String boundaryStr = allBoundary_data == null ? "" : allBoundary_data.toString();
        return target_piStr + target_position + boundaryStr;
    }
}
