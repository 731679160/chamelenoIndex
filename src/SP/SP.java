package SP;

import DO.*;
import it.unisa.dia.gas.jpbc.Element;
import prepare.ElementToLong;
import prepare.compare_Zr;
import tool.WriteVO;

import java.security.Key;
import java.util.*;

public class SP {
    HashMap<Long, KeywordTree> trees;
    public SP (HashMap<Long, List<Insert_data>> insertInformation, Map<Long, Element> rootCommitment) {
        trees = new HashMap<>();
        for (Map.Entry<Long, List<Insert_data>> entry : insertInformation.entrySet()) {
            Long keyword = entry.getKey();
            List<Insert_data> InsertInfo = entry.getValue();
            KeywordTree keywordTree = new KeywordTree(keyword);
            keywordTree.buildTree(InsertInfo, rootCommitment.get(keyword));
            trees.put(keyword, keywordTree);
        }
        getIndexSize();
    }

    private String treeToStr(KeywordTree tree) {
        StringBuffer stringBuffer = new StringBuffer();
        for (TreeNode node : tree.tree) {
            if (node == null) {
                stringBuffer.append("");
                continue;
            }
            if (node.c != null) {
                stringBuffer.append(node.c.toString() + " ");
            }
            if (node.pi != null) {
                stringBuffer.append(node.pi.toString() + " ");
            }
            if (node.pi_parent != null) {
                stringBuffer.append(node.pi_parent.toString() + " ");
            }
            if (node.data != null) {
                stringBuffer.append(node.data.toString() + " ");
            }
        }
        return stringBuffer.toString();
    }

    public void getIndexSize() {
        StringBuffer ans = new StringBuffer();
        for (Map.Entry<Long, KeywordTree> entry : trees.entrySet()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(entry.getKey() + " ");
            stringBuffer.append(treeToStr(entry.getValue()));
            ans.append(stringBuffer.toString());
        }
        long size = WriteVO.writeVOToLocal(ans.toString());
        System.out.println("索引大小：" + size / 1024.0 / 1024 + "MB");
    }
    public SearchOutput MultiKeywordQuery(long[] queryRequest){
        HashSet<Element> elementSet = new HashSet<>();
        SearchOutput searchOutput = new SearchOutput();
        int keyword_number = queryRequest.length;
        if(keyword_number == 0){
            return null;
        }
        List<KeywordTree> keywordTrees = new ArrayList<>();
        for(int i = 0;i < queryRequest.length;i++){
            keywordTrees.add(trees.get(queryRequest[i]));
        }
        int[] positions = new int[keyword_number];
        //初始化第一轮扫描的位置，其他位置仍然为0
        positions[0] = 1;
        int round = 0;
        while(true){
            int nextRound = round;
            QueryVO_data round_VO = new QueryVO_data();
            round_VO.target = keywordTrees.get(round).tree.get(positions[round]).data;
            round_VO.target_position = positions[round];
            round_VO.target_keyword = keywordTrees.get(round).keyword;
            if (searchOutput.round_VOs.size() == 0) {
                round_VO.target_pi = keywordTrees.get(round).generatePathVO(positions[round], elementSet);
            }
            boolean arriveBoundary = false;
            round_VO.allBoundary_data = new ArrayList<>();
            int real_nextRound = (round + 1) % keyword_number;
            do{
                nextRound = (nextRound + 1) % keyword_number;
                //若扫描了所有树，加入结果退出循环
                if(nextRound == round){
                    searchOutput.result.add(ElementToLong.ElementToLong(round_VO.target));
                    break;
                }
                Boundary_data boundary = new Boundary_data();
                boundary.keyword = keywordTrees.get(nextRound).keyword;

                //在下一轮数据中进行搜索，若小于等于当前target继续搜索，直到下一轮所有数据扫描完毕或者找到数据
                while(positions[nextRound] != (keywordTrees.get(nextRound).tree.size() - 1 ) && compare_Zr.isBigger(keywordTrees.get(nextRound).tree.get(positions[nextRound] + 1).data,round_VO.target) != 1){
                    positions[nextRound] ++;
                }
                //若到达边界表示没有比目标值更大的右边界，结束循环。
                if(positions[nextRound] == keywordTrees.get(nextRound).tree.size() - 1){
                    boundary.boundary_position[0] = positions[nextRound];
                    boundary.boundary_m[0] = keywordTrees.get(nextRound).tree.get(boundary.boundary_position[0]).data;
                    boundary.boundary_pi[0] = keywordTrees.get(nextRound).generatePathVO(boundary.boundary_position[0], elementSet);
                    arriveBoundary = true;
                    real_nextRound = -1;//表示已到达边界，没有下一轮循环
                }else {//若没有达到边界
                    boundary.boundary_position[0] = positions[nextRound];
                    boundary.boundary_position[1] = positions[nextRound] + 1;
                    if (boundary.boundary_position[0] != 0) {//如果左边界不为0表示有左边界，需要加入左边界的pi
                        boundary.boundary_m[0] = keywordTrees.get(nextRound).tree.get(boundary.boundary_position[0]).data;
                        boundary.boundary_pi[0] = keywordTrees.get(nextRound).generatePathVO(boundary.boundary_position[0], elementSet);
                    }
                    boundary.boundary_m[1] = keywordTrees.get(nextRound).tree.get(boundary.boundary_position[1]).data;
                    boundary.boundary_pi[1] = keywordTrees.get(nextRound).generatePathVO(boundary.boundary_position[1], elementSet);//加入右边界信息
                }
                round_VO.allBoundary_data.add(boundary);
                //使边界对象的position都指到其右边界
                positions[nextRound] ++;
                //如果没有到达边界。需要找到最合适的下一轮target
                if(real_nextRound != -1){
                    //如果当前伦次右边界不比以前的小，则更新
                    if(compare_Zr.isBigger(boundary.boundary_m[1],keywordTrees.get(real_nextRound).tree.get(positions[real_nextRound]).data) != -1){
                        real_nextRound = nextRound;
                    }
                }
            }while(round_VO.target.equals(keywordTrees.get(nextRound).tree.get(positions[nextRound] - 1).data));
            searchOutput.round_VOs.add(round_VO);
            if(arriveBoundary){
                break;
            }
            round = real_nextRound;
        }
        return searchOutput;
    }
}
