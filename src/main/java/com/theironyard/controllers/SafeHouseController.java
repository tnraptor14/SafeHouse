package com.theironyard.controllers;


import com.theironyard.entities.House;
import com.theironyard.entities.Item;
import com.theironyard.entities.User;
import com.theironyard.services.HouseRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.AmazonUtil;
import com.theironyard.utilities.TokenUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;


@RestController
public class SafeHouseController {

    @Autowired
    private UserRepository users;
    @Autowired
    private HouseRepository houses;

    // register a new user
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody Map<String, String> json) {
        String username = json.get("username");
        String password = json.get("password");

        try {
            User user = new User(username, password); // throws IllegalArgumentException on bad UN or PW
            users.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid username or password.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Problem", HttpStatus.BAD_REQUEST);
        }
    }

    // user login
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody Map<String, String> json) {
        String username = json.get("username");
        String password = json.get("password");

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        if ((username != null && !username.isEmpty()) && (password != null && !password.isEmpty())) {
            User user = users.findOneByName(username);
            if (user != null) {
                if (user.verifyPassword(password)) {
                    return new ResponseEntity<>(TokenUtil.getJwt(username), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Bad login.", HttpStatus.UNAUTHORIZED);
    }

    // get user Todo
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public void getUser(@RequestBody Map<String, String> json) {
        System.out.println(json);
    }

    // get house Todo
    @RequestMapping(path = "/house", method = RequestMethod.GET)
    public void getHouse(@RequestBody Map<String, String> json) {
        System.out.println(json);
    }

    // add a new house Todo
    @RequestMapping(path = "/house", method = RequestMethod.POST)
    public void addHouse(@RequestBody Map<String, String> json) {
        String username = json.get("username");
        String houseName = json.get("houseName");

        User user = users.findOneByName(username);
        houses.save(new House(houseName, user));
    }

    // remove a house Todo
    @RequestMapping(path = "/house", method = RequestMethod.DELETE)
    public void deleteHouse(@RequestBody Map<String, String> json) {
        System.out.println(json);
    }

    // add item to house Todo
    @RequestMapping(path = "/item", method = RequestMethod.POST)
    public void addItem(@RequestBody Map<String, String> json) {
        String username = json.get("username");
        String houseName = json.get("houseName");
        String itemName = json.get("itemName");

        House house = houses.findOneByNameAndUser_Name(houseName, username);
        System.out.println(house.getName());
        house.addItem(new Item(itemName));
        houses.save(house);
    }

    // remove item from house Todo
    @RequestMapping(path = "/item", method = RequestMethod.DELETE)
    public void deleteItem(@RequestBody Map<String, String> json) {
        System.out.println(json);
    }

    // get items from search Todo
    @RequestMapping(path = "/items", method = RequestMethod.GET)
    public void getItems(@RequestBody Map<String, String> json) {
        System.out.println(json);
    }

    //Search Amazon Product API ToDo
    @RequestMapping(path = "/items", method = RequestMethod.POST)
    public ResponseEntity<?> searchItems(@RequestBody Map<String, String> json) throws Exception {
        String keywords = json.get("keywords");
        String category = json.get("category");
        String searchUrl = AmazonUtil.lookupItem(keywords, category);

        try {
            URL url = new URL(searchUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                JSONObject xmlJSONObj = XML.toJSONObject(strTemp);
                String jsonFormattedString = xmlJSONObj.toString(4);
                return new ResponseEntity<>(jsonFormattedString, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Problem with the search request", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(searchUrl, HttpStatus.OK);
    }
}
