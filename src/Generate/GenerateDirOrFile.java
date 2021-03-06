package Generate;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 根据传入参数生成包及文件的类
 *
 * @author sr
 * 2017/10/3
 */
public class GenerateDirOrFile {
    String separator;
    NameAndPath nameAndPath;

    public GenerateDirOrFile(NameAndPath nameAndPath) {
        this.nameAndPath = nameAndPath;
        this.separator = File.separator;
    }

    /**
     * 建立文件夹及文件
     *
     * @param waitForGenerate list本层到结构
     * @param path           生成文件到绝对路径
     * @param tempName       找哪一个temp
     * @param type           生成到文件类型
     * @param genrealPath    java的基础包地址
     * @param willAddPath    java基础包地址后面的每层的地址，根据每一层的名字叠加
     * @throws IOException
     */
    public void deepSearchAndMake(ArrayList waitForGenerate, String path, String tempName, String type, String genrealPath, String willAddPath,String exName) throws Exception {

        boolean hasOneMap = false;
        for (int i = 0; i < waitForGenerate.size(); i++) {
            if (waitForGenerate.get(i) instanceof Map) {
                hasOneMap = true;
            }
        }
        if (hasOneMap) {
            for (int i = 0; i < waitForGenerate.size(); i++) {

                if (waitForGenerate.get(i) instanceof Map) {
                    String addPath = path + separator + ((Map) waitForGenerate.get(i)).keySet().iterator().next();
                    System.out.println("建立文件夹：" + addPath);
                    File f = new File(addPath);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    for (Object s : ((Map) waitForGenerate.get(i)).values()) {
                        //获取到这一层目录到名字，因为只有一个名字，直接取第一个就好类。
                        String nowPack = "";
                        for (Object b : ((Map) waitForGenerate.get(i)).keySet()) {
                            nowPack = (String) b;
                        }
                        System.err.println(((ArrayList) s));
                        deepSearchAndMake((ArrayList) s, addPath, tempName, type, genrealPath, genrealPath + "." + willAddPath + nowPack,exName);
                    }
                } else {
                    System.out.println("建立文件夹 文件：" + path + separator + waitForGenerate.get(i));
                    String addPath = path + separator + waitForGenerate.get(i);
                    makeAndWrite(waitForGenerate, genrealPath, willAddPath, path, i, type, addPath, tempName,exName);
                }
            }
        } else {
            for (int i = 0; i < waitForGenerate.size(); i++) {
                String addPath = path + separator + waitForGenerate.get(i);
                makeAndWrite(waitForGenerate, genrealPath, willAddPath, path, i, type, addPath, tempName,exName);
            }
            return;
        }
    }

    public void makeAndWrite(ArrayList waitForGenerate, String genrealPath, String willAddPath, String path, int i, String type, String addPath, String tempName,String exName) throws Exception {
        System.out.println("建立文件夹及文件：" + addPath);
        File f = new File(addPath);
        f.mkdir();
        HandlerImpl handler = new HandlerImpl((String) waitForGenerate.get(i));
        if (willAddPath.equals("")) {
            handler.setThisPath(genrealPath + "." + waitForGenerate.get(i));
            handler.setImportPath(waitForGenerate.get(i) + "");
        } else {
            handler.setThisPath(willAddPath + "." + waitForGenerate.get(i));
            String temp = willAddPath + "." + waitForGenerate.get(i);
            System.out.println(genrealPath);
            String nowPath = (willAddPath + "." + waitForGenerate.get(i)).split(genrealPath + ".")[1];
            handler.setImportPath(nowPath);

        }
        Parse ss = new Parse(handler);
        FileReader f1 = new FileReader(new File(System.getProperty("user.dir") + separator + "src" + separator + "template" + separator + tempName));
        int j = f1.read();
        String s = "";
        while (j != -1) {
            s += ((char) j);
            j = f1.read();
        }
        String parse = ss.parse(s);
        writeTelplate(path, (String) waitForGenerate.get(i), "." + type, parse,exName);
        f1.close();
    }

    /**
     * 写文件
     *
     * @param path        文件路径
     * @param tempName    文件名
     * @param type        文件类型
     * @param inputString 文件内容
     * @throws IOException
     */
    public void writeTelplate(String path, String tempName, String type, String inputString,String exName) throws IOException {
        tempName=separator+tempName.substring(0, 1).toUpperCase() + tempName.substring(1);
        String fileName="";
        if (exName!=""){
            fileName=tempName+exName;
        }else {
            fileName=tempName;
        }
        FileWriter fw = new FileWriter(path + tempName + File.separator + fileName + type, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(inputString);
        bw.close();
        fw.close();
    }
}
