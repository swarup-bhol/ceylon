package org.ceylonsmunich.service.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index(){
        return "index";
    }
    @RequestMapping(value = "/signIn")
    public String signIn(){
        return "index";
    }

    @RequestMapping(value = "/images/{id}",method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void icon( @PathVariable("id") String id,HttpServletResponse response) throws IOException {
        ClassPathResource imgFile = new ClassPathResource("static/images/"+ id);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(imgFile.getInputStream(), response.getOutputStream());
    }
}
