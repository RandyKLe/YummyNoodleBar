 
## Step 6: Uploading files

need a semi plausible reason why we need a file upload ....

### create a test using MockMVC

figure out how mockmvc handles multipart.
http://forum.springsource.org/showthread.php?133813-Writing-an-integration-test-for-file-upload-controller-with-spring-test-mvc

### Update the web config to enable support for servlet 3

update the web config (javax.servlet.MultipartConfigElement)

### Configure the multipart resolver

create a new bean in the web @Configuration

void MultipartResolver multipartResolver() {
  return new StandardServletMultipartResolver();
}

<bean id="multipartResolver"
    class="org.springframework.web.multipart.support.StandardServletMultipartResolver">
</bean>

### create the html form
--
<html>
    <head>
        <title>Upload a file please</title>
    </head>
    <body>
        <h1>Please upload a file</h1>
        <form method="post" action="/form" enctype="multipart/form-data">
            <input type="text" name="name"/>
            <input type="file" name="file"/>
            <input type="submit"/>
        </form>
    </body>
</html>

###create a controller

something like ..

@Controller
public class FileUploadController {

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("name") String name,
        @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            // store the bytes somewhere
           return "redirect:uploadSuccess";
       } else {
           return "redirect:uploadFailure";
       }
    }

}

