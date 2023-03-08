package tool;

import SP.SearchOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteVO {
    public static long writeVOToLocal(String vo) {
        try {
            File writeName = new File("./src/vo.txt");
            writeName.createNewFile();
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(vo);
            }
            return writeName.length();
//            System.out.println("文件大小：" + writeName.length() + "字节");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String voToStr(SearchOutput res) {
        return res.toString();
    }

}
