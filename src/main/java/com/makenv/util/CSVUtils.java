package com.makenv.util;

import org.apache.commons.beanutils.BeanUtils;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/7/10.
 */
public class CSVUtils {

    /**
     * 生成为CVS文件
     * @param exportData
     *       源数据List
     * @param map
     *       csv文件的列表头map
     * @param outPutPath
     *       文件路径
     * @param fileName
     *       文件名称
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static File createCSVFile(List exportData, LinkedHashMap map, String outPutPath,
                                     String fileName) {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {
            File file = new File(outPutPath);
            if (!file.exists()) {
                file.mkdir();
            }
            //定义文件名格式并创建
            csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
            System.out.println("csvFile：" + csvFile);
            // UTF-8使正确读取分隔符","
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "UTF-8"), 1024);
            System.out.println("csvFileOutputStream：" + csvFileOutputStream);
            // 写入文件头部
            for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext();) {
                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                csvFileOutputStream
                        .write("" + (String) propertyEntry.getValue() != null ? (String) propertyEntry
                                .getValue() : "" + "");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {
                Object row = (Object) iterator.next();
                for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
                        .hasNext();) {
                    Map.Entry propertyEntry = (Map.Entry) propertyIterator
                            .next();
                    csvFileOutputStream.write((String) BeanUtils.getProperty(row,
                            (String) propertyEntry.getKey()));
                    if (propertyIterator.hasNext()) {
                        csvFileOutputStream.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    csvFileOutputStream.newLine();
                }
            }
            csvFileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 生成为CVS文件
     * @param exportData
     *       源数据List
     * @param map
     *       csv文件的列表头map
     *
     * @param filePath
     *       文件名称
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static File createCSVFile(List exportData, LinkedHashMap map,
                                     String filePath) {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {

            //定义文件名格式并创建
            csvFile = new File(filePath);

            if(!csvFile.exists())

                csvFile.createNewFile();

            // UTF-8使正确读取分隔符","
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "UTF-8"), 1024);
            // 写入文件头部
            for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext();) {
                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                csvFileOutputStream
                        .write("" + (String) propertyEntry.getValue() != null ? (String) propertyEntry
                                .getValue() : "" + "");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {
                Object row = (Object) iterator.next();
                for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
                        .hasNext();) {
                    Map.Entry propertyEntry = (Map.Entry) propertyIterator
                            .next();
                    csvFileOutputStream.write(BeanUtils.getProperty(row,
                            String.valueOf(propertyEntry.getKey())));
                    if (propertyIterator.hasNext()) {
                        csvFileOutputStream.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    csvFileOutputStream.newLine();
                }
            }
            csvFileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }
}
