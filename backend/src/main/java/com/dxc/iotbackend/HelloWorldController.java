package com.dxc.iotbackend;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//helloworld end point:

@RestController
public class HelloWorldController {
    ///hello da hwa el path to the end point lma aktbo fe el browser hydenie el string msg
    @GetMapping(path = "/hello")  // we needed to add another annotation to tell springboot that this is an endpoint of our controller
    public String helloWorld() {   //this is a method returning a string
        return "Hello Emmy!";
    }

}
