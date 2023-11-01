package Alpha.alphaspring.service;

import Alpha.alphaspring.DTO.UserDetails;
import Alpha.alphaspring.DTO.UserRegisterRequestDto;
import Alpha.alphaspring.DTO.UserResponseDto;
import Alpha.alphaspring.Utils.CommonTokenUtils;
import Alpha.alphaspring.Utils.KakaoTokenUtils;
import Alpha.alphaspring.domain.User;
import Alpha.alphaspring.repository.UserRepository;

import com.auth0.jwk.JwkException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
@PropertySource("classpath:application.properties")
public class UserService {

    // use env to get kakao native key from application.properties
    @Autowired
    private Environment env;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonTokenUtils tokenUtils;
    @Autowired
    private KakaoTokenUtils kakaoTokenUtils;

    //
    public List<User> findUsers() throws Exception {
        if (userRepository.findAll().isEmpty()){
            throw new Exception("result set null");
        }
        return userRepository.findAll();
    }

    public void store_token(String tokens) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject tokenObj = (JSONObject) parser.parse(tokens);
        String idToken = (String) tokenObj.get("IdToken");

        String[] jwt = idToken.split("\\.");

        byte[] decodedBytes = Base64.getDecoder().decode(jwt[1]);
        String payload = new String(decodedBytes);

//        sessions.put("", idToken);
        System.out.println("payload = " + payload);
    }

    public boolean checkKakaoToken(String token) throws ParseException, JwkException {
        return kakaoTokenUtils.validate(token);
    }

    public void join(UserRegisterRequestDto request) throws ParseException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userBefore = userRepository.findByUsernameAndProvider(userDetails.getUsername(), userDetails.getProvider()).orElseThrow(() -> new RuntimeException("can not find user!"));

        User user = request.toEntity(null);
        user.setProvider(userBefore.getProvider());
        user.setUsername(userBefore.getUsername());
        user.setRefreshToken(userBefore.getRefreshToken());
        user.setId(userBefore.getId());

        userRepository.save(user);
    }

    public UserResponseDto findUser(String nickname) throws Exception {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new Exception("error find by nickname"));
        return new UserResponseDto().fromEntity(user);
    }

    public void saveToken(String username, String provider, String refreshToken) throws  Exception{
        User user = userRepository.findByUsernameAndProvider(username, provider).isPresent() ?
                userRepository.findByUsernameAndProvider(username, provider).orElseThrow(() -> new Exception("error find by nickname")) :
                User.builder()
                        .username(username)
                        .provider(provider)
                        .build();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }
}
