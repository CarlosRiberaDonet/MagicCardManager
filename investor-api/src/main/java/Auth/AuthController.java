package Auth;

import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {


    private final UserService userService;

    public AuthController(UserService userService){

        this.userService = userService;
    }
    @PostMapping("/login")
    public String login(@RequestBody UserDTO request) {

        return userService.authUser(request);
    }
}
