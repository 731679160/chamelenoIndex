package DO;

import SP.NodeVerificationVO;
import SP.QueryVO_data;
import SP.SearchOutput;
import chameleonVectorComitment.*;
import it.unisa.dia.gas.jpbc.Element;
import prepare.ElementToLong;
import prepare.SHA;
import prepare.compare_Zr;
import prepare.import_data;
import tool.WriteVO;

import java.math.BigInteger;
import java.util.*;

public class DO {
    HashMap<Long, List<Long>> data;
    HashMap<Long, Integer> updState = new HashMap<>();
    String sk;
    CGen KEY;
    public HashMap<Long, List<Insert_data>> insertInformation;
    public Map<Long, Element> rootCommitment;

    public List<Insert_data> getUpdToken(Long id, List<Long> keywords) {
        List<Insert_data> updToken = new ArrayList<Insert_data>();
        for (Long keyword : keywords) {
            updToken.add(insertData(sk, KEY, updState.get(keyword), keyword, id));
        }
        return updToken;
    }

    public void getLocalSize() {
        StringBuilder ans = new StringBuilder();
        ans.append(KEY.g.toString());
        ans.append(Arrays.toString(KEY.hi));
        ans.append(Arrays.toString(KEY.hi));
        ans.append(Arrays.toString(KEY.hij));
        ans.append(KEY.G1.toString());
        ans.append(KEY.Zr.toString());
        ans.append(KEY.pair.toString());
        ans.append("\n");
        for (Map.Entry<Long, Integer> entry : updState.entrySet()) {
            ans.append(entry.getKey().toString());
            ans.append(":" + entry.getValue().toString());
        }
        long size = WriteVO.writeVOToLocal(ans.toString());
        System.out.println("localSize:" + size / 1024 + "kb");
    }

