package SP;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


//表示某树中一节点的路径验证信息，主要被keywordTree中generatePathVO方法调用
public class NodeVerificationVO {
    public Element c;
    public Element pi;
    public Element m;
    public int position;
    public List<Element> c_parent;
    public List<Element> pi_parent;
    public NodeVerificationVO(int position, List<TreeNode> tree, HashSet<Element> elementSet){
        this.m = tree.get(position).data;
        this.position = position;
        int length = (int)Math.floor(Math.log(position + 1) / Math.log(2));
        //c_parent不存储根承诺
        c_parent = new ArrayList<>();
        //pi_parent从该节点的ρ开始存储
        pi_parent = new ArrayList<>();
        TreeNode thisNode = tree.get(position);
        this.c = thisNode.c;
        if (elementSet.contains(this.c)) {
            return;
        } else {
            elementSet.add(this.c);
        }
        this.pi = thisNode.pi;
        elementSet.add(c);
        int i = 0;
        for(;i < length - 1;i++){
           pi_parent.add(thisNode.pi_parent);
           position = (int)Math.ceil((position - 2) / 2.0);
           thisNode = tree.get(position);
           c_parent.add(thisNode.c);
           if (elementSet.contains(thisNode.c)) {
               return;
           } else {
               elementSet.add(thisNode.c);
           }
        }
        pi_parent.add(thisNode.pi_parent);
    }

    @Override
    public String toString() {
        String piStr = pi == null ? "" : pi.toString();
        return c.toString() +
                piStr +
                m.toString() +
                position +
                c_parent.toString() +
                pi_parent.toString()
                ;
    }
}
