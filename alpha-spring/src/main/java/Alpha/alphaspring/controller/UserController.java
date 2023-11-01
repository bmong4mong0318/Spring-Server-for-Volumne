package Alpha.alphaspring.controller;

import Alpha.alphaspring.DTO.StringResponseDto;
import Alpha.alphaspring.DTO.UserRegisterRequestDto;
import Alpha.alphaspring.DTO.UserResponseDto;
import Alpha.alphaspring.domain.User;
import Alpha.alphaspring.service.UserService;
import com.auth0.jwk.JwkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public StringResponseDto home(){
        return new StringResponseDto("Server Running!!");
    }

    @GetMapping("/session")
    public String session(ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(SecurityContextHolder.getContext().getAuthentication());
    }

    @GetMapping("/users")
    public List<User> userList(User user){
        try {
            return userService.findUsers();
        }
        catch (Exception e){
            return null;
        }
    }

    @GetMapping("/token")
    public boolean checkToken(@RequestParam("token") String token){
        System.out.println("Token : ");
        System.out.println(token);
        try {
            return userService.checkKakaoToken(token);

        } catch (ParseException | JwkException e) {
            System.out.println("error! = " + e);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public StringResponseDto registerUser(
            @RequestBody UserRegisterRequestDto requestBody
            ) throws ParseException {
        userService.join(requestBody);
        return new StringResponseDto("User Registered!!");
    }

    @GetMapping("/user")
    public UserResponseDto findUser(@RequestParam(value = "nickname") String nickname){
        try {
            return userService.findUser(nickname);
        }
        catch (Exception e){
            System.out.println("e = " + e);
            return null;
        }
    }


}
