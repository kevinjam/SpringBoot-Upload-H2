package com.kevinjan.learningSpring.learning.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * Created by kevinjanvier on 12/06/2017.
 */
@Controller
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;


//TODO The field file exceeds its maximum permitted size of 1048576 bytes.

    @Autowired
    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/")
    public String index(Model model, Pageable pageable){
        final Page<Image> page = imageService.findPage(pageable);
        model.addAttribute("page", page);

    //check if the image exist
        if (page.hasNext()){
            model.addAttribute("next", pageable.next());
        }


        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String filename){
        try {
            Resource file = imageService.findOneImage(filename);
            return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        }catch (IOException e){
            return ResponseEntity.badRequest()
                    .body("Couldn't find " + filename + "=>" + e.getMessage());
        }

    }


    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    public String createfile(@RequestParam("file")MultipartFile file,
                             RedirectAttributes redirectAttributes){
        try {
            imageService.createImage(file);
            redirectAttributes.addFlashAttribute("flash.message", "Successfuly uploaded"
                    + file.getOriginalFilename());

        }catch (IOException e){
            redirectAttributes.addFlashAttribute("flash.message", "Fail to Upload "
                    + file.getOriginalFilename());


        }

        return "redirect:/";
    }

//
//    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
//    @ResponseBody
//    public ResponseEntity<?> deleteFile(@PathVariable String filename){
//        try {
//            imageService.deleteImage(filename);
//            return ResponseEntity.status(HttpStatus.NO_CONTENT)
//                    .body("Successul deleted " + filename);
//        }catch (IOException e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to upload " + filename + "=>" + e.getMessage());
//
//        }
//    }



    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
//    @ResponseBody
    public String deleteFile(@PathVariable String filename , RedirectAttributes redirectAttributes){
        try {
            imageService.deleteImage(filename);
            redirectAttributes.addFlashAttribute("flash.message", "successfully");
        }catch (IOException e){
            redirectAttributes.addFlashAttribute("flash.message", "failed to delete " + filename + "=>" + e.getMessage());

        }
        return "redirect:/";
    }
}
