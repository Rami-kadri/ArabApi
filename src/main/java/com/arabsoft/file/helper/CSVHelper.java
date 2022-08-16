package com.arabsoft.file.helper;


import java.util.List;


import org.springframework.web.multipart.MultipartFile;

import com.arabsoft.file.model.File;



public class CSVHelper {
  public static String TYPE = "text/csv";
  //static String[] HEADERs = { "Id", "Title", "Description", "Published" };
  static List<File> files;
  public static boolean hasCSVFormat(MultipartFile file) {

    if (!TYPE.equals(file.getContentType())) {
      return false;
    }

    return true;
  }

  
}
