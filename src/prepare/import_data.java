package prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class import_data {
    public  static HashMap<Long, List<Long>> readGeneratedData(String path, HashMap<Long, Integer> updState) throws Exception{
        HashMap<Long, List<Long>> map = new HashMap<>();
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
                String[] data = str.split(" ");
                List<Long> row = new ArrayList<Long>();
                for(int i = 1;i < data.length;i++){
                    row.add((Long.valueOf(data[i])));
                }
                map.put(Long.valueOf(data[0]), row);
                updState.put(Long.valueOf(data[0]), data.length - 1);
            }
            scr.close();
            fla.close();
        }
        return map;
    }

    public static HashMap readForwardData(String path) throws Exception{
        HashMap<Long, List<Long>> forwardIndexMap = new HashMap<>();
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
                String[] data = str.split(" ");
                List<Long> keywords = new ArrayList<Long>();
                for(int i = 1;i < data.length;i++){//第三个才是文档id
                    keywords.add(Long.valueOf(data[i]));
                }
                keywords.sort(new Comparator<Long>() {
                    @Override
                    public int compare(Long o1, Long o2) {
                        return (int)(o1 - o2);
                    }
                });
                forwardIndexMap.put(Long.valueOf(data[0]), keywords);
            }
            scr.close();
            fla.close();
        }
        return forwardIndexMap;
    }
}