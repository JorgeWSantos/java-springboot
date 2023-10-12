package br.com.jorgesantos.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jorgesantos.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

            try {
                var servletPath = request.getServletPath();

                if(!servletPath.equals("/tasks")) {
                    filterChain.doFilter(request, response);
                }
                else {
                    var auth = request.getHeader("Authorization");

                    var authEncoded = auth.substring("Basic".length()).trim();

                    byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

                    var authString = new String(authDecoded);
                    var username = authString.split(":")[0];
                    var password = authString.split(":")[1];

                    var user = this.userRepository.findByUsername(username);

                    if(user == null) {
                        response.sendError(401);
                    } else {
                        var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                        
                        if(passwordVerified.verified) {
                            request.setAttribute("idUser", user.getId());
                            filterChain.doFilter(request, response);
                            return;
                        }
            
                        response.sendError(401);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                response.sendError(404, e.getMessage());
                // TODO: handle exception
            }

            
        }
    
}
