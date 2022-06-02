import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/main/java/data.csv";
        String fileNameXml = "src/main/java/data.xml";
        String jspnForCsv = "src/main/java/dataCsv.json";
        String jspnForXml = "src/main/java/dataXml.json";
        List<Employee> list2 = parseXML(fileNameXml);
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        String jsonXML = listToJson(list2);
        writeString(json,jspnForCsv);
        writeString(jsonXML,jspnForXml);
    }

    private static List<Employee> parseXML(String s)  {
        List<Employee> listXML = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(s));
            Node root = document.getDocumentElement();
            System.out.println("Корневой элемент " + root.getNodeName());
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.ELEMENT_NODE != node.getNodeType()) {
                    continue;
                }
                System.out.println("Узел " + node.getNodeName());
                Employee employeeTemp = new Employee();
                NodeList nodeList1 = node.getChildNodes();
                for (int k = 0; k < nodeList1.getLength(); k++) {
                    Node child = nodeList1.item(k);
                    if (child.getNodeName().equals("id")) {
                        employeeTemp.id = Long.parseLong(child.getTextContent());
                    }
                    if (child.getNodeName().equals("firstName")) {
                        employeeTemp.firstName = child.getTextContent();
                    }
                    if (child.getNodeName().equals("lastName")) {
                        employeeTemp.lastName = child.getTextContent();
                    }
                    if (child.getNodeName().equals("country")) {
                        employeeTemp.country = child.getTextContent();
                    }
                    if (child.getNodeName().equals("age")) {
                        employeeTemp.age = Integer.parseInt(child.getTextContent());
                    }
                }
                listXML.add(employeeTemp);
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return listXML;
        }

    private static void writeString(String json, String name){
        try (FileWriter fileWriter = new FileWriter(name)){
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
