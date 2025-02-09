package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.*;
import java.util.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> data = reader.readAll();
            reader.close();
            
            if (data.isEmpty()) return result;
            
            // extracting headings
            String[] headings = data.get(0);
            
            // creating Json arrays
            JsonArray colHeadings = new JsonArray();
            JsonArray prodNums = new JsonArray();
            JsonArray jsonData = new JsonArray();
            colHeadings.addAll(Arrays.asList(headings));
            
            // processing data rows
            for (int i=1; i < data.size(); i++){
                String[] row=data.get(i);
                if (row.length > 0){
                    prodNums.add(row[0]);
                    JsonArray rowData=new JsonArray();
                    
                    for (int j=1; j < row.length; j++){
                        try{
                            rowData.add(Integer.parseInt(row[j])); 
                        }catch (NumberFormatException e){
                            rowData.add(row[j]); 
                        }
                    }
                    jsonData.add(rowData);
                }
            }
            
            // constructing a Json object
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeadings);
            jsonObject.put("Data", jsonData);
            result = jsonObject.toJson();
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
        // parsing the JSON file
        JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);
        JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
        JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
        JsonArray jsonData = (JsonArray) jsonObject.get("Data");

        // using OpenCSV to generate the CSV output
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter, ',', '\"', '\"', "\n");

        // writing the column headers
        writer.writeNext(colHeadings.toArray(new String[0]));

        // writing the data rows
        for (int i=0; i < jsonData.size(); i++){
            JsonArray rowData = (JsonArray) jsonData.get(i);
            List<String> row=new ArrayList<>();
            row.add(prodNums.get(i).toString()); 

            for (Object value : rowData){
                // if-else loop to make sure the episode number has a leading zero if needed.
                if (colHeadings.get(row.size()).equals("Episode") && value instanceof Number){
                    row.add(String.format("%02d", ((Number) value).intValue())); // making the format a 2-digit number.
                } else{
                    row.add(value.toString());
                }
            }
            writer.writeNext(row.toArray(new String[0]));
        }

        writer.close();
        result = stringWriter.toString();


    }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
