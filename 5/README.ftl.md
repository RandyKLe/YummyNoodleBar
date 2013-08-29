 
## Step 5: Accepting user submitted data

Need to complete a form with the address and name

### create a test using MockMVC


  
### create the html form
--
<html>
    <head>
        <title>Upload a file please</title>
    </head>
    <body>
        <h1>Please upload a file</h1>
        <form method="post" action="/form">
            <input type="text" name="name"/>
            <input type="submit"/>
        </form>
    </body>
</html>

###create a controller

something like ..

TODO, integrate a command/ form bean with validation.

@Controller
public class OrderCreateController {

    @RequestMapping(value = "/doCheckout", method = RequestMethod.POST)
    public String doCheckout(@RequestParam("name") String name,
        @RequestParam("firstLine") String firstLine) {
        ... validate and send order ...
    }

}



