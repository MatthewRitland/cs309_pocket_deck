package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    // @GetMapping("path") = listens for GET request with that specific path
    // so @GetMapping("/") is listening for a GET request with
    // root directory (http://localhost:8080/)

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to mattrit experiment #1";
    }

    //using @GetMapping("/{variable}") listens for a GET request with root directory
    // followed by /anything (anything = variable)
    // such as http://localhost:8080/matthew will now use the method below the @GetMapping("/{name}")
    @GetMapping("/{name}")
    // must use @PathVariable to use the path variable
    public String welcome(@PathVariable String name) {
        return "mattrit experiment#1, GET request was made with http://localhost:8080/someString" +
                "<br>" + "someString = " + name; // "<br>" is used so webpage (HTML) has newline
    }


    // its a little risky to use this, since if i had
    // http://localhost:8080/users/profile then users = num1 and profile=num2
    @GetMapping("/{num1}/{num2}")
    public String sum(@PathVariable int num1, @PathVariable int num2) {

        return "mattrit experiment#1 using **ONLY** 2 path variables, no distinct method call in url<br> " +
                "Sum is: " + (num1 + num2);
    }


    // this one requires that /hardCallsum be typed, callSum is static and must, guarantees
    // that this method is used.
    @GetMapping("/sum/{num1}/{num2}")
    public String specifySum(@PathVariable int num1, @PathVariable int num2) {

        return "mattrit experiment#1 specifiying this sum method from browser, 2 path variables<br> " +
                "Sum is: " + (num1 + num2);
    }


    // allows something like http://localhost:8080/multiply?x=3&y=2
    // @RequestParam looks AFTER the "?", for the ***parameter*** variables, NOT path variables
    // order doesn't matter so much now, but parameter variables must be assigned
    // immedietly (x=5) and seperated with "&" such as x=5&y=2.
    // variable names must also match, since it requested a parameter variable x,
    // which is to be defined in the browser/url AFTER the "?"
    @GetMapping("/multiply")
    public String multiply(@RequestParam int x, @RequestParam int y) {
        return "mattrit experiement#1 multiplying using @RequestParam<br>" +
                "x = " + x +"<br>" +
                "y = " + y + "<br>" +
                x + " times " + y + " equals: " + x*y;
    }


    // standard URL layout is:
    // protocol://host:port/pathVar1/pathVarn?paramVar1=x&paramVarn=y
    // https://localhost:8080/pathVar1/pathVarn?paramVar=x&paramVar2=y
    // This includes the path variables, and param variables, but url DOES NOT have body variables
}
