package SP;

import DO.Insert_data;
import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KeywordTree {
    List<TreeNode> tree = new ArrayList<>();
    public long keyword;
    public KeywordTree(long keyword){
        this.keyword = keyword;
    }
    //建立树
    public void buildTree(List<Insert_data> data, Element rootCommitment){
        TreeNode root = new TreeNode();
        root.c = rootCommitment;
        this.tree.add(root);
        for (Insert_data roundData : data) {
            TreeNode node = new TreeNode();
            node.c = roundData.c;
            node.data = roundData.data;
            node.pi = roundData.pi;
            node.pi_parent = roundData.pi_parent;
            tree.add(node);
        }
    }
    //插入一个节点
    public void insertData(Insert_data insert_data){
        TreeNode node = new TreeNode();
        node.c = insert_data.c;
        node.data = insert_data.data;
        node.pi = insert_data.pi;
        node.pi_parent = insert_data.pi_parent;
        tree.add(node);
    }
    //获取该树某一节点的路径证明信息
    public NodeVerificationVO generatePathVO(int position, HashSet<Element> elementSet){
        return new NodeVerificationVO(position,this.tree, elementSet);
    }
}
