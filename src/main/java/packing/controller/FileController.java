package packing.controller;

import packing.payload.Box;
import packing.payload.BoxItem;
import packing.payload.BruteForcePackager;
import packing.payload.CSV2JSON;
import packing.payload.Container;
import packing.payload.Packager;
import packing.payload.UploadFileResponse;
import packing.service.FileStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.PathContainer.Element;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FileController 
{
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    private CSV2JSON csv_json;

    @RequestMapping("/users")     
    public @ResponseBody String getUsers() 
    {     	
    	return "succesfully executed users call";     
    } 
    
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) 
    {
    	
    	System.out.println(Arrays.asList(files));
    	List output = new ArrayList();
    	List output1 = new ArrayList();
    	
 
    	  
      
    	for(MultipartFile s : files)
    	{
    		/*System.out.println(s.getOriginalFilename());
    		try 
    		{
    			al = csv_json.csvtojson(s.getOriginalFilename());
			} 
    		catch (Exception e) 
    		{
				e.printStackTrace();
			}*/
    		boolean rotate3d = true;
    		List<Container> containers = new ArrayList<Container>();
    		containers.add(new Container(20, 20, 20, 24)); // x y z and weight
    		//Packager packager = new LargestAreaFitFirstPackager(containers, rotate3d, true, true);
    		
    		Packager packager = new BruteForcePackager(containers);
    		
    		List<BoxItem> products = new ArrayList<BoxItem>();
    		products.add(new BoxItem(new Box("Foot", 15, 15, 15, 1), 1)); // x,z,y,w
    		products.add(new BoxItem(new Box("Leg", 5, 20, 5, 1), 5));
    		//products.add(new BoxItem(new Box("Arm", 4, 10, 2, 50), 1));
    		
    		//Container match = packager.pack(products);
    		
    		long deadline = System.currentTimeMillis() + 5000;
    		Container match = packager.pack(products, deadline);
    		String final_output = match.toString();
    		System.out.println(final_output);
    		
    		fileStorageService.writeJsonData(final_output);
    	}
        
    	
        output = Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
        
        Iterator<UploadFileResponse> iterator = output.iterator();
    	while (iterator.hasNext()) {
    		System.out.println(iterator.next());
    	}
        
        return output;
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
