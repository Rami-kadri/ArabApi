package com.arabsoft.file.controller;


import com.arabsoft.file.helper.CSVHelper;
import com.arabsoft.file.message.ResponseMessage;
import com.arabsoft.file.model.File;
import com.arabsoft.file.service.FileStorageService;
import com.arabsoft.file.service.JsonExporter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;


@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    public List<File> myfile;
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, Model model) {
    
      
            String message = "";

            if (CSVHelper.hasCSVFormat(file)) {
            	fileStorageService.storeFile(file);
            
              try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream())))  
            	  
            	   {

      				CsvToBean<File> csvToBean = new CsvToBeanBuilder<File>(reader)
                              .withType(File.class)
                              .withIgnoreLeadingWhiteSpace(true)
                              .build();

                      List<File> files = csvToBean.parse();
                      myfile=files;
            	  
                      model.addAttribute("files", files);
                      model.addAttribute("status", true);
              
            	   message = "Uploaded the file successfully: " + file.getOriginalFilename();
            	    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            	   } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                
            	   }
              }
            
            message = "Please upload a csv file!";
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
          
                    
    }
    
   
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Autowired
	private JsonExporter jsonExporter;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/downloadJson")
	public ResponseEntity<byte[]> downloadJsonFile() {
		
		

		String myfileJsonString = jsonExporter.export(myfile); 
		
		byte[] customerJsonBytes = myfileJsonString.getBytes();
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=customers.json")
				.contentType(MediaType.APPLICATION_JSON)
				.contentLength(customerJsonBytes.length)
				.body(customerJsonBytes);
	}
	
}
