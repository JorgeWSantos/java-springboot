package br.com.jorgesantos.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jorgesantos.todolist.dto.ErrorResponse;
import br.com.jorgesantos.todolist.dto.SuccessResponse;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository repository;

    @PostMapping("")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        try {
            var user = this.repository.findByUsername(userModel.getUsername());

            if(user != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe!");
            }
    
            var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
    
            userModel.setPassword(passwordHashed);
    
            UserModel userCreated = this.repository.save(userModel);
    
            SuccessResponse<UserModel> response = new SuccessResponse<UserModel>();
            response.setSuccess(true);
            response.setData(userCreated);
    
            return ResponseEntity.status(HttpStatus.CREATED).body(response); 
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse();
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(response); 
        }
    }
}