    public DO(String path, String forwardPath, String sk, CGen key) {
//        long start = System.currentTimeMillis();
        //建立每个关键字的倒置索引
        HashMap<Long, List<Long>> data = null;
        HashMap<Long, List<Long>> forwardIndex = null;
        try {
            data = import_data.readGeneratedData(path, updState);
            forwardIndex = import_data.readForwardData(forwardPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.KEY = key;
        this.data = data;
        this.sk = sk;

        for (long i = 1; i <= 10; ++i) {
            getUpdToken(i, forwardIndex.get(i));
        }
        getLocalSize();

//        this.insertInformation = generateInformation(data, sk, KEY);
//        this.rootCommitment = generateRootKeywordCommitment(sk, data, KEY);
        long start = System.currentTimeMillis();
        for (long i = 20; i < 30; ++i) {
            getUpdToken(i, forwardIndex.get(i));
        }
        long end = System.currentTimeMillis();
        System.out.println("索引构造时间：" + (end - start) / 10 + "ms");
    }

    //产生keyword关键字树中某一节点的插入数据验证信息，注意cnt表示该节点前的树中节点数量，例如要产生第一个节点的验证信息，此时cnt = 0
    //该方法是内部方法，主要用于generateInformation方法
    public Insert_data insertData(String sk, CGen KEY, int cnt, long keyword, long data) {
        Element ZERO = KEY.Zr.newElement(new BigInteger("0")).getImmutable();
        Element[] nullVector = {ZERO,ZERO,ZERO};
        Insert_data insert_data = new Insert_data();
        insert_data.data = KEY.Zr.newElement(new BigInteger(String.valueOf(data))).getImmutable();
        insert_data.cnt = ++cnt;
        String s = sk + cnt + keyword;
        Element r = KEY.Zr.newElement(new BigInteger(s)).getImmutable();
        CCom Com = new CCom(nullVector,r,KEY);
        CCol Col = new CCol(0,insert_data.data,KEY.td,Com);
        Open Open = new Open(0,KEY,Col.r_, Com.vector);
        insert_data.pi = Open.pi;
        insert_data.c = Com.c;
        //要证明c3为c1的孩子，但是c3是椭圆曲线上的一点，无法转换为Zn。本方法将c3先取哈希再转成Zn，因此在客户端验证时，也要做相应转化。
        Element childData = KEY.Zr.newElement(SHA.HASHData(Com.c.toString())).getImmutable();
        s = sk + (int)Math.ceil((cnt - 2) / 2.0) + keyword;
        Element r_ = KEY.Zr.newElement(new BigInteger(s)).getImmutable();
        int j;//判断是其父亲的第几个孩子
        if(cnt % 2 == 0){
            j = 2;
        }else{
            j = 1;
        }
        CCom com_par = new CCom(nullVector,r_,KEY);
        CCol col_par = new CCol(j,childData,KEY.td,com_par);
        Open open_par = new Open(j,KEY,col_par.r_, com_par.vector);
        insert_data.pi_parent = open_par.pi;

        return insert_data;
    }

    //传入验证的关键字的根承诺c和某position节点的验证路径信息VO,从而验证树中某该节点存在于这个关键字树中
    public boolean verifyNodePath(Element c, NodeVerificationVO VO, CGen KEY, HashSet<Element> elementSet){
        if (elementSet.contains(VO.c)) {
            return true;
        }
        if(!Ver.Verify(VO.c,0,VO.m,KEY,VO.pi)){
            return false;
        } else {
            elementSet.add(VO.c);
        }
        int position = VO.position;
        Element the_c = VO.c;
        int j;
        int i = 0;
        for(;i < VO.c_parent.size();i++){
            if(position % 2 == 0){
                j = 2;
            }else{
                j = 1;
            }
            if (elementSet.contains(VO.c_parent.get(i))) {
                return true;
            } else {
                elementSet.add(VO.c_parent.get(i));
            }
            if(!Ver.Verify(VO.c_parent.get(i),j, KEY.Zr.newElement(SHA.HASHData(the_c.toString())),KEY,VO.pi_parent.get(i))){
                return false;
            }
            position = (int)Math.ceil((position - 2) / 2.0);
            the_c = VO.c_parent.get(i);
        }
        if(position % 2 == 0){
            j = 2;
        }else{
            j = 1;
        }
        if(!Ver.Verify(c,j,KEY.Zr.newElement(SHA.HASHData(the_c.toString())),KEY,VO.pi_parent.get(i))){
            return false;
        } else {
            elementSet.add(c);
        }
        return true;
    }

    //传入所有关键字的信息，产生所有关键字中每个节点的插入数据验证信息
    public HashMap<Long, List<Insert_data>> generateInformation(HashMap<Long, List<Long>> data, String sk, CGen KEY) {
        HashMap<Long, List<Insert_data>> informationMap = new HashMap<>();

        for (Map.Entry<Long, List<Long>> entry : data.entrySet()) {
            Long keyword = entry.getKey();
            List<Long> idList = entry.getValue();
            List<Insert_data> roundInformation = new ArrayList<>();
            for (int j = 0; j < idList.size(); j++) {
                roundInformation.add(insertData(sk,KEY,j,keyword, idList.get(j)));
            }
             informationMap.put(keyword, roundInformation);
        }
        return informationMap;
    }

    //传入关键字的数量，产生所有关键字的根承诺
    public Map<Long, Element> generateRootKeywordCommitment(String sk, HashMap<Long, List<Long>> data, CGen KEY){
        Map<Long, Element> c = new HashMap<>();
        Element ZERO = KEY.Zr.newElement(new BigInteger("0")).getImmutable();
        Element[] nullVector = {ZERO,ZERO,ZERO};
        for (Map.Entry<Long, List<Long>> entry : data.entrySet()) {
            Long keyword = entry.getKey();
            String s = sk + 0 + keyword;
            Element r = KEY.Zr.newElement(new BigInteger(s));
            c.put(keyword ,(new CCom(nullVector,r,KEY)).c) ;
        }
        return c;
    }

    public boolean verifyVO(SearchOutput queryResult) {
        HashSet<Element> elementSet = new HashSet<>();
        Element nextRoundTarget = queryResult.round_VOs.get(0).target;
        int result_number = 0;
        for(int i = 0;i < queryResult.round_VOs.size();i++){
            QueryVO_data thisRound = queryResult.round_VOs.get(i);
            if (i == 0) {
                if(!verifyNodePath(rootCommitment.get(thisRound.target_keyword),thisRound.target_pi, this.KEY, elementSet)){
                    return false;
                }
            }
            //判断当前伦次的target是否与上一轮的右边界相等
            if(!thisRound.target.equals(nextRoundTarget)){
                return false;
            }
            nextRoundTarget = thisRound.allBoundary_data.get(0).boundary_m[1];
            //判断每轮数据的boundary的正确性
            for(int j = 0;j < thisRound.allBoundary_data.size();j++){
                //除最后一轮以外要求左边界与target相等
                if(j < thisRound.allBoundary_data.size() - 1 && !thisRound.target.equals(thisRound.allBoundary_data.get(j).boundary_m[0])){
                    return false;
                }
                //如果有关键字个数-1轮，若这轮也相等可以加入查询结果
                if(j == rootCommitment.size() - 1 && thisRound.target.equals(thisRound.allBoundary_data.get(j).boundary_m[0])){
                    //如果给定的查询结果与预想的结果不相等或者查询结果数量不对，则返回false
                    if(result_number >= queryResult.result.size() || queryResult.result.get(result_number) != ElementToLong.ElementToLong(thisRound.target)){
                        return false;
                    }
                    result_number++;
                }
                Element left = thisRound.allBoundary_data.get(j).boundary_m[0];
                Element right = thisRound.allBoundary_data.get(j).boundary_m[1];
                //判断boundary的连续性
                if(left != null && right != null && (thisRound.allBoundary_data.get(j).boundary_position[0] + 1) != thisRound.allBoundary_data.get(j).boundary_position[1]){
                    return false;
                }

                //判断boundary存在于树中并判断target在boundary内
                //若左边界不为空
                if(left != null){
                    if(!verifyNodePath(rootCommitment.get(thisRound.allBoundary_data.get(j).keyword),thisRound.allBoundary_data.get(j).boundary_pi[0], KEY, elementSet)){
                        return false;
                    }
                    if(compare_Zr.isBigger(thisRound.target,left) == -1){
                        return false;
                    }
                }
                //若右边界不为空
                if(right != null){
                    if(!verifyNodePath(rootCommitment.get(thisRound.allBoundary_data.get(j).keyword),thisRound.allBoundary_data.get(j).boundary_pi[1],KEY, elementSet)){
                        return false;
                    }
                    if(compare_Zr.isBigger(thisRound.target,right) != -1) {
                        return false;
                    }

                    //当最大右边界不为空，并且当前伦次右边界大于等于最大右边界时，更新最大右边界
                    if(nextRoundTarget !=  null && compare_Zr.isBigger(right,nextRoundTarget) != -1){
                        nextRoundTarget = right;
                    }
                }else{//如果右边界为空，表示扫描应该结束，将nextRoundTarget置成最大即空
                    nextRoundTarget = null;
                }
            }
//            //判断当前target是否在树中
//            if(!verifyNodePath(rootCommitment.get(thisRound.target_keyword),thisRound.target_pi, this.KEY, elementSet)){
//                return false;
//            }
        }
        return true;
    }
}
